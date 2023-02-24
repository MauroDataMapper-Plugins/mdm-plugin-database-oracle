/*
 * Copyright 2020-2023 University of Oxford and NHS England
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


import uk.ac.ox.softeng.maurodatamapper.datamodel.item.datatype.PrimitiveType
import uk.ac.ox.softeng.maurodatamapper.datamodel.provider.DefaultDataTypeProvider
import uk.ac.ox.softeng.maurodatamapper.datamodel.rest.transport.DefaultDataType

// @CompileStatic
class OracleDataTypeProviderService implements DefaultDataTypeProvider {

    @Override
    String getDisplayName() {
        'Oracle Database DataTypes'
    }

    @Override
    String getVersion() {
        getClass().getPackage().getSpecificationVersion() ?: 'SNAPSHOT'
    }

    @Override
    List<DefaultDataType> getDefaultListOfDataTypes() {
        [[label: 'BFILE', description:
            'File locators that point to a binary file on the server file system (outside the database).\n Maximum file size of 232-1 bytes.'],
         [label: 'BLOB', description:
             'Stores unstructured binary large objects.\nStore up to (4 gigabytes -1) * (the value of the CHUNK parameter of LOB storage).'],
         [label: 'CHAR', description:
             'Where size is the number of characters to store. Fixed-length strings. Space padded.\n Maximum size of 2000 bytes.'],
         [label: 'CHAR(SIZE)', description:
             'Where size is the number of characters to store. Fixed-length strings. Space padded.\n Maximum size of 2000 bytes.'],
         [label: 'CLOB', description:
             'Stores single-byte and multi-byte character data.\nStore up to (4 gigabytes -1) * \
             (the value of the CHUNK parameter of LOB storage) of character data.'],
         [label: 'DATE', description: 'A date between Jan 1, 4712 BC and Dec 31, 9999 AD.'],
         [label: 'DEC(P,S)', description: 'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38.'],
         [label: 'DECIMAL(P,S)', description: 'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38.'],
         [label: 'DOUBLE PRECISION'],
         [label: 'FLOAT'],
         [label: 'INT'],
         [label: 'INTEGER'],
         [label: 'INTERVAL DAY (DAY PRECISION) TO SECOND (FRACTIONAL SECONDS PRECISION)', description:
             'Time period stored in days, hours, minutes, and seconds.\nday precision must be a number between 0 and 9. (default is 2) \
             fractional seconds precision must be a number between 0 and 9. (default is 6)'],
         [label: 'INTERVAL YEAR (YEAR PRECISION) TO MONTH', description:
             'Time period stored in years and months.\nyear precision is the number of digits in the year. (default is 2)'],
         [label: 'LONG', description: 'Variable-length strings. (backward compatible)\nMaximum size of 2GB.'],
         [label: 'LONG RAW', description: 'Variable-length binary strings. (backward compatible)\nMaximum size of 2GB.'],
         [label: 'NCHAR(SIZE)', description:
             'Where size is the number of characters to store. Fixed-length NLS string Space padded.\nMaximum size of  2000 bytes.'],
         [label: 'NCLOB', description:
             'Stores unicode data.\nStore up to (4 gigabytes -1) * (the value of the CHUNK parameter of LOB storage) of character text data.'],
         [label: 'NUMBER', description:
             'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38. Scale can range from -84 to 127.'],
         [label: 'NUMBER(P,S)', description:
             'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38. Scale can range from -84 to 127.'],
         [label: 'NUMERIC(P,S)', description: 'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38.'],
         [label: 'NVARCHAR2(SIZE)', description:
             'Where size is the number of characters to store. Variable-length NLS string.\nMaximum size of 4000 bytes.'],
         [label: 'RAW', description: 'Variable-length binary strings\nMaximum size of 2000 bytes.'],
         [label: 'REAL'],
         [label: 'ROWID', description:
             'Fixed-length binary data. Every record in the database has a physical address or rowid. \nThe format of the rowid is: \
             BBBBBBB.RRRR.FFFFF Where BBBBBBB is the block in the database file; RRRR is the row in the block; FFFFF is the database file.'],
         [label: 'SMALLINT'],
         [label: 'TIMESTAMP (FRACTIONAL SECONDS PRECISION)', description:
             'Includes year, month, day, hour, minute, and seconds.\nfractional seconds precision must be a number between 0 and 9. (default is 6)'],
         [label: 'TIMESTAMP (FRACTIONAL SECONDS PRECISION) WITH LOCAL TIME ZONE', description:
             'Includes year, month, day, hour, minute, and seconds; with a time zone expressed as the session time zone.\n \
             fractional seconds precision must be a number between 0 and 9. (default is 6)'],
         [label: 'TIMESTAMP (FRACTIONAL SECONDS PRECISION) WITH TIME ZONE', description:
             'Includes year, month, day, hour, minute, and seconds; with a time zone displacement value.\n \
             fractional seconds precision must be a number between 0 and 9. (default is 6)'],
         [label: 'UROWID(SIZE)', description: 'Universal rowid. Where size is optional.'],
         [label: 'VARCHAR2', description:
             'Where size is the number of characters to store. Variable-length string.\nMaximum size of 4000 bytes. Maximum size of 32KB in PLSQL.'],
         [label: 'VARCHAR2(SIZE)', description:
             'Where size is the number of characters to store. Variable-length string.\nMaximum size of 4000 bytes. Maximum size of 32KB in PLSQL.'],
        ].collect {Map<String, String> properties -> new DefaultDataType(new PrimitiveType(properties))}
    }
}
