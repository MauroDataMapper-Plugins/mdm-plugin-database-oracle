package uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle

import uk.ac.ox.softeng.maurodatamapper.core.provider.importer.parameter.config.ImportGroupConfig
import uk.ac.ox.softeng.maurodatamapper.core.provider.importer.parameter.config.ImportParameterConfig
import uk.ac.ox.softeng.maurodatamapper.plugins.database.DatabaseDataModelImporterProviderServiceParameters

import oracle.jdbc.pool.OracleDataSource

import java.sql.SQLException;

/**
 * Created by james on 31/05/2017.
 */
public class OracleDatabaseDataModelImporterProviderServiceParameters extends DatabaseDataModelImporterProviderServiceParameters<OracleDataSource> {

    @ImportParameterConfig(
        displayName = "Database Server",
        description = "The name of the database server to connect to.",
        order = 1,
        group = @ImportGroupConfig(
            name = "Database",
            order = 1
        )
    )
    private String databaseNames;

    @ImportParameterConfig(
        displayName = "Database Name/Owner",
        description = "The name of the database/owner which is to be imported.",
        order = 1,
        group = @ImportGroupConfig(
            name = "Database",
            order = 1
        )
    )
    private String databaseOwner;

    @Override
    public int getDefaultPort() {
        return 1521;
    }

    @Override
    public String getDatabaseDialect() {
        return "Oracle DB";
    }

    @Override
    public String getUrl(String databaseName) {
        return "jdbc:oracle:thin:@" + getDatabaseHost() + ":" + getDatabasePort() + "/" + databaseName;
    }

    @Override
    public OracleDataSource getDataSource(String databaseName) throws SQLException {
        OracleDataSource dataSource = new OracleDataSource();
        // Just to make sure all the correct fields are set we use the URL parsing (this is how Oracle examples do it)
        dataSource.setURL(getUrl(databaseName));
        getLogger().info("DataSource connection url: {}", dataSource.getURL());
        return dataSource;
    }

    @Override
    public String getDataModelName() {
        return super.getDataModelName() != null ? super.getDataModelName() : getDatabaseOwner();
    }

    public String getDatabaseOwner() {
        return databaseOwner;
    }

    public void setDatabaseOwner(String databaseOwner) {
        this.databaseOwner = databaseOwner;
    }

    @Override
    public void populateFromProperties(Properties properties) {
        super.populateFromProperties(properties);
        databaseOwner = properties.getProperty("import.database.owner");
    }
}
