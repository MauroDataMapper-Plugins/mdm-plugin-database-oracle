/*
 * Copyright 2020-2021 University of Oxford and Health and Social Care Information Centre, also known as NHS Digital
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle

import uk.ac.ox.softeng.maurodatamapper.datamodel.item.datatype.DataType
import uk.ac.ox.softeng.maurodatamapper.plugins.database.AbstractDatabaseDataModelImporterProviderService
import uk.ac.ox.softeng.maurodatamapper.plugins.database.RemoteDatabaseDataModelImporterProviderService
import uk.ac.ox.softeng.maurodatamapper.plugins.database.summarymetadata.AbstractIntervalHelper

import java.sql.Connection
import java.sql.PreparedStatement
import java.time.format.DateTimeFormatter

// @CompileStatic
class OracleDatabaseDataModelImporterProviderService
    extends AbstractDatabaseDataModelImporterProviderService<OracleDatabaseDataModelImporterProviderServiceParameters>
    implements RemoteDatabaseDataModelImporterProviderService {

    @Override
    String getDisplayName() {
        'Oracle DB Importer'
    }

    @Override
    String getVersion() {
        getClass().getPackage().getSpecificationVersion() ?: 'SNAPSHOT'
    }

    @Override
    String getSchemaNameColumnName() {
        'owner'
    }

    @Override
    String getColumnIsNullableColumnName() {
        'nullable'
    }

    @Override
    String getStandardConstraintInformationQueryString() {
        '''
        SELECT
          TABLE_NAME,
          SEARCH_CONDITION AS check_clause
        FROM SYS.ALL_CONSTRAINTS
        WHERE OWNER = ?
              AND CONSTRAINT_TYPE = 'C'
              AND SEARCH_CONDITION IS NOT NULL
        '''.stripIndent()
    }

    @Override
    String getPrimaryKeyAndUniqueConstraintInformationQueryString() {
        '''
        SELECT
          ac.CONSTRAINT_NAME,
          ac.TABLE_NAME,
          CASE ac.CONSTRAINT_TYPE
          WHEN 'P'
            THEN 'primary_key'
          ELSE 'unique'
          END          AS CONSTRAINT_TYPE,
          acc.COLUMN_NAME,
          acc.POSITION AS ORDINAL_POSITION
        FROM SYS.ALL_CONSTRAINTS ac
          INNER JOIN SYS.ALL_CONS_COLUMNS acc ON ac.CONSTRAINT_NAME = acc.CONSTRAINT_NAME
        WHERE ac.OWNER = ?
              AND
              CONSTRAINT_TYPE IN ('P', 'U')
        '''.stripIndent()
    }

    @Override
    String getIndexInformationQueryString() {
        '''
        SELECT
          ix.TABLE_NAME,
          ix.INDEX_NAME,
          CASE ix.UNIQUENESS
          WHEN 'UNIQUE'
            THEN 1
          ELSE 0
          END AS                      UNIQUE_INDEX,
          CASE pc.CONSTRAINT_TYPE
          WHEN 'P'
            THEN 1
          ELSE 0
          END AS                      PRIMARY_INDEX,
          LISTAGG(cols.COLUMN_NAME, ', ')
          WITHIN GROUP (
            ORDER BY COLUMN_POSITION) "COLUMN_NAMES"
        FROM SYS.ALL_INDEXES ix
          LEFT JOIN SYS.ALL_CONSTRAINTS pc ON ix.INDEX_NAME = pc.INDEX_NAME
          LEFT JOIN SYS.ALL_IND_COLUMNS cols ON ix.INDEX_NAME = cols.INDEX_NAME
        WHERE ix.OWNER = ?
        GROUP BY ix.TABLE_NAME, ix.INDEX_NAME, ix.UNIQUENESS, pc.CONSTRAINT_TYPE
        '''.stripIndent()
    }

    @Override
    String getForeignKeyInformationQueryString() {
        '''
        SELECT
          ac.CONSTRAINT_NAME,
          ac.TABLE_NAME,
          acc.COLUMN_NAME,
          rac.TABLE_NAME   AS REFERENCE_TABLE_NAME,
          racc.COLUMN_NAME AS REFERENCE_COLUMN_NAME
        FROM SYS.ALL_CONSTRAINTS ac
          INNER JOIN SYS.ALL_CONS_COLUMNS acc ON ac.CONSTRAINT_NAME = acc.CONSTRAINT_NAME
          INNER JOIN SYS.ALL_CONSTRAINTS rac ON ac.R_CONSTRAINT_NAME = rac.CONSTRAINT_NAME
          INNER JOIN SYS.ALL_CONS_COLUMNS racc ON rac.CONSTRAINT_NAME = racc.CONSTRAINT_NAME
        WHERE ac.OWNER = ?
              AND
              ac.CONSTRAINT_TYPE = 'R'
        '''.stripIndent()
    }

    @Override
    String getDatabaseStructureQueryString() {
        'SELECT * FROM SYS.ALL_TAB_COLUMNS WHERE OWNER = ?'
    }

    @Override
    boolean isColumnNullable(String nullableColumnValue) {
        nullableColumnValue == 'Y'
    }

    @Override
    Boolean canImportMultipleDomains() {
        false
    }

    /**
     * Oracle identifiers escaped in double quotes. Identifiers must be in upper case for Oracle.
     */
    @Override
    String escapeIdentifier(String identifier) {
        "\"${identifier.toUpperCase()}\""
    }

    @Override
    boolean isColumnPossibleEnumeration(DataType dataType) {
        dataType.domainType == 'PrimitiveType' && (dataType.label == "CHAR" || dataType.label == "VARCHAR2")
    }

    @Override
    boolean isColumnForDateSummary(DataType dataType) {
        ["date", "smalldatetime", "datetime", "datetime2"].contains(dataType.label)
    }

    @Override
    boolean isColumnForDecimalSummary(DataType dataType) {
        ["decimal", "numeric"].contains(dataType.label)
    }

    @Override
    boolean isColumnForIntegerSummary(DataType dataType) {
        ["tinyint", "smallint", "int", "bigint"].contains(dataType.label)
    }

    @Override
    String minMaxColumnValuesQueryString(String tableName, String columnName) {
        "SELECT MIN(\"${columnName.toUpperCase()}\") AS min_value, MAX(\"${columnName.toUpperCase()}\") AS max_value FROM \"${tableName.toUpperCase()}\";"
    }

    @Override
    String columnRangeDistributionQueryString(String tableName, String columnName, DataType dataType, AbstractIntervalHelper intervalHelper) {
        List<String> selects = intervalHelper.intervals.collect {
            "SELECT '${it.key}' AS interval_label, ${formatDataType(dataType, it.value.aValue)} AS interval_start, ${formatDataType(dataType, it.value.bValue)} AS interval_end"
        }

        rangeDistributionQueryString(tableName, columnName, selects)
    }

    /**
     * Return a string which uses the SQL Server CONVERT function for Dates, otherwise string formatting
     *
     * @param dataType
     * @param value
     * @return Date formatted as ISO8601 (see
     * https://docs.microsoft.com/en-us/sql/t-sql/functions/cast-and-convert-transact-sql?view=sql-server-ver15)
     * or a string
     */
    String formatDataType(DataType dataType, Object value) {
        if (isColumnForDateSummary(dataType)){
            "CONVERT(DATETIME, '${DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value)}', 126)"
        } else {
            "${value}"
        }
    }

    /**
     * Returns a String that looks, for example, like this:
     * WITH #interval AS (
     *   SELECT '0 - 100' AS interval_label, 0 AS interval_start, 100 AS interval_end
     *   UNION
     *   SELECT '100 - 200' AS interval_label, 100 AS interval_start, 200 AS interval_end
     * )
     * SELECT interval_label, COUNT([my_column]) AS interval_count
     * FROM #interval
     * LEFT JOIN
     * [my_table] ON [my_table].[my_column] >= #interval.interval_start AND [my_table].[my_column] < #interval.interval_end
     * GROUP BY interval_label, interval_start
     * ORDER BY interval_start ASC;
     *
     * @param tableName
     * @param columnName
     * @param selects
     * @return
     */
    private String rangeDistributionQueryString(String tableName, String columnName, List<String> selects) {
        String intervals = selects.join(" UNION ")

        String sql = "WITH #interval AS (${intervals})" +
                """
        SELECT interval_label, COUNT([${columnName}]) AS interval_count
        FROM #interval
        LEFT JOIN
        [${tableName}] ON [${tableName}].[${columnName}] >= #interval.interval_start AND [${tableName}].[${columnName}] < #interval.interval_end
        GROUP BY interval_label, interval_start
        ORDER BY interval_start ASC;
        """

        sql.stripIndent()
    }


    @Override
    PreparedStatement prepareCoreStatement(Connection connection, OracleDatabaseDataModelImporterProviderServiceParameters parameters) {
        connection.prepareStatement(databaseStructureQueryString).tap {setString 1, parameters.databaseOwner}
    }
}
