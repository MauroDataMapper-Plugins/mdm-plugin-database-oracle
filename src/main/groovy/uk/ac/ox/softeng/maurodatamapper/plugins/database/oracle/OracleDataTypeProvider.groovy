package uk.ac.ox.softeng.maurodatamapper.plugins.database.oracle

import uk.ac.ox.softeng.maurodatamapper.datamodel.item.datatype.DataTypeService
import uk.ac.ox.softeng.maurodatamapper.datamodel.item.datatype.PrimitiveType
import uk.ac.ox.softeng.maurodatamapper.datamodel.rest.transport.DefaultDataType

class OracleDataTypeProvider extends DataTypeService {

    @Override
    String getDisplayName() {
        'Oracle Database DataTypes'
    }

    @Override
    List<DefaultDataType> getDefaultListOfDataTypes() {
        [
            new PrimitiveType(label: 'bfile',
                              description: 'File locators that point to a binary file on the server file system (outside the database).\nMaximum ' +
                                           'file size of 232-1 bytes.'),
            new PrimitiveType(label: 'blob',
                              description: 'Stores unstructured binary large objects.\nStore up to (4 gigabytes -1) * (the value of the CHUNK ' +
                                           'parameter of LOB storage).'),
            new PrimitiveType(label: 'char(size)',
                              description: 'Where size is the number of characters to store. Fixed-length strings. Space padded.\nMaximum size of ' +
                                           '2000 bytes.'),
            new PrimitiveType(label: 'clob',
                              description: 'Stores single-byte and multi-byte character data.\nStore up to (4 gigabytes -1) * (the value of the ' +
                                           'CHUNK parameter of LOB storage) of character data.'),
            new PrimitiveType(label: 'date', description: 'A date between Jan 1, 4712 BC and Dec 31, 9999 AD.'),
            new PrimitiveType(label: 'dec(p,s)', description: 'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38.'),
            new PrimitiveType(label: 'decimal(p,s)', description: 'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38.'),
            new PrimitiveType(label: 'double precision'),
            new PrimitiveType(label: 'float'),
            new PrimitiveType(label: 'int'),
            new PrimitiveType(label: 'integer'),
            new PrimitiveType(label: 'interval day (day precision) to second (fractional seconds precision)',
                              description: 'Time period stored in days, hours, minutes, and seconds.\nday precision must be a number between 0 and ' +
                                           '9. (default is 2) fractional seconds precision must be a number between 0 and 9. (default is 6)'),
            new PrimitiveType(label: 'interval year (year precision) to month',
                              description: 'Time period stored in years and months.\nyear precision is the number of digits in the year. (default ' +
                                           'is 2)'),
            new PrimitiveType(label: 'long', description: 'Variable-length strings. (backward compatible)\nMaximum size of 2GB.'),
            new PrimitiveType(label: 'long raw', description: 'Variable-length binary strings. (backward compatible)\nMaximum size of 2GB.'),
            new PrimitiveType(label: 'nchar(size)',
                              description: 'Where size is the number of characters to store. Fixed-length NLS string Space padded.\nMaximum size of' +
                                           ' 2000 bytes.'),
            new PrimitiveType(label: 'nclob',
                              description: 'Stores unicode data.\nStore up to (4 gigabytes -1) * (the value of the CHUNK parameter of LOB storage) ' +
                                           'of character text data.'),
            new PrimitiveType(label: 'number(p,s)',
                              description: 'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38. Scale can range from ' +
                                           '-84 to 127.'),
            new PrimitiveType(label: 'numeric(p,s)', description: 'Where p is the precision and s is the scale.\nPrecision can range from 1 to 38.'),
            new PrimitiveType(label: 'nvarchar2(size)',
                              description: 'Where size is the number of characters to store. Variable-length NLS string.\nMaximum size of 4000 ' +
                                           'bytes.'),
            new PrimitiveType(label: 'raw', description: 'Variable-length binary strings\nMaximum size of 2000 bytes.'),
            new PrimitiveType(label: 'real'),
            new PrimitiveType(label: 'rowid',
                              description: 'Fixed-length binary data. Every record in the database has a physical address or rowid. \nThe format of' +
                                           ' the rowid is: BBBBBBB.RRRR.FFFFF Where BBBBBBB is the block in the database file; RRRR is the row in ' +
                                           'the block; FFFFF is the database file.'),
            new PrimitiveType(label: 'smallint'),
            new PrimitiveType(label: 'timestamp (fractional seconds precision)',
                              description: 'Includes year, month, day, hour, minute, and seconds.\nfractional seconds precision must be a number ' +
                                           'between 0 and 9. (default is 6)'),
            new PrimitiveType(label: 'timestamp (fractional seconds precision) with local time zone',
                              description: 'Includes year, month, day, hour, minute, and seconds; with a time zone expressed as the session time ' +
                                           'zone.\nfractional seconds precision must be a number between 0 and 9. (default is 6)'),
            new PrimitiveType(label: 'timestamp (fractional seconds precision) with time zone',
                              description: 'Includes year, month, day, hour, minute, and seconds; with a time zone displacement value.\nfractional ' +
                                           'seconds precision must be a number between 0 and 9. (default is 6)'),
            new PrimitiveType(label: 'urowid(size)', description: 'Universal rowid. Where size is optional.'),
            new PrimitiveType(label: 'varchar2(size)',
                              description: 'Where size is the number of characters to store. Variable-length string.\nMaximum size of 4000 bytes. ' +
                                           'Maximum size of 32KB in PLSQL.'),
        ]
    }
}
