/*
 * Copyright 2020 University of Oxford
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

import uk.ac.ox.softeng.maurodatamapper.datamodel.item.datatype.DataTypeService
import uk.ac.ox.softeng.maurodatamapper.datamodel.item.datatype.PrimitiveType
import uk.ac.ox.softeng.maurodatamapper.datamodel.rest.transport.DefaultDataType

// @CompileStatic
class OracleDataTypeProvider extends DataTypeService {

    @Override
    String getDisplayName() {
        'Oracle Database DataTypes'
    }

    @Override
    List<DefaultDataType> getDefaultListOfDataTypes() {
        [[label: 'bfile', description:
            'File locators that point to a binary file on the server file system (outside the database).\n Maximum file size of 232-1 bytes.'],
         [label: 'blob', description:
             'Stores unstructured binary large objects.\nStore up to (4 gigabytes -1) * (the value of the CHUNK parameter of LOB storage).'],
         [label: 'char(size)', description:
             'Where size is the number of characters to store. Fixed-length strings. Space padded.\n Maximum size of 2000 bytes.'],
         [label: 'clob', description:
             'Stores single-byte and multi-byte character data.\nStore up to (4 gigabytes -1) * \
             (the value of the CHUNK parameter of LOB storage) of character data.'],
         [label: 'date', description: 'A date between Jan 1, 4712 BC and Dec 31, 9999 AD.'],
         [label: 'dec(p,s)', description: 'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38.'],
         [label: 'decimal(p,s)', description: 'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38.'],
         [label: 'double precision'],
         [label: 'float'],
         [label: 'int'],
         [label: 'integer'],
         [label: 'interval day (day precision) to second (fractional seconds precision)', description:
             'Time period stored in days, hours, minutes, and seconds.\nday precision must be a number between 0 and 9. (default is 2) \
             fractional seconds precision must be a number between 0 and 9. (default is 6)'],
         [label: 'interval year (year precision) to month', description:
             'Time period stored in years and months.\nyear precision is the number of digits in the year. (default is 2)'],
         [label: 'long', description: 'Variable-length strings. (backward compatible)\nMaximum size of 2GB.'],
         [label: 'long raw', description: 'Variable-length binary strings. (backward compatible)\nMaximum size of 2GB.'],
         [label: 'nchar(size)', description:
             'Where size is the number of characters to store. Fixed-length NLS string Space padded.\nMaximum size of  2000 bytes.'],
         [label: 'nclob', description:
             'Stores unicode data.\nStore up to (4 gigabytes -1) * (the value of the CHUNK parameter of LOB storage) of character text data.'],
         [label: 'number(p,s)', description:
             'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38. Scale can range from -84 to 127.'],
         [label: 'numeric(p,s)', description: 'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38.'],
         [label: 'nvarchar2(size)', description:
             'Where size is the number of characters to store. Variable-length NLS string.\nMaximum size of 4000 bytes.'],
         [label: 'raw', description: 'Variable-length binary strings\nMaximum size of 2000 bytes.'],
         [label: 'real'],
         [label: 'rowid', description:
             'Fixed-length binary data. Every record in the database has a physical address or rowid. \nThe format of the rowid is: \
             BBBBBBB.RRRR.FFFFF Where BBBBBBB is the block in the database file; RRRR is the row in the block; FFFFF is the database file.'],
         [label: 'smallint'],
         [label: 'timestamp (fractional seconds precision)', description:
             'Includes year, month, day, hour, minute, and seconds.\nfractional seconds precision must be a number between 0 and 9. (default is 6)'],
         [label: 'timestamp (fractional seconds precision) with local time zone', description:
             'Includes year, month, day, hour, minute, and seconds; with a time zone expressed as the session time zone.\n \
             fractional seconds precision must be a number between 0 and 9. (default is 6)'],
         [label: 'timestamp (fractional seconds precision) with time zone', description:
             'Includes year, month, day, hour, minute, and seconds; with a time zone displacement value.\n \
             fractional seconds precision must be a number between 0 and 9. (default is 6)'],
         [label: 'urowid(size)', description: 'Universal rowid. Where size is optional.'],
         [label: 'varchar2(size)', description:
             'Where size is the number of characters to store. Variable-length string.\nMaximum size of 4000 bytes. Maximum size of 32KB in PLSQL.'],
        ].collect {Map<String, String> properties -> new DefaultDataType(new PrimitiveType(properties))}
    }
}
