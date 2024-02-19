package utils.csv

import utils.ErrorHandling

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
    private var errorHandling: ErrorHandling = ErrorHandling.WARN_DROP

    val boolean = this to { s: String -> s.toBoolean() } //TODO allow other encoding of true (currently "true")
    val byte = this to { s: String -> s.toByte() }
    val short = this to { s: String -> s.toShort() }
    val int = this to { s: String -> s.toInt() }
    val long = this to { s: String -> s.toLong() }
    val float = this to { s: String -> s.toFloat() }
    val double = this to { s: String -> s.toDouble() }
    val string = this to { s: String -> s }


    /**
     * Add column using the given transform function.
     *
     * @param name the name of the column
     * @param transform a function that takes the parsed value and the entity to
     *     be processed, and returns the modified entity or an entirely new entity
     * @return this builder
     */
    fun addColumn(name: String, transform: (E, String) -> E?): CsvParserBuilder<E> {
        columnParsers[name] = transform
        return this
    }

    /**
     * Add property column using the given setter function.
     *
     * @param name the name of the column
     * @param setter a function that takes the parsed value and the entity to
     *     be processed, it may further transform the string value and should
     *     then modify the entity accordingly (e.g. set a property to the
     *     parsed value)
     * @return this builder
     */
    fun addProperty(name: String, setter: (E, String) -> Unit): CsvParserBuilder<E> {
        columnParsers[name] = { e, s -> e.also { setter(e, s) } }
        return this
    }

    /**
     * On parsing error use the given [ErrorHandling] strategy.
     *
     * @param handling the error handling strategy
     * @return this builder
     */
    fun onErrorUse(handling: ErrorHandling): CsvParserBuilder<E> {
        this.errorHandling = handling
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


fun <P, T, E> P.property(name: String, setter: (E, T) -> Unit )
        where P: Pair<CsvParserBuilder<E>, (String) -> T> =
    this.first.addProperty(name) { e: E, s: String -> e.also { setter(e, this.second(s)) } }

fun <P, T, E> P.column(name: String, transform: (E, T) -> E? )
        where P: Pair<CsvParserBuilder<E>, (String) -> T> =
    this.first.addColumn(name) { e: E, s: String -> transform(e, this.second(s)) }

fun <P, E> P.value(name: String)
        where P: Pair<CsvParserBuilder<E>, (String) -> E> =
    this.first.addColumn(name) { _: E, s: String -> this.second(s) }
