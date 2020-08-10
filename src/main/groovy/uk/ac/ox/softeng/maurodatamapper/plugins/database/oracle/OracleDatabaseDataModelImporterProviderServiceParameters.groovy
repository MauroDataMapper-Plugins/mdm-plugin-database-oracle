package uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle

import uk.ac.ox.softeng.maurodatamapper.core.provider.importer.parameter.config.ImportGroupConfig
import uk.ac.ox.softeng.maurodatamapper.core.provider.importer.parameter.config.ImportParameterConfig
import uk.ac.ox.softeng.maurodatamapper.plugins.database.DatabaseDataModelImporterProviderServiceParameters

import oracle.jdbc.pool.OracleDataSource

import java.sql.SQLException

class OracleDatabaseDataModelImporterProviderServiceParameters extends DatabaseDataModelImporterProviderServiceParameters<OracleDataSource> {

    @ImportParameterConfig(
        displayName = 'Database Server',
        description = 'The name of the database server to connect to.',
        order = 1,
        group = @ImportGroupConfig(
            name = 'Database',
            order = 1
        ))
    private String databaseNames

    @ImportParameterConfig(
        displayName = 'Database Name/Owner',
        description = 'The name of the database/owner which is to be imported.',
        order = 1,
        group = @ImportGroupConfig(
            name = 'Database',
            order = 1
        ))
    private String databaseOwner

    @Override
    int getDefaultPort() {
        1521
    }

    @Override
    String getDatabaseDialect() {
        'Oracle DB'
    }

    @Override
    String getUrl(String databaseName) {
        'jdbc:oracle:thin:@' + getDatabaseHost() + ':' + getDatabasePort() + '/' + databaseName
    }

    @Override
    OracleDataSource getDataSource(String databaseName) throws SQLException {
        OracleDataSource dataSource = new OracleDataSource()
        // Just to make sure all the correct fields are set we use the URL parsing (this is how Oracle examples do it)
        dataSource.setURL getUrl(databaseName)
        getLogger().info 'DataSource connection url: {}', dataSource.getURL()
        dataSource
    }

    String getDataModelName() {
        super.getDataModelName() != null ? super.getDataModelName() : getDatabaseOwner()
    }

    String getDatabaseOwner() {
        databaseOwner
    }

    void setDatabaseOwner(String databaseOwner) {
        this.databaseOwner = databaseOwner
    }

    @Override
    void populateFromProperties(Properties properties) {
        super.populateFromProperties properties
        databaseOwner = properties.getProperty 'import.database.owner'
    }
}
