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

import uk.ac.ox.softeng.maurodatamapper.datamodel.item.datatype.DataType
import uk.ac.ox.softeng.maurodatamapper.plugins.database.DatabaseDataModelImporterProviderServiceParameters
import uk.ac.ox.softeng.maurodatamapper.plugins.database.calculation.CalculationStrategy

/**
 * @since 03/05/2022
 */
class OracleCalculationStrategy extends CalculationStrategy {

    OracleCalculationStrategy(DatabaseDataModelImporterProviderServiceParameters parameters) {
        super(parameters)
    }

    @Override
    boolean isColumnPossibleEnumeration(DataType dataType) {
        dataType.domainType == 'PrimitiveType' && (dataType.label == "CHAR" || dataType.label == "VARCHAR2")
    }

    @Override
    boolean isColumnForDateSummary(DataType dataType) {
        dataType.domainType == 'PrimitiveType' && ["DATE"].contains(dataType.label)
    }

    /**
     * Int and decimals all have datatype NUMBER
     * @param dataType
     * @return
     */
    @Override
    boolean isColumnForDecimalSummary(DataType dataType) {
        dataType.domainType == 'PrimitiveType' && ["NUMBER"].contains(dataType.label)
    }

    /**
     * INT has a datatype of NUMBER so will be handled using the Decimal helper
     * @param dataType
     * @return
     */
    @Override
    boolean isColumnForIntegerSummary(DataType dataType) {
        false
    }
}
