/*
 * Copyright 2020-2022 University of Oxford and Health and Social Care Information Centre, also known as NHS Digital
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
import uk.ac.ox.softeng.maurodatamapper.datamodel.provider.DefaultDataTypeProvider
import uk.ac.ox.softeng.maurodatamapper.datamodel.summarymetadata.AbstractIntervalHelper
import uk.ac.ox.softeng.maurodatamapper.plugins.database.AbstractDatabaseDataModelImporterProviderService
import uk.ac.ox.softeng.maurodatamapper.plugins.database.RemoteDatabaseDataModelImporterProviderService
import uk.ac.ox.softeng.maurodatamapper.plugins.database.SamplingStrategy

import org.springframework.beans.factory.annotation.Autowired

import java.sql.Connection
import java.sql.PreparedStatement
import java.time.format.DateTimeFormatter

// @CompileStatic
class OracleDatabaseDataModelImporterProviderService
    extends AbstractDatabaseDataModelImporterProviderService<OracleDatabaseDataModelImporterProviderServiceParameters>
    implements RemoteDatabaseDataModelImporterProviderService {

    @Autowired
    OracleDataTypeProvider oracleDataTypeProvider

    @Override
    SamplingStrategy getSamplingStrategy(OracleDatabaseDataModelImporterProviderServiceParameters parameters) {
        new OracleSamplingStrategy(parameters.sampleThreshold ?: DEFAULT_SAMPLE_THRESHOLD, parameters.samplePercent ?: DEFAULT_SAMPLE_PERCENTAGE)
    }

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
    DefaultDataTypeProvider getDefaultDataTypeProvider() {
        oracleDataTypeProvider
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
    List<String> approxCountQueryString(String tableName, String schemaName = null) {
        List<String> queryStrings = super.approxCountQueryString(tableName, schemaName)
        queryStrings.push("SELECT NUM_ROWS AS APPROX_COUNT FROM ALL_TABLES WHERE TABLE_NAME = '${tableName}'".toString())
        queryStrings
    }

    @Override
    boolean isColumnPossibleEnumeration(DataType dataType) {
        dataType.domainType == 'PrimitiveType' && (dataType.label == "CHAR" || dataType.label == "VARCHAR2")
    }

    @Override
    boolean isColumnForDateSummary(DataType dataType) {
        dataType.domainType == 'PrimitiveType' && ["DATE"].contains(dataType.label)
    }

    /**
     * Int and decimals all have datatype NUMBER
     * @param dataType
     * @return
     */
    @Override
    boolean isColumnForDecimalSummary(DataType dataType) {
        dataType.domainType == 'PrimitiveType' && ["NUMBER"].contains(dataType.label)
    }

    /**
     * INT has a datatype of NUMBER so will be handled using the Decimal helper
     * @param dataType
     * @return
     */
    @Override
    boolean isColumnForIntegerSummary(DataType dataType) {
        false
    }
    String columnRangeDistributionQueryString(DataType dataType,
                                              AbstractIntervalHelper intervalHelper,
                                              String columnName, String tableName, String schemaName) {
        SamplingStrategy samplingStrategy = new SamplingStrategy()
        columnRangeDistributionQueryString(samplingStrategy, dataType, intervalHelper, columnName, tableName, schemaName)
    }

    @Override
    String columnRangeDistributionQueryString(SamplingStrategy samplingStrategy, DataType dataType, AbstractIntervalHelper intervalHelper, String columnName, String tableName, String schemaName) {
        List<String> selects = intervalHelper.intervals.collect {
            "SELECT '${it.key}' AS interval_label, ${formatDataType(dataType, it.value.aValue)} AS interval_start, ${formatDataType(dataType, it.value.bValue)} AS interval_end FROM DUAL "
        }

        rangeDistributionQueryString(samplingStrategy, selects, columnName, tableName, schemaName)
    }

    /**
     * If dataType represents a date then return a string which uses the Oracle TO_DATE function to
     * convert the value to a date, otherwise just return the value as a string
     *
     * @param dataType
     * @param value
     * @return fragment of query like TO_DATE('2020-08-15 23:18:00', 'YYYY-MM-DD HH24:MI:SS')
     * or a string
     */
    String formatDataType(DataType dataType, Object value) {
        if (isColumnForDateSummary(dataType)){
            "TO_DATE('${DateTimeFormatter.ISO_LOCAL_DATE.format(value)} ${DateTimeFormatter.ISO_LOCAL_TIME.format(value)}', 'YYYY-MM-DD HH24:MI:SS')"
        } else {
            "${value}"
        }
    }

    /**
     * Returns a String that looks, for example, like this:
     * WITH interval AS (
     *   SELECT '0 - 100' AS interval_label, 0 AS interval_start, 100 AS interval_end FROM DUAL
     *   UNION
     *   SELECT '100 - 200' AS interval_label, 100 AS interval_start, 200 AS interval_end FROM DUAL
     * )
     * SELECT interval_label, COUNT("MY_COLUMN") AS interval_count
     * FROM interval
     * LEFT JOIN
     * "MY_SCHEMA"."MY_TABLE" ON "MY_SCHEMA"."MY_TABLE"."MY_COLUMN" >= interval.interval_start
     * AND "MY_SCHEMA"."MY_TABLE"."MY_COLUMN" < interval.interval_end
     * GROUP BY interval_label, interval_start
     * ORDER BY interval_start ASC;
     *
     * @param schemaName
     * @param tableName
     * @param columnName
     * @param selects
     * @return Query string for intervals, using Oracle SQL
     */
    private String rangeDistributionQueryString(SamplingStrategy samplingStrategy, List<String> selects, String columnName, String tableName, String schemaName) {
        String intervals = selects.join(" UNION ")

        String sql = "WITH interval AS (${intervals})" +
                """
        SELECT interval_label, COUNT(${escapeIdentifier(columnName)}) AS interval_count
        FROM interval
        LEFT JOIN
        ${escapeIdentifier(schemaName)}.${escapeIdentifier(tableName)} 
        ${samplingStrategy.samplingClause()}
        ON ${escapeIdentifier(schemaName)}.${escapeIdentifier(tableName)}.${escapeIdentifier(columnName)} >= interval.interval_start 
        AND ${escapeIdentifier(schemaName)}.${escapeIdentifier(tableName)}.${escapeIdentifier(columnName)} < interval.interval_end
        GROUP BY interval_label, interval_start
        ORDER BY interval_start ASC
        """

        sql.stripIndent()
    }

    /**
     * Use IS NOT NULL rather than <> ''
     */
    @Override
    String countDistinctColumnValuesQueryString(SamplingStrategy samplingStrategy, String columnName, String tableName, String schemaName = null) {
        String schemaIdentifier = schemaName ? "${escapeIdentifier(schemaName)}." : ""
        "SELECT COUNT(DISTINCT(${escapeIdentifier(columnName)})) AS count FROM ${schemaIdentifier}${escapeIdentifier(tableName)}" +
                samplingStrategy.samplingClause() +
                "WHERE ${escapeIdentifier(columnName)} IS NOT NULL"
    }

    /**
     * Use IS NOT NULL rather than <> ''
     */
    @Override
    String distinctColumnValuesQueryString(SamplingStrategy samplingStrategy, String columnName, String tableName, String schemaName = null) {
        String schemaIdentifier = schemaName ? "${escapeIdentifier(schemaName)}." : ""
        "SELECT DISTINCT(${escapeIdentifier(columnName)}) AS distinct_value FROM ${schemaIdentifier}${escapeIdentifier(tableName)}" +
                samplingStrategy.samplingClause() +
                "WHERE ${escapeIdentifier(columnName)} IS NOT NULL"
    }

    @Override
    PreparedStatement prepareCoreStatement(Connection connection, OracleDatabaseDataModelImporterProviderServiceParameters parameters) {
        connection.prepareStatement(databaseStructureQueryString).tap {setString 1, parameters.databaseOwner}
    }
}
