package ox.softeng.metadatacatalogue.plugins.database.oracle

import ox.softeng.metadatacatalogue.core.spi.module.AbstractModule

/**
 * @since 17/08/2017
 */
class PluginDatabaseOracleModule extends AbstractModule {
    @Override
    String getName() {
        return "Plugin:Database - Oracle"
    }

    @Override
    Closure doWithSpring() {
        {->
            oracleDatabaseImporterService(OracleDatabaseImporterService)
            oracleDefaultDataTypeProvider(OracleDefaultDataTypeProvider)
        }
    }
}
