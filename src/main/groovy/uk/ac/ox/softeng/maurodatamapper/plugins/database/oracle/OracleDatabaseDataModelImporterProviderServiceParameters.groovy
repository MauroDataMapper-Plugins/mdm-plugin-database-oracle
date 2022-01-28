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

import uk.ac.ox.softeng.maurodatamapper.core.provider.importer.parameter.config.ImportGroupConfig
import uk.ac.ox.softeng.maurodatamapper.core.provider.importer.parameter.config.ImportParameterConfig
import uk.ac.ox.softeng.maurodatamapper.plugins.database.DatabaseDataModelWithSamplingImporterProviderServiceParameters

import groovy.util.logging.Slf4j
import oracle.jdbc.pool.OracleDataSource

import java.sql.SQLException

@Slf4j
// @CompileStatic
class OracleDatabaseDataModelImporterProviderServiceParameters extends
        DatabaseDataModelWithSamplingImporterProviderServiceParameters<OracleDataSource> {

    @ImportParameterConfig(
        displayName = 'Database Server',
        description = 'The name of the database server to connect to.',
        order = 1,
        group = @ImportGroupConfig(
            name = 'Database',
            order = 1
        ))
    String databaseNames

    @ImportParameterConfig(
        displayName = 'Database Name/Owner',
        description = 'The name of the database/owner which is to be imported.',
        order = 1,
        group = @ImportGroupConfig(
            name = 'Database',
            order = 1
        ))
    String databaseOwner

    @Override
    String getModelName() {
        super.modelName ?: databaseOwner
    }

    @Override
    boolean isMultipleDataModelImport() {
        databaseNames?.contains ','
    }

    @Override
    void populateFromProperties(Properties properties) {
        super.populateFromProperties properties
        databaseOwner = properties.getProperty 'import.database.owner'
    }

    @Override
    OracleDataSource getDataSource(String databaseName) throws SQLException {
        // Just to make sure all the correct fields are set we use the URL parsing (this is how Oracle examples do it)
        final OracleDataSource dataSource = new OracleDataSource().tap {setURL getUrl(databaseName)}
        log.info 'DataSource connection url: {}', dataSource.getURL()
        dataSource
    }

    @Override
    String getUrl(String databaseName) {
        "jdbc:oracle:thin:@${databaseHost}:${databasePort}/${databaseName}"
    }

    @Override
    String getDatabaseDialect() {
        'Oracle DB'
    }

    @Override
    int getDefaultPort() {
        1521
    }
}
