/*
 * Copyright 2020-2021 University of Oxford and Health and Social Care Information Centre, also known as NHS Digital
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

import uk.ac.ox.softeng.maurodatamapper.datamodel.DataModel
import uk.ac.ox.softeng.maurodatamapper.datamodel.item.DataClass
import uk.ac.ox.softeng.maurodatamapper.datamodel.item.DataElement
import uk.ac.ox.softeng.maurodatamapper.datamodel.item.datatype.EnumerationType
import uk.ac.ox.softeng.maurodatamapper.plugins.testing.utils.BaseDatabasePluginTest

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue

// @CompileStatic
@Slf4j
class OracleDatabaseDataModelImporterProviderServiceTest
    extends BaseDatabasePluginTest<OracleDatabaseDataModelImporterProviderServiceParameters, OracleDatabaseDataModelImporterProviderService> {

    @Override
    String getDatabasePortPropertyName() {
        'oracle.database.port'
    }

    @Override
    int getDefaultDatabasePort() {
        1521
    }

    @Override
    OracleDatabaseDataModelImporterProviderServiceParameters createDatabaseImportParameters() {
        new OracleDatabaseDataModelImporterProviderServiceParameters().tap {
            databaseNames = 'ORCLPDB1'
            databaseUsername = 'SYSTEM'
            databasePassword = 'BOpVnzFi9Ew=1'
            databaseOwner = 'SYSTEM'
        }
    }

    @Test
    void testImportSimpleDatabase() {
        final DataModel dataModel = importDataModelAndRetrieveFromDatabase(
            createDatabaseImportParameters(databaseHost, databasePort).tap {databaseOwner = 'METADATA_SIMPLE'})

        checkBasic(dataModel)
        checkOrganisationNotEnumerated(dataModel)
        checkSampleNoSummaryMetadata(dataModel)
        checkBiggerSampleNoSummaryMetadata(dataModel)

        List<String> defaultDataTypeLabels = importerInstance.defaultDataTypeProvider.defaultListOfDataTypes.collect {it.label}
        assertEquals 'Default DT Provider', 29, defaultDataTypeLabels.size()
        log.warn '{}', dataModel.primitiveTypes.findAll {!(it.label in defaultDataTypeLabels)}
        assertEquals 'Number of columntypes/datatypes', 31, dataModel.dataTypes?.size()
        assertTrue 'All primitive DTs map to a default DT', dataModel.primitiveTypes.findAll {!(it.label in defaultDataTypeLabels)}.isEmpty()
        assertEquals 'Number of primitive types', 29, dataModel.dataTypes.findAll {it.domainType == 'PrimitiveType'}.size()
        assertEquals 'Number of reference types', 2, dataModel.dataTypes.findAll {it.domainType == 'ReferenceType'}.size()
        assertEquals 'Number of child tables/dataclasses', 1, dataModel.childDataClasses?.size()


    }

    @Test
    void testImportSimpleDatabaseWithEnumerations() {
        final DataModel dataModel = importDataModelAndRetrieveFromDatabase(
                createDatabaseImportParameters(databaseHost, databasePort).tap {databaseOwner = 'METADATA_SIMPLE'
                    detectEnumerations = true
                    maxEnumerations = 20})

        checkBasic(dataModel)
        checkOrganisationEnumerated(dataModel)
        checkSampleNoSummaryMetadata(dataModel)
        checkBiggerSampleNoSummaryMetadata(dataModel)

        List<String> defaultDataTypeLabels = importerInstance.defaultDataTypeProvider.defaultListOfDataTypes.collect {it.label}
        assertEquals 'Default DT Provider', 29, defaultDataTypeLabels.size()

        assertEquals 'Number of columntypes/datatypes', 31, dataModel.dataTypes?.size()
        assertTrue 'All primitive DTs map to a default DT', dataModel.primitiveTypes.findAll {!(it.label in defaultDataTypeLabels)}.isEmpty()
        assertEquals 'Number of primitive types', 29, dataModel.dataTypes.findAll {it.domainType == 'PrimitiveType'}.size()
        assertEquals 'Number of reference types', 2, dataModel.dataTypes.findAll {it.domainType == 'ReferenceType'}.size()
        assertEquals 'Number of child tables/dataclasses', 1, dataModel.childDataClasses?.size()
    }

    @Test
    void 'testImportSimpleDatabaseWithSummaryMetadata'() {
        final DataModel dataModel = importDataModelAndRetrieveFromDatabase(
                createDatabaseImportParameters(databaseHost, databasePort).tap {
                    databaseOwner = 'METADATA_SIMPLE'
                    detectEnumerations = true
                    maxEnumerations = 20
                    calculateSummaryMetadata = true})

        checkBasic(dataModel)
        checkOrganisationEnumerated(dataModel)
        checkSampleSummaryMetadata(dataModel)
        checkBiggerSampleSummaryMetadata(dataModel)
        checkBiggerSampleViewSummaryMetadata(dataModel)

    }

    @Test
    void 'testImportSimpleDatabaseWithSummaryMetadataWithSampling'() {
        final DataModel dataModel = importDataModelAndRetrieveFromDatabase(
            createDatabaseImportParameters(databaseHost, databasePort).tap {
                databaseOwner = 'METADATA_SIMPLE'
                detectEnumerations = true
                maxEnumerations = 20
                calculateSummaryMetadata = true
                sampleThreshold = 1000
                samplePercent = 10
            }
        )

        checkBasic(dataModel)
        checkOrganisationEnumerated(dataModel)
        checkSampleSummaryMetadata(dataModel)

        final DataClass publicSchema = dataModel.childDataClasses.first()
        final Set<DataClass> dataClasses = publicSchema.dataClasses
        final DataClass sampleTable = dataClasses.find {it.label == 'BIGGER_SAMPLE'}

        assertEquals 'Sample Number of columns/dataElements', 4, sampleTable.dataElements.size()

        final DataElement sample_int = sampleTable.dataElements.find{it.label == "SAMPLE_INT"}
        assertEquals 'description of summary metadata for sample_int',
                'Estimated Value Distribution (calculated by sampling 10% of rows)',
                sample_int.summaryMetadata[0].description

        final DataElement sample_decimal = sampleTable.dataElements.find{it.label == "SAMPLE_DECIMAL"}
        assertEquals 'description of summary metadata for sample_decimal',
                'Estimated Value Distribution (calculated by sampling 10% of rows)',
                sample_decimal.summaryMetadata[0].description

        final DataElement sample_date = sampleTable.dataElements.find{it.label == "SAMPLE_DATE"}
        assertEquals 'description of summary metadata for sample_date',
                'Estimated Value Distribution (calculated by sampling 10% of rows)',
                sample_date.summaryMetadata[0].description

        /**
         * Enumeration type determined using a sample, so we can't be certain that there will be exactly 15 results.
         * But there should be between 1 and 15 values, and any values must be in our expected list.
         */
        final EnumerationType sampleVarcharEnumerationType = sampleTable.findDataElement('SAMPLE_VARCHAR2').dataType
        assertTrue 'One or more enumeration values', sampleVarcharEnumerationType.enumerationValues.size() >= 1
        assertTrue '15 or fewer enumeration values', sampleVarcharEnumerationType.enumerationValues.size() <= 15
        sampleVarcharEnumerationType.enumerationValues.each {
            assertTrue 'Enumeration key in expected set',
                    ['ENUM0', 'ENUM1', 'ENUM2', 'ENUM3', 'ENUM4', 'ENUM5', 'ENUM6', 'ENUM7', 'ENUM8', 'ENUM9', 'ENUM10', 'ENUM11', 'ENUM12', 'ENUM13', 'ENUM14'].contains(it.key)
        }
    }

    private void checkBasic(DataModel dataModel) {
        assertEquals 'Database/Model name', 'METADATA_SIMPLE', dataModel.label
        assertEquals 'Number of tables/dataclasses', 8, dataModel.dataClasses.size()
        final DataClass publicSchema = dataModel.childDataClasses.first()
        assertEquals 'Number of child tables/dataclasses', 7, publicSchema.dataClasses?.size()

        final Set<DataClass> dataClasses = publicSchema.dataClasses

        // Tables
        final DataClass metadataTable = dataClasses.find {it.label == 'METADATA'}
        assertEquals 'Metadata Number of columns/dataElements', 10, metadataTable.dataElements.size()
        assertEquals 'Metadata Number of metadata', 3, metadataTable.metadata.size()

        assertTrue 'MD All metadata values are valid', metadataTable.metadata.every {it.value && it.key != it.value}

        List<Map> indexesInfo = new JsonSlurper().parseText(metadataTable.metadata.find {it.key == 'indexes'}.value) as List<Map>

        assertEquals('MD Index count', 4, indexesInfo.size())

        assertEquals 'MD Primary key', 1, metadataTable.metadata.count {it.key == 'primary_key_name'}
        assertEquals 'MD Primary key', 1, metadataTable.metadata.count {it.key == 'primary_key_columns'}
        assertEquals 'MD Primary indexes', 1, indexesInfo.findAll {it.primaryIndex}.size()
        assertEquals 'MD Unique indexes', 2, indexesInfo.findAll {it.uniqueIndex}.size()
        assertEquals 'MD indexes', 2, indexesInfo.findAll {!it.uniqueIndex && !it.primaryIndex}.size()

        final Map multipleColIndex =indexesInfo.find {it.name ==  'UNIQUE_ITEM_ID_NAMESPACE_KEY'}
        assertNotNull 'Should have multi column index', multipleColIndex
        assertEquals 'Correct order of columns', 'CATALOGUE_ITEM_ID, NAMESPACE, KEY', multipleColIndex.columns

        final DataClass ciTable = dataClasses.find {it.label == 'CATALOGUE_ITEM'}
        assertEquals 'CI Number of columns/dataElements', 10, ciTable.dataElements.size()
        assertEquals 'CI Number of metadata', 3, ciTable.metadata.size()

        assertTrue 'CI All metadata values are valid', ciTable.metadata.every {it.value && it.key != it.value}

        indexesInfo = new JsonSlurper().parseText(ciTable.metadata.find {it.key == 'indexes'}.value) as List<Map>

        assertEquals('CI Index count', 3, indexesInfo.size())

        assertEquals 'CI Primary key', 1, ciTable.metadata.count {it.key == 'primary_key_name'}
        assertEquals 'CI Primary key', 1, ciTable.metadata.count {it.key == 'primary_key_columns'}
        assertEquals 'CI Primary indexes', 1, indexesInfo.findAll {it.primaryIndex}.size()
        assertEquals 'CI indexes', 2, indexesInfo.findAll {!it.uniqueIndex && !it.primaryIndex}.size()

        final DataClass cuTable = dataClasses.find {it.label == 'CATALOGUE_USER'}
        assertEquals 'CU Number of columns/dataElements', 18, cuTable.dataElements.size()
        assertEquals 'CU Number of metadata', 5, cuTable.metadata.size()

        assertTrue 'CU All metadata values are valid', cuTable.metadata.every {it.value && it.key != it.value}

        indexesInfo = new JsonSlurper().parseText(cuTable.metadata.find {it.key == 'indexes'}.value) as List<Map>

        assertEquals('CU Index count', 3, indexesInfo.size())

        assertEquals 'CU Primary key', 1, cuTable.metadata.count {it.key == 'primary_key_name'}
        assertEquals 'CU Primary key', 1, cuTable.metadata.count {it.key == 'primary_key_columns'}
        assertEquals 'CI Primary indexes', 1, indexesInfo.findAll {it.primaryIndex}.size()
        assertEquals 'CI Unique indexes', 2, indexesInfo.findAll {it.uniqueIndex}.size()
        assertEquals 'CI indexes', 1, indexesInfo.findAll {!it.uniqueIndex && !it.primaryIndex}.size()
        assertEquals 'CU constraint', 1, cuTable.metadata.count {it.key == 'unique_name'}
        assertEquals 'CU constraint', 1, cuTable.metadata.count {it.key == 'unique_columns'}

        // Columns
        assertTrue 'MD all elements required', metadataTable.dataElements.every {it.minMultiplicity == 1}
        assertEquals 'CI mandatory elements', 9, ciTable.dataElements.count {it.minMultiplicity == 1}
        assertEquals 'CI optional element description', 0, ciTable.findDataElement('DESCRIPTION').minMultiplicity
        assertEquals 'CU mandatory elements', 10, cuTable.dataElements.count {it.minMultiplicity == 1}
    }

    private void checkOrganisationNotEnumerated(DataModel dataModel) {
        final DataClass publicSchema = dataModel.childDataClasses.first()
        final Set<DataClass> dataClasses = publicSchema.dataClasses
        final DataClass organisationTable = dataClasses.find {it.label == 'ORGANISATION'}

        Map<String, String> expectedColumns = [
                'ORG_CODE': 'PrimitiveType',
                'ORG_NAME': 'PrimitiveType',
                'ORG_CHAR': 'PrimitiveType',
                'DESCRIPTION': 'PrimitiveType',
                'ORG_TYPE': 'PrimitiveType',
                'ID': 'PrimitiveType'
        ]

        assertEquals 'Organisation Number of columns/dataElements', expectedColumns.size(), organisationTable.dataElements.size()
        // Expect 3 metadata - 2 for the primary key and 1 for indexes
        assertEquals 'Organisation Number of metadata', 3, organisationTable.metadata.size()
        //Expect all types to be Primitive, because we are not detecting enumerations
        expectedColumns.each {
            columnName, columnType ->
                assertEquals "DomainType of the DataType for ${columnName}", columnType, organisationTable.findDataElement(columnName).dataType.domainType
        }
    }

    private void checkOrganisationEnumerated(DataModel dataModel) {
        final DataClass publicSchema = dataModel.childDataClasses.first()
        final Set<DataClass> dataClasses = publicSchema.dataClasses
        final DataClass organisationTable = dataClasses.find {it.label == 'ORGANISATION'}

        Map<String, String> expectedColumns = [
                'ORG_CODE': 'EnumerationType',
                'ORG_NAME': 'PrimitiveType',
                'ORG_CHAR': 'EnumerationType',
                'DESCRIPTION': 'PrimitiveType',
                'ORG_TYPE': 'EnumerationType',
                'ID': 'PrimitiveType'
        ]

        assertEquals 'Organisation Number of columns/dataElements', expectedColumns.size(), organisationTable.dataElements.size()
        // Expect 3 metadata - 2 for the primary key and 1 for indexes
        assertEquals 'Organisation Number of metadata', 3, organisationTable.metadata.size()
        //Expect all types to be Primitive, because we are not detecting enumerations
        expectedColumns.each {
            columnName, columnType ->
                assertEquals "DomainType of the DataType for ${columnName}", columnType, organisationTable.findDataElement(columnName).dataType.domainType
        }

        final EnumerationType orgCodeEnumerationType = organisationTable.findDataElement('ORG_CODE').dataType
        assertEquals 'Number of enumeration values for org_code', 4, orgCodeEnumerationType.enumerationValues.size()
        assertNotNull 'Enumeration value found', orgCodeEnumerationType.enumerationValues.find{it.key == 'CODEZ'}
        assertNotNull 'Enumeration value found',orgCodeEnumerationType.enumerationValues.find{it.key == 'CODEY'}
        assertNotNull 'Enumeration value found',orgCodeEnumerationType.enumerationValues.find{it.key == 'CODEX'}
        assertNotNull 'Enumeration value found',orgCodeEnumerationType.enumerationValues.find{it.key == 'CODER'}
        assertNull 'Not an expected value', orgCodeEnumerationType.enumerationValues.find{it.key == 'CODEP'}

        final EnumerationType orgTypeEnumerationType = organisationTable.findDataElement('ORG_TYPE').dataType
        assertEquals 'Number of enumeration values for org_type', 3, orgTypeEnumerationType.enumerationValues.size()
        assertNotNull 'Enumeration value found', orgTypeEnumerationType.enumerationValues.find{it.key == 'TYPEA'}
        assertNotNull 'Enumeration value found', orgTypeEnumerationType.enumerationValues.find{it.key == 'TYPEB'}
        assertNotNull 'Enumeration value found', orgTypeEnumerationType.enumerationValues.find{it.key == 'TYPEC'}
        assertNull 'Not an expected value', orgTypeEnumerationType.enumerationValues.find{it.key == 'TYPEZ'}

        final EnumerationType orgCharEnumerationType = organisationTable.findDataElement('ORG_CHAR').dataType
        assertEquals 'Number of enumeration values for org_char', 3, orgCharEnumerationType.enumerationValues.size()
        assertNotNull 'Enumeration   value found', orgCharEnumerationType.enumerationValues.find{it.key == 'CHAR1'}
        assertNotNull 'Enumeration value found', orgCharEnumerationType.enumerationValues.find{it.key == 'CHAR2'}
        assertNotNull 'Enumeration value found', orgCharEnumerationType.enumerationValues.find{it.key == 'CHAR3'}
        assertNull 'Not an expected value', orgCharEnumerationType.enumerationValues.find{it.key == 'CHAR4'}
    }

    private checkSampleNoSummaryMetadata(DataModel dataModel) {
        final DataClass publicSchema = dataModel.childDataClasses.first()
        final Set<DataClass> dataClasses = publicSchema.dataClasses
        final DataClass sampleTable = dataClasses.find {it.label == 'SAMPLE'}

        List<String> expectedColumns = [
                "ID",
                "SAMPLE_SMALLINT",
                "SAMPLE_INT",
                "SAMPLE_DECIMAL",
                "SAMPLE_NUMERIC",
                "SAMPLE_DATE"
        ]

        assertEquals 'Sample Number of columns/dataElements', expectedColumns.size(), sampleTable.dataElements.size()

        expectedColumns.each {columnName ->
            DataElement de = sampleTable.dataElements.find{it.label == columnName}
            assertEquals 'Zero summaryMetadata', 0, de.summaryMetadata.size()
        }
    }

    private void checkSampleSummaryMetadata(DataModel dataModel) {
        final DataClass publicSchema = dataModel.childDataClasses.first()
        final Set<DataClass> dataClasses = publicSchema.dataClasses
        final DataClass sampleTable = dataClasses.find {it.label == 'SAMPLE'}

        assertEquals 'Sample Number of columns/dataElements', 6, sampleTable.dataElements.size()

        final DataElement id = sampleTable.dataElements.find{it.label == "ID"}
        //Expect id to have contiguous values from 1 to 201
        assertEquals 'reportValue for id',
                '{"0 - 20":19,"20 - 40":20,"40 - 60":20,"60 - 80":20,"80 - 100":20,"100 - 120":20,"120 - 140":20,"140 - 160":20,"160 - 180":20,"180 - 200":20,"200 - 220":2}',
                id.summaryMetadata[0].summaryMetadataReports[0].reportValue

        //sample_smallint
        final DataElement sample_smallint = sampleTable.dataElements.find{it.label == "SAMPLE_SMALLINT"}
        assertEquals 'reportValue for sample_smallint',
                '{"-100 - -80":20,"-80 - -60":20,"-60 - -40":20,"-40 - -20":20,"-20 - 0":20,"0 - 20":20,"20 - 40":20,"40 - 60":20,"60 - 80":20,"80 - 100":20,"100 - 120":1}',
                sample_smallint.summaryMetadata[0].summaryMetadataReports[0].reportValue

        //sample_int
        final DataElement sample_int = sampleTable.dataElements.find{it.label == "SAMPLE_INT"}
        assertEquals 'reportValue for sample_int',
                '{"0 - 1000":63,"1000 - 2000":26,"2000 - 3000":20,"3000 - 4000":18,"4000 - 5000":14,"5000 - 6000":14,"6000 - 7000":12,"7000 - 8000":12,"8000 - 9000":10,"9000 - 10000":10,"10000 - 11000":2}',
                sample_int.summaryMetadata[0].summaryMetadataReports[0].reportValue

        //sample_decimal
        final DataElement sample_decimal = sampleTable.dataElements.find{it.label == "SAMPLE_DECIMAL"}
        assertEquals 'reportValue for sample_decimal',
                '{"0 - 1000000":83,"1000000 - 2000000":36,"2000000 - 3000000":26,"3000000 - 4000000":22,"4000000 - 5000000":20,"5000000 - 6000000":14}',
                sample_decimal.summaryMetadata[0].summaryMetadataReports[0].reportValue

        //sample_numeric
        final DataElement sample_numeric = sampleTable.dataElements.find{it.label == "SAMPLE_NUMERIC"}
        assertEquals 'reportValue for sample_numeric',
                '{"-10.00000 - -5.00000":20,"-5.00000 - 0.00000":80,"0.00000 - 5.00000":81,"5.00000 - 10.00000":20}',
                sample_numeric.summaryMetadata[0].summaryMetadataReports[0].reportValue

        //sample_date
        final DataElement sample_date = sampleTable.dataElements.find{it.label == "SAMPLE_DATE"}
        assertEquals 'reportValue for sample_date',
                '{"Sep 2020":30,"Oct 2020":31,"Nov 2020":30,"Dec 2020":31,"Jan 2021":31,"Feb 2021":28,"Mar 2021":20}',
                sample_date.summaryMetadata[0].summaryMetadataReports[0].reportValue

    }

    /**
     * Check that there is a DataClass for the bigger_sample table, with 4 columns but no
     * summary metadata on any of these columns.
     * @param dataModel
     * @return
     */
    private checkBiggerSampleNoSummaryMetadata(DataModel dataModel) {
        final DataClass publicSchema = dataModel.childDataClasses.first()
        final Set<DataClass> dataClasses = publicSchema.dataClasses
        final DataClass sampleTable = dataClasses.find {it.label == 'BIGGER_SAMPLE'}

        List<String> expectedColumns = [
                "SAMPLE_INT",
                "SAMPLE_DECIMAL",
                "SAMPLE_DATE",
                "SAMPLE_VARCHAR2"
        ]

        assertEquals 'Sample Number of columns/dataElements', expectedColumns.size(), sampleTable.dataElements.size()

        expectedColumns.each {columnName ->
            DataElement de = sampleTable.dataElements.find{it.label == columnName}
            assertEquals 'Zero summaryMetadata', 0, de.summaryMetadata.size()
        }
    }

    /**
     * Check that there is a DataClass for the bigger_sample table, with 4 columns but exact
     * summary metadata on any of these columns.
     * @param dataModel
     * @return
     */
    private checkBiggerSampleSummaryMetadata(DataModel dataModel) {
        final DataClass publicSchema = dataModel.childDataClasses.first()
        final Set<DataClass> dataClasses = publicSchema.dataClasses
        final DataClass sampleTable = dataClasses.find {it.label == 'BIGGER_SAMPLE'}

        //Map of column name to expected summary metadata description:reportValue. Expect exact counts.
        Map<String, Map<String, String>> expectedColumns = [
                "SAMPLE_INT": ['Value Distribution':'{"0 - 100000":99999,"100000 - 200000":100000,"200000 - 300000":100000,"300000 - 400000":100000,"400000 - 500000":100000,"500000 - 600000":1}'],
                "SAMPLE_DECIMAL": ['Value Distribution':'{"-1 - 0":249924,"0 - 1":245051,"1 - 2":5025}'],
                "SAMPLE_DATE": ['Value Distribution':'{"24/08/2020 - 26/08/2020":91265,"26/08/2020 - 28/08/2020":56305,"28/08/2020 - 30/08/2020":43810,"30/08/2020 - 01/09/2020":39468,"01/09/2020 - 03/09/2020":38302,"03/09/2020 - 05/09/2020":39468,"05/09/2020 - 07/09/2020":43810,"07/09/2020 - 09/09/2020":56306,"09/09/2020 - 11/09/2020":91266}'],
                "SAMPLE_VARCHAR2": ['Enumeration Value Distribution':'{"ENUM0":33333,"ENUM1":33334,"ENUM10":33333,"ENUM11":33333,"ENUM12":33333,"ENUM13":33333,"ENUM14":33333,"ENUM2":33334,"ENUM3":33334,"ENUM4":33334,"ENUM5":33334,"ENUM6":33333,"ENUM7":33333,"ENUM8":33333,"ENUM9":33333}']
        ]

        assertEquals 'Sample Number of columns/dataElements', expectedColumns.size(), sampleTable.dataElements.size()

        expectedColumns.each {columnName, expectedReport ->
            DataElement de = sampleTable.dataElements.find{it.label == columnName}
            assertEquals 'One summaryMetadata', expectedReport.size(), de.summaryMetadata.size()

            expectedReport.each {expectedReportDescription, expectedReportValue ->
                assertEquals "Description of summary metadatdata for ${columnName}", expectedReportDescription, de.summaryMetadata[0].description
                assertEquals "Value of summary metadatdata for ${columnName}", expectedReportValue, de.summaryMetadata[0].summaryMetadataReports[0].reportValue
            }
        }
    }

    /**
     * Check that there is a DataClass for the bigger_sample_view view, with 4 columns but exact
     * summary metadata on any of these columns.
     * @param dataModel
     * @return
     */
    private checkBiggerSampleViewSummaryMetadata(DataModel dataModel) {
        final DataClass publicSchema = dataModel.childDataClasses.first()
        final Set<DataClass> dataClasses = publicSchema.dataClasses
        final DataClass sampleTable = dataClasses.find {it.label == 'BIGGER_SAMPLE_VIEW'}

        //Map of column name to expected summary metadata description:reportValue. Expect exact counts.
        Map<String, Map<String, String>> expectedColumns = [
                "SAMPLE_INT": ['Value Distribution':'{"0 - 100000":99999,"100000 - 200000":100000,"200000 - 300000":100000,"300000 - 400000":100000,"400000 - 500000":100000,"500000 - 600000":1}'],
                "SAMPLE_DECIMAL": ['Value Distribution':'{"-1 - 0":249924,"0 - 1":245051,"1 - 2":5025}'],
                "SAMPLE_DATE": ['Value Distribution':'{"24/08/2020 - 26/08/2020":91265,"26/08/2020 - 28/08/2020":56305,"28/08/2020 - 30/08/2020":43810,"30/08/2020 - 01/09/2020":39468,"01/09/2020 - 03/09/2020":38302,"03/09/2020 - 05/09/2020":39468,"05/09/2020 - 07/09/2020":43810,"07/09/2020 - 09/09/2020":56306,"09/09/2020 - 11/09/2020":91266}'],
                "SAMPLE_VARCHAR2": ['Enumeration Value Distribution':'{"ENUM0":33333,"ENUM1":33334,"ENUM10":33333,"ENUM11":33333,"ENUM12":33333,"ENUM13":33333,"ENUM14":33333,"ENUM2":33334,"ENUM3":33334,"ENUM4":33334,"ENUM5":33334,"ENUM6":33333,"ENUM7":33333,"ENUM8":33333,"ENUM9":33333}']
        ]

        assertEquals 'Sample Number of columns/dataElements', expectedColumns.size(), sampleTable.dataElements.size()

        expectedColumns.each {columnName, expectedReport ->
            DataElement de = sampleTable.dataElements.find{it.label == columnName}
            assertEquals 'One summaryMetadata', expectedReport.size(), de.summaryMetadata.size()

            expectedReport.each {expectedReportDescription, expectedReportValue ->
                assertEquals "Description of summary metadatdata for ${columnName}", expectedReportDescription, de.summaryMetadata[0].description
                assertEquals "Value of summary metadatdata for ${columnName}", expectedReportValue, de.summaryMetadata[0].summaryMetadataReports[0].reportValue
            }
        }
    }
}
