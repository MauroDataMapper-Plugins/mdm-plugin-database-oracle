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
package uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle.calculation

import uk.ac.ox.softeng.maurodatamapper.plugins.database.DatabaseDataModelWithSamplingImporterProviderServiceParameters
import uk.ac.ox.softeng.maurodatamapper.plugins.database.calculation.SamplingStrategy

class OracleSamplingStrategy extends SamplingStrategy {

    /**
     * Oracle can sample views as well as tables, so we don't need to check the table type
     * @return
     */
    OracleSamplingStrategy(String schema, String table,
                           DatabaseDataModelWithSamplingImporterProviderServiceParameters samplingImporterProviderServiceParameters) {
        super(schema, table, samplingImporterProviderServiceParameters)
    }

    @Override
    boolean requiresTableType() {
        false
    }

    /**
     * Oracle can sample views as well as tables, so we don't need to check the table type
     * @return true
     */
    @Override
    boolean canSampleTableType() {
        true
    }

    /**
     * Return a sampling clause. Subclasses should override wth vendor specific sampling clauses
     * @return
     */
    @Override
    String samplingClause(Type type) {
        BigDecimal percentage
        switch (type) {
            case Type.SUMMARY_METADATA:
                percentage = getSummaryMetadataSamplePercentage()
                break
            case Type.ENUMERATION_VALUES:
                percentage = getEnumerationValueSamplePercentage()
                break
        }
        this.useSamplingFor(type) ? " SAMPLE (${percentage})" : ''
    }
}
