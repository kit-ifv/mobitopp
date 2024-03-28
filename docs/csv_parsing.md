# Reading and parsing csv files

A common type of input data for mobiTopp are csv files. The framework provides several options to read these files and parse them into a target data format. In the following we describe the interfaces for csv reading and parsing as well as the provided default implementations and how to use them.

# Parsing csv files
## The CsvParserInterface
A **CsvParser** provides a method to parse the file content provided by a **CsvReader** to a desired target data type.
Also, it provides convenience methods which take care of instantiating the CsvReader:
```kotlin
parser.parse("test.csv")
parser.parse(file)
parser.parse(csvReader)
```
The 'constructor' **CsvParser()** currently defaults to creating a **DefaultCsvParser**. 

## Parsing rows
A **RowCsvParser<E>** parses each row/line to a single entity of the target data type E.
The mapping function ```(Row) -> E?``` may produce null, if the row could not be mapped to an entity.
In that case an error message is printed to the console, depending on the selected **ErrorHandling** strategy.
Moreover, null values are removed from the generated stream.

```kotlin
CsvParser<Entity> { 
    row -> Entity(
        id = row("id", String::toInt),
        name = row("name")
    ) 
}
```

## Typed columns
To enhance readability, the csv package provides convenience methods to parse non-string values for several common data types.
```kotlin
CsvParser<Entity> { 
    row -> Entity(
        string = row("strCol"),
        byte = row.byte("byteCol"),
        short = row.short("shortCol"),
        int = row.int("intCol"),
        long = row.long("longCol"),
        float = row.float("floatCol"),
        double = row.double("doubleCol"),
        bool = row.boolean("boolCol"),
        id = row.id("idCol"), // type ID
        percent = row.share("shareCol"), //UnitIntervalValue -> number in [0,1]
        const = row.decode("enumIntCodeCol", decoder), // decode int values to constant values using a decoder
        const = row.decode("enumNameCol", decode), // decode string values to constant values using a decoder
        dist = row.distance("distCol", unit),
        meters = row.meters("meterCol"),
        kilometers = row.kilometers("kilometersCol"),
        currency = row.currency("currencyCol", currencyUnit),
        euros = row.euros("euroCol"),
    ) 
}
```


## Parsing single columns
When parsing only a single column, a **SingleColumnParser** can be applied. This will provide more detailed error messages.
```kotlin
SingleColumnParser<Int>(column="id", parser=String::toInt)
```

## Parsing pairs
A **TwoColumnParser** wraps two **RowCsvParsers**s, one key and one value parser.
If both parsers return non-null value for the row to be parsed, a key-value **Pair** is created.
```kotlin
CsvPairParser<Int, String>(keyParser, valueParser)
```

## Parsing maps
When parsing key-value pairs, a **MapCsvParser** can be applied. 
It provides convenience methods to create a **Map** from the resulting sequence of key-value pairs.
```kotlin
DefaultMapCsvParser<Int, String>(
    CsvParser{ row -> row("id", String::toInt) to row("name")}
).parseMap(file) // Map<Int, String>
```
If the key is not unique, a **MapMergeCsvParser** can be applied to collect all values of the same key in a list.
```kotlin
MapMergeCsvParser<Int, String>(
    CsvParser{ row -> row("id", String::toInt) to row("name")}
).parseMap(file) // Map<Int, List<String>>
```



# Reading csv files

## The CsvReader interface
A **CsvReader** must provide a set of column names, the number of lines in the file as well as a description of the datasource (e.g. the file path). This metadata is required to provide context information while debugging and to create meaningful error messages.

Most importantly CsvReaders provide a sequence of **Row**s, each containing the information of one line in the csv file.

## Rows
The **Row** interface requires that each row has a description of its source (for debugging and meaningful error messages). Also **Row**s have an index within their source. They also allow to obtain values from the file/line they represent by specifying a column name or the column index. By default, values are read as Strings. Optionally, the value can be mapped to a desired type:
```kotlin
val value: String = row("column_name")
val other: Int = row("other_column", String::toInt)
val foo: String1 = row.valueAt(3)
val bar: Int = row.valueAt(4, String::toInt)
```
The default implementation **DefaultRow** checks whether the requested column is available and the row contains a value for that column.

## The DefaultCsvReader
The default implementation of the **CsvReader** interface is a **lazy** reader.
Upon instantiation, the header of the provided csv file is read and the lines are counted.
However, the lines are not yet processed.
Only when the sequence of rows is processed, the lines of the file are read lazily.
The default separator is a **semicolon**, however it can be customized.
When extracting the separated values of a line, separators occurring between double-quotes are ignored.
```kotlin
val reader = DefaultCsvReader(file, ";")
```


