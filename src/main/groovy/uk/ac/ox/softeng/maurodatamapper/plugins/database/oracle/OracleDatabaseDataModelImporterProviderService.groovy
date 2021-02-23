/*
 * Copyright 2020 University of Oxford and Health and Social Care Information Centre, also known as NHS Digital
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

import uk.ac.ox.softeng.maurodatamapper.plugins.database.AbstractDatabaseDataModelImporterProviderService
import uk.ac.ox.softeng.maurodatamapper.plugins.database.RemoteDatabaseDataModelImporterProviderService

import java.sql.Connection
import java.sql.PreparedStatement

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

    @Override
    PreparedStatement prepareCoreStatement(Connection connection, OracleDatabaseDataModelImporterProviderServiceParameters parameters) {
        connection.prepareStatement(databaseStructureQueryString).tap {setString 1, parameters.databaseOwner}
    }
}
