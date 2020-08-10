package uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle

import uk.ac.ox.softeng.maurodatamapper.provider.plugin.AbstractMauroDataMapperPlugin

/**
 * @since 17/08/2017
 */
class OracleDatabasePlugin extends AbstractMauroDataMapperPlugin {
    @Override
    String getName() {
        return "Plugin:Database - Oracle"
    }

    @Override
    Closure doWithSpring() {
        {->
            oracleDatabaseDataModelImporterProviderService(OracleDatabaseDataModelImporterProviderService)
            oracleDataTypeProvider(OracleDataTypeProvider)
        }
    }
}
