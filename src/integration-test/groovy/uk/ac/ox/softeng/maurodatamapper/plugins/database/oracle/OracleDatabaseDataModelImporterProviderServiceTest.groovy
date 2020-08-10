package uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle

import uk.ac.ox.softeng.maurodatamapper.core.facet.Metadata
import uk.ac.ox.softeng.maurodatamapper.datamodel.DataModel
import uk.ac.ox.softeng.maurodatamapper.datamodel.item.DataClass
import uk.ac.ox.softeng.maurodatamapper.plugins.testing.utils.BaseDatabasePluginTest

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class OracleDatabaseDataModelImporterProviderServiceTest extends BaseDatabasePluginTest<OracleDatabaseDataModelImporterProviderServiceParameters, OracleDatabaseDataModelImporterProviderService> {

    @Override
    OracleDatabaseDataModelImporterProviderServiceParameters createDatabaseImportParameters() {
        OracleDatabaseDataModelImporterProviderServiceParameters params = new OracleDatabaseDataModelImporterProviderServiceParameters()
        params.setDatabaseNames("ORCLPDB1")
        params.setDatabaseUsername("SYSTEM")
        params.setDatabasePassword("BOpVnzFi9Ew=1")
        params.setDatabaseOwner("SYSTEM")
        return params
    }

    @Override
    String getDatabasePortPropertyName() {
        return "oracle.database.port"
    }

    @Override
    int getDefaultDatabasePort() {
        return 1521
    }

    @Test
    void testImportSimpleDatabase() {
        OracleDatabaseDataModelImporterProviderServiceParameters params = createDatabaseImportParameters(databaseHost, databasePort)
        params.setDatabaseOwner("METADATA_SIMPLE")

        DataModel dataModel = importDataModelAndRetrieveFromDatabase(params);

        assertEquals("Database/Model name", "METADATA_SIMPLE", dataModel.getLabel())
        assertEquals("Number of columntypes/datatypes", 9, dataModel.getDataTypes()?.size())

        assertEquals("Number of primitive types", 7, dataModel.getDataTypes().findAll {it.domainType == 'PrimitiveType'}.size())
        assertEquals("Number of reference types", 2, dataModel.getDataTypes().findAll {it.domainType == 'ReferenceType'}.size())

        assertEquals("Number of tables/dataclasses", 4, dataModel.getDataClasses()?.size())
        assertEquals("Number of child tables/dataclasses", 1, dataModel.getChildDataClasses()?.size())
        Set<DataClass> childDataClasses = dataModel.getChildDataClasses()
        DataClass publicSchema = childDataClasses.first()

        assertEquals("Number of child tables/dataclasses", 3, publicSchema.getDataClasses()?.size())

        Set<DataClass> dataClasses = publicSchema.dataClasses

        // Tables
        DataClass metadataTable = dataClasses.find {it.label == "METADATA"}
        assertEquals("Metadata Number of columns/dataElements", 10, metadataTable.getDataElements().size())
        assertEquals("Metadata Number of metadata", 5, metadataTable.metadata.size())

        assertTrue("MD All metadata values are valid", metadataTable.metadata.every {it.value && it.key != it.value})

        assertEquals("MD Primary key", 1, metadataTable.metadata.count {it.key.startsWith 'primary_key'})
        assertEquals("MD Primary indexes", 1, metadataTable.metadata.count {it.key.startsWith 'primary_index'})
        assertEquals("MD Unique indexes", 1, metadataTable.metadata.count {it.key.startsWith 'unique_index'})
        assertEquals("MD Indexes", 2, metadataTable.metadata.count {it.key.startsWith 'index'})

        Metadata multipleColIndex = metadataTable.metadata.find {it.key.contains('UNIQUE_ITEM_ID_NAMESPACE_KEY')}
        assertNotNull("Should have multi column index", multipleColIndex)
        assertEquals("Correct order of columns", 'CATALOGUE_ITEM_ID, NAMESPACE, KEY', multipleColIndex.value)

        DataClass ciTable = dataClasses.find {it.label == "CATALOGUE_ITEM"}
        assertEquals("CI Number of columns/dataElements", 10, ciTable.getDataElements().size())
        assertEquals("CI Number of metadata", 4, ciTable.metadata.size())

        assertTrue("CI All metadata values are valid", ciTable.metadata.every {it.value && it.key != it.value})

        assertEquals("CI Primary key", 1, ciTable.metadata.count {it.key.startsWith 'primary_key'})
        assertEquals("CI Primary indexes", 1, ciTable.metadata.count {it.key.startsWith 'primary_index'})
        assertEquals("CI Indexes", 2, ciTable.metadata.count {it.key.startsWith 'index'})


        DataClass cuTable = dataClasses.find {it.label == "CATALOGUE_USER"}
        assertEquals("CU Number of columns/dataElements", 18, cuTable.getDataElements().size())
        assertEquals("CU Number of metadata", 5, cuTable.metadata.size())

        assertTrue("CU All metadata values are valid", cuTable.metadata.every {it.value && it.key != it.value})

        assertEquals("CU Primary key", 1, cuTable.metadata.count {it.key.startsWith 'primary_key'})
        assertEquals("CU Primary indexes", 1, cuTable.metadata.count {it.key.startsWith 'primary_index'})
        assertEquals("CU Unique indexes", 1, cuTable.metadata.count {it.key.startsWith 'unique_index'})
        assertEquals("CU Indexes", 1, cuTable.metadata.count {it.key.startsWith 'index'})
        assertEquals("CU Unique Constraint", 1, cuTable.metadata.count {it.key.startsWith 'unique['})

        // Columns
        assertTrue("MD all elements required", metadataTable.dataElements.every {it.minMultiplicity == 1})
        assertEquals("CI mandatory elements", 9, ciTable.dataElements.count {it.minMultiplicity == 1})
        assertEquals("CI optional element description", 0, ciTable.findDataElement('DESCRIPTION').minMultiplicity)
        assertEquals("CU mandatory elements", 10, cuTable.dataElements.count {it.minMultiplicity == 1})

    }
}
