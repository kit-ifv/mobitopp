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
class CsvParserBuilder<E>(
    private val entitySpawner: (Int) -> E
) {
    private val columnParsers: MutableMap<String, (E, String) -> Unit> = mutableMapOf()
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
    fun addStringColumn(column: String, setter: (E, String) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = setter
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
    fun addByteColumn(column: String, setter: (E, Byte) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> setter(e, s.toByte()) }
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
    fun addShortColumn(column: String, setter: (E, Short) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> setter(e, s.toShort()) }
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
    fun addIntColumn(column: String, setter: (E, Int) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> setter(e, s.toInt()) }
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
    fun addLongColumn(column: String, setter: (E, Long) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> setter(e, s.toLong()) }
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
    fun addFloatColumn(column: String, setter: (E, Float) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> setter(e, s.toFloat()) }
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
    fun addDoubleColumn(column: String, setter: (E, Double) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> setter(e, s.toDouble()) }
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
    fun addBooleanColumn(column: String, setter: (E, Boolean) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] =
            { e, s -> setter(e, s.toBoolean()) } //TODO allow other encoding of true (currently "true")
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
    fun <T> addTypedColumn(column: String, convert: (String) -> T, setter: (E, T) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = { e, s -> setter(e, convert(s)) }
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
     * Use parsing error handling: drop rows with errors silently
     *
     * @return
     */
    fun onParseErrorKeepRowWithWarning(): CsvParserBuilder<E> {
        this.errorHandling = ParserErrorHandling.WARN_KEEP
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
