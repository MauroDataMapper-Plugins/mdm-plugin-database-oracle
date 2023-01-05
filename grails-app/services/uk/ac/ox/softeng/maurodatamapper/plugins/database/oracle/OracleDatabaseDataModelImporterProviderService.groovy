/*
 * Copyright 2020-2023 University of Oxford and Health and Social Care Information Centre, also known as NHS Digital
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

import uk.ac.ox.softeng.maurodatamapper.datamodel.provider.DefaultDataTypeProvider
import uk.ac.ox.softeng.maurodatamapper.plugins.database.AbstractDatabaseDataModelImporterProviderService
import uk.ac.ox.softeng.maurodatamapper.plugins.database.RemoteDatabaseDataModelImporterProviderService
import uk.ac.ox.softeng.maurodatamapper.plugins.database.calculation.CalculationStrategy
import uk.ac.ox.softeng.maurodatamapper.plugins.database.calculation.SamplingStrategy
import uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle.calculation.OracleCalculationStrategy
import uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle.calculation.OracleSamplingStrategy
import uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle.parameters.OracleDatabaseDataModelImporterProviderServiceParameters
import uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle.query.OracleQueryStringProvider
import uk.ac.ox.softeng.maurodatamapper.plugins.database.query.QueryStringProvider

import org.springframework.beans.factory.annotation.Autowired

import java.sql.Connection
import java.sql.PreparedStatement

// @CompileStatic
class OracleDatabaseDataModelImporterProviderService
    extends AbstractDatabaseDataModelImporterProviderService<OracleDatabaseDataModelImporterProviderServiceParameters>
    implements RemoteDatabaseDataModelImporterProviderService {

    @Autowired
    OracleDataTypeProviderService oracleDataTypeProviderService

    @Override
    String getDisplayName() {
        'Oracle DB Importer'
    }

    @Override
    String getVersion() {
        getClass().getPackage().getSpecificationVersion() ?: 'SNAPSHOT'
    }

    @Override
    Boolean handlesContentType(String contentType) {
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
    Boolean canImportMultipleDomains() {
        false
    }

    @Override
    DefaultDataTypeProvider getDefaultDataTypeProvider() {
        oracleDataTypeProviderService
    }

    @Override
    QueryStringProvider createQueryStringProvider() {
        return new OracleQueryStringProvider()
    }

    @Override
    SamplingStrategy createSamplingStrategy(String schema, String table, OracleDatabaseDataModelImporterProviderServiceParameters parameters) {
        new OracleSamplingStrategy(schema, table, parameters)
    }

    @Override
    CalculationStrategy createCalculationStrategy(OracleDatabaseDataModelImporterProviderServiceParameters parameters) {
        new OracleCalculationStrategy(parameters)
    }

    @Override
    PreparedStatement prepareCoreStatement(Connection connection, OracleDatabaseDataModelImporterProviderServiceParameters parameters) {
        connection.prepareStatement(queryStringProvider.databaseStructureQueryString).tap {setString 1, parameters.databaseOwner}
    }

    @Override
    boolean isColumnNullable(String nullableColumnValue) {
        nullableColumnValue == 'Y'
    }
}
