package utils.csv

/**
 * A builder to create a [CsvParser] constructed from multiple column
 * parsing functions. The builder uses the fluent builder pattern for
 * chained definitions.
 *
 * @param E the generic type of the entities to be generated
 * @constructor create an empty [CsvParserBuilder] using the given entity
 *     spawner function
 * @property entitySpawner a function to produce an empty entity, it
 *     receives the index of the parsed row
 */
@Suppress("TooManyFunctions")
class CsvParserBuilder<E>(
    private val entitySpawner: (Int) -> E
) {
    private val columnParsers: MutableMap<String, (E, String) -> E?> = mutableMapOf()
    private var errorHandling: ParserErrorHandling = ParserErrorHandling.WARN_KEEP

    /**
     * Add string column using the given setter function.
     *
     * @param column the name of the column
     * @param setter a function that takes the parsed value and the entity to
     *     be processed, it may further transform the string value and should
     *     then modify the entity accordingly (e.g. set a property to the
     *     parsed value)
     * @return this builder
     */
    fun addStringPropertyColumn(column: String, setter: (E, String) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> e.also{setter(e, s)} }
        return this
    }

    /**
     * Add string column using the given transform function.
     *
     * @param column the name of the column
     * @param transform a function that takes the parsed value and the entity to
     *     be processed, and returns the modified entity or an entirely new entity
     * @return this builder
     */
    fun addStringColumn(column: String, transform: (E, String) -> E?): CsvParserBuilder<E> {
        columnParsers[column] = transform
        return this
    }

    /**
     * Add byte column using the given setter function.
     *
     * @param column the name of the column
     * @param setter a function that takes the parsed value and the entity to
     *     be processed, it may further transform the byte value and should
     *     then modify the entity accordingly (e.g. set a property to the
     *     parsed value)
     * @return this builder
     */
    fun addBytePropertyColumn(column: String, setter: (E, Byte) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> e.also{setter(e, s.toByte())} }
        return this
    }

    /**
     * Add byte column using the given transform function.
     *
     * @param column the name of the column
     * @param transform a function that takes the parsed value and the entity to
     *     be processed, and returns the modified entity or an entirely new entity
     * @return this builder
     */
    fun addByteColumn(column: String, transform: (E, Byte) -> E?): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> transform(e, s.toByte()) }
        return this
    }

    /**
     * Add short column using the given setter function.
     *
     * @param column the name of the column
     * @param setter a function that takes the parsed value and the entity to
     *     be processed, it may further transform the short value and should
     *     then modify the entity accordingly (e.g. set a property to the
     *     parsed value)
     * @return this builder
     */
    fun addShortPropertyColumn(column: String, setter: (E, Short) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> e.also{ setter(e, s.toShort()) } }
        return this
    }

    /**
     * Add short column using the given transform function.
     *
     * @param column the name of the column
     * @param transform a function that takes the parsed value and the entity to
     *      be processed, and returns the modified entity or an entirely new entity
     * @return this builder
     */
    fun addShortColumn(column: String, transform: (E, Short) -> E?): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> transform(e, s.toShort()) }
        return this
    }

    /**
     * Add int column using the given setter function.
     *
     * @param column the name of the column
     * @param setter a function that takes the parsed value and the entity to
     *     be processed, it may further transform the int value and should then
     *     modify the entity accordingly (e.g. set a property to the parsed
     *     value)
     * @return this builder
     */
    fun addIntPropertyColumn(column: String, setter: (E, Int) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> e.also{setter(e, s.toInt())} }
        return this
    }

    /**
     * Add int column using the given transform function.
     *
     * @param column the name of the column
     * @param transform a function that takes the parsed value and the entity to
     *      be processed, and returns the modified entity or an entirely new entity
     * @return this builder
     */
    fun addIntColumn(column: String, transform: (E, Int) -> E?): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> transform(e, s.toInt()) }
        return this
    }

    /**
     * Add long column using the given setter function.
     *
     * @param column the name of the column
     * @param setter a function that takes the parsed value and the entity to
     *     be processed, it may further transform the long value and should
     *     then modify the entity accordingly (e.g. set a property to the
     *     parsed value)
     * @return this builder
     */
    fun addLongPropertyColumn(column: String, setter: (E, Long) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> e.also { setter(e, s.toLong()) } }
        return this
    }

    /**
     * Add long column using the given transform function.
     *
     * @param column the name of the column
     * @param transform a function that takes the parsed value and the entity to
     *      be processed, and returns the modified entity or an entirely new entity
     * @return this builder
     */
    fun addLongColumn(column: String, transform: (E, Long) -> E?): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> transform(e, s.toLong()) }
        return this
    }

    /**
     * Add float column using the given setter function.
     *
     * @param column the name of the column
     * @param setter a function that takes the parsed value and the entity to
     *     be processed, it may further transform the float value and should
     *     then modify the entity accordingly (e.g. set a property to the
     *     parsed value)
     * @return this builder
     */
    fun addFloatPropertyColumn(column: String, setter: (E, Float) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> e.also{ setter(e, s.toFloat()) } }
        return this
    }

    /**
     * Add float column using the given transform function.
     *
     * @param column the name of the column
     * @param transform a function that takes the parsed value and the entity to
     *      be processed, and returns the modified entity or an entirely new entity
     * @return this builder
     */
    fun addFloatColumn(column: String, transform: (E, Float) -> E?): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> transform(e, s.toFloat()) }
        return this
    }

    /**
     * Add double column using the given setter function.
     *
     * @param column the name of the column
     * @param setter a function that takes the parsed value and the entity to
     *     be processed, it may further transform the double value and should
     *     then modify the entity accordingly (e.g. set a property to the
     *     parsed value)
     * @return this builder
     */
    fun addDoublePropertyColumn(column: String, setter: (E, Double) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> e.also { setter(e, s.toDouble()) } }
        return this
    }

    /**
     * Add double column using the given transform function.
     *
     * @param column the name of the column
     * @param transform a function that takes the parsed value and the entity to
     *      be processed, and returns the modified entity or an entirely new entity
     * @return this builder
     */
    fun addDoubleColumn(column: String, transform: (E, Double) -> E?): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> transform(e, s.toDouble()) }
        return this
    }

    /**
     * Add boolean column using the given setter function.
     *
     * @param column the name of the column
     * @param setter a function that takes the parsed value and the entity to
     *     be processed, it may further transform the boolean value and should
     *     then modify the entity accordingly (e.g. set a property to the
     *     parsed value)
     * @return this builder
     */
    fun addBooleanPropertyColumn(column: String, setter: (E, Boolean) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] =
            { e, s -> e.also { setter(e, s.toBoolean()) } } //TODO allow other encoding of true (currently "true")
        return this
    }

    /**
     * Add boolean column using the given transform function.
     *
     * @param column the name of the column
     * @param transform a function that takes the parsed value and the entity to
     *      be processed, and returns the modified entity or an entirely new entity
     * @return this builder
     */
    fun addBooleanColumn(column: String, transform: (E, Boolean) -> E): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> transform(e, s.toBoolean()) }
        return this
    }

    /**
     * Add a typed column using the given setter function.
     *
     * @param column the name of the column
     * @param convert
     * @param setter a function that takes the parsed value and the entity to
     *     be processed, it may further transform the value (of type [T]) and
     *     should then modify the entity accordingly (e.g. set a property to
     *     the parsed value)
     * @param T the generic type of the value to be parsed
     * @return this builder
     */
    fun <T> addTypedPropertyColumn(
        column: String,
        convert: (String) -> T, setter: (E, T) -> Unit
    ): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> e.also { setter(e, convert(s)) } }
        return this
    }

    /**
     * Add a typed column using the given transform function.
     *
     * @param column the name of the column
     * @param convert
     * @param transform a function that takes the parsed value and the entity to
     *      be processed, and returns the modified entity or an entirely new entity
     * @param T the generic type of the value to be parsed
     * @return this builder
     */
    fun <T> addTypedColumn(column: String, convert: (String) -> T, transform: (E, T) -> E?): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> transform(e, convert(s)) }
        return this
    }

    /**
     * Use parsing error handling: drop rows with errors silently
     *
     * @return this builder
     */
    fun onParseErrorDropRowSilently(): CsvParserBuilder<E> {
        this.errorHandling = ParserErrorHandling.SILENT_DROP
        return this
    }

    /**
     * Use parsing error handling: drop rows with errors and print warning
     *
     * @return this builder
     */
    fun onParseErrorDropRowWithWarning(): CsvParserBuilder<E> {
        this.errorHandling = ParserErrorHandling.WARN_DROP
        return this
    }

    /**
     * Use parsing error handling: drop rows with errors and print stack trace
     *
     * @return this builder
     */
    fun onParseErrorDropRowWithError(): CsvParserBuilder<E> {
        this.errorHandling = ParserErrorHandling.ERROR_DROP
        return this
    }

    /**
     * Use parsing error handling: keep rows with errors without printing any
     * warnings
     *
     * @return this builder
     */
    fun onParseErrorKeepRowSilently(): CsvParserBuilder<E> {
        this.errorHandling = ParserErrorHandling.SILENT_KEEP
        return this
    }

    /**
     * Use parsing error handling: keeps rows with errors and prints warning
     *
     * @return
     */
    fun onParseErrorKeepRowWithWarning(): CsvParserBuilder<E> {
        this.errorHandling = ParserErrorHandling.WARN_KEEP
        return this
    }

    /**
     * Use parsing error handling: keeps rows with errors and prints stack trace
     *
     * @return
     */
    fun onParseErrorKeepRowWithError(): CsvParserBuilder<E> {
        this.errorHandling = ParserErrorHandling.ERROR_KEEP
        return this
    }

    /**
     * Use parsing error handling: throw exception when first parsing error
     * occurs.
     *
     * @return this builder
     */
    fun onParseErrorThrowException(): CsvParserBuilder<E> {
        this.errorHandling = ParserErrorHandling.THROW
        return this
    }

    /**
     * Builds a [CsvParser] from the previously specified columns and error
     * handling strategy.
     *
     * @return a [CsvParser] for entities of type [E]
     */
    fun build(): CsvParser<E> {
        return DefaultRowCsvParser(entitySpawner, columnParsers, errorHandling)
    }

}

/**
 * Extension function to build a [MapCsvParser].
 *
 * @param B the generic type of the [CsvParserBuilder] to be extended
 * @param K the generic key type
 * @param V the generic value type
 * @return a [MapCsvParser]
 * @receiver [CsvParserBuilder]s with generic type E = [Pair]<[K], [V]>
 */
fun <B, K, V> B.buildMapParser(
): MapCsvParser<K, V>
        where B : CsvParserBuilder<Pair<K, V>> {

    val pairParser = this.build()
    return DefaultMapCsvParser(pairParser)
}

/**
 * Extension function to build a [MapMergeCsvParser].
 *
 * @param B the generic type of the [CsvParserBuilder] to be extended
 * @param K the generic key type
 * @param V the generic value type
 * @return a [MapMergeCsvParser]
 * @receiver [CsvParserBuilder]s with generic type E = [Pair]<[K], [V]>
 */
fun <B, K, V> B.buildMergeMapParser(
): MapCsvParser<K, List<V>>
        where B : CsvParserBuilder<Pair<K, V>> {

    val pairParser = this.build()
    return MapMergeCsvParser(pairParser)
}
