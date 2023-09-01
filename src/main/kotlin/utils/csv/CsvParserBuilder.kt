package utils.csv

class CsvParserBuilder<E>(
    private val entitySpawner: (Int) -> E
) {
    private val columnParsers: MutableMap<String, (E, String) -> Unit> = mutableMapOf()


    fun addStringColumn(column: String, setter: (E, String) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = setter
        return this
    }

    fun addByteColumn(column: String, setter: (E, Byte) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = {e, s -> setter(e, s.toByte())}
        return this
    }

    fun addShortColumn(column: String, setter: (E, Short) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = {e, s -> setter(e, s.toShort())}
        return this
    }

    fun addIntColumn(column: String, setter: (E, Int) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = {e, s -> setter(e, s.toInt())}
        return this
    }

    fun addLongColumn(column: String, setter: (E, Long) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = {e, s -> setter(e, s.toLong())}
        return this
    }

    fun addFlotColumn(column: String, setter: (E, Float) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = {e, s -> setter(e, s.toFloat())}
        return this
    }

    fun addDoubleColumn(column: String, setter: (E, Double) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = {e, s -> setter(e, s.toDouble())}
        return this
    }

    fun addBooleanColumn(column: String, setter: (E, Boolean) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] =
            { e, s -> setter(e, s.toBoolean()) } //TODO allow other encoding of true (currently "true")
        return this
    }

    fun <T> addTypedColumn(column: String, convert: (String) -> T,  setter: (E, T) -> Unit): CsvParserBuilder<E> {
        columnParsers[column] = {e, s -> setter(e, convert(s))}
        return this
    }

    fun build() : CsvParser<E> {
        return DefaultRowCsvParser(entitySpawner, columnParsers)
    }

}

fun <P, K, V> P.buildMapParser(
): MapCsvParser<K, V>
where P: CsvParserBuilder<Pair<K, V>> {

    val pairParser = this.build()
    return DefaultMapCsvParser(pairParser)
}

fun <P, K, V> P.buildMergeMapParser(
): MapCsvParser<K, List<V>>
        where P: CsvParserBuilder<Pair<K, V>> {

    val pairParser = this.build()
    return MapMergeCsvParser(pairParser)
}