package uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle

import uk.ac.ox.softeng.maurodatamapper.plugins.database.AbstractDatabaseDataModelImporterProviderService
import uk.ac.ox.softeng.maurodatamapper.plugins.database.RemoteDatabaseDataModelImporterProviderService

import java.sql.Connection
import java.sql.PreparedStatement

class OracleDatabaseDataModelImporterProviderService
    extends AbstractDatabaseDataModelImporterProviderService<OracleDatabaseDataModelImporterProviderServiceParameters>
    implements RemoteDatabaseDataModelImporterProviderService {

    @Override
    Boolean canImportMultipleDomains() {
        false
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
    String getDatabaseStructureQueryString() {
        'SELECT * FROM SYS.ALL_TAB_COLUMNS WHERE OWNER = ?'
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
    String getDisplayName() {
        'Oracle DB Importer'
    }

    @Override
    String getVersion() {
        '3.0.0-SNAPSHOT'
    }

    @Override
    PreparedStatement prepareCoreStatement(Connection connection, OracleDatabaseDataModelImporterProviderServiceParameters params) {
        PreparedStatement st = connection.prepareStatement(getDatabaseStructureQueryString())
        st.setString(1, params.databaseOwner)
        st
    }

    @Override
    boolean isColumnNullable(String nullableColumnValue) {
        nullableColumnValue == 'Y'
    }
}
