package uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle

import uk.ac.ox.softeng.maurodatamapper.provider.plugin.AbstractMauroDataMapperPlugin

class OracleDatabasePlugin extends AbstractMauroDataMapperPlugin {

    @Override
    String getName() {
        'Plugin : Database - Oracle'
    }

    @Override
    Closure doWithSpring() {
        {->
            oracleDatabaseDataModelImporterProviderService OracleDatabaseDataModelImporterProviderService
            oracleDataTypeProvider OracleDataTypeProvider
        }
    }
}
