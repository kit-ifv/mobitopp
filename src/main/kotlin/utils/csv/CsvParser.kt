package utils.csv

import CodePlan
import Encodable
import ID
import utils.ErrorHandling
import utils.units.CurrencyUnits
import utils.units.DistanceUnit
import utils.units.euros
import utils.units.kilometers
import utils.units.meters
import utils.units.toCurrency
import utils.units.toDistance
import java.io.File

/**
 * The CsvParser interface defines methods for parsing a csv file and
 * generating a Sequence of entities of generic type E.
 *
 * It provides a default implementation for parsing a file path or File
 * object for convenience.
 *
 * @param E the generic type of the entities to be parsed
 */
interface CsvParser<E> {

    /**
     * Parse the csv file at the given path.
     *
     * @param path the file path of the csv file to be parsed
     * @return a sequence of parsed entities
     */
    fun parse(path: String, separator: String = SEMICOLON): Sequence<E> {
        return parse(File(path), separator)
    }

    /**
     * Parse the given file as csv.
     *
     * @param file the csv file to be parsed
     * @return a sequence of parsed entities
     */
    fun parse(file: File, separator: String = SEMICOLON): Sequence<E> {
        val csv = CsvReader.of(file, separator)
        return parse(csv)
    }

    /**
     * Parse the csv data provided by the given CsvReader.
     *
     * @param csv the csv reader providing the csv data to be parsed
     * @return a sequence of parsed entities
     */
    fun parse(csv: CsvReader): Sequence<E>

}

/**
 * A CsvParser parsing each csv Row as a single entity.
 *
 * @param E the generic type of the entities to be parsed
 */
abstract class RowCsvParser<E> : CsvParser<E> {
    //TODO add validation: check if all required columns are available

    override fun parse(csv: CsvReader): Sequence<E> {
        return csv.rows().map { parse(it) }.filterNotNull()
    }

    /**
     * Parse the given Row as entity of generic type E.
     *
     * May return null, if an error occurs while parsing the row.
     *
     * @param row the row to be parsed as entity
     * @return parsed entity, may be null if parsing produces errors
     */
    abstract fun parse(row: Row): E?

}


/**
 * A default implementation of RowCsvParser.
 *
 * @param E the generic type of entities to be produced by the parser
 * @constructor create a row based csv parser with the given mapping
 * @property exceptionHandling the exception handling strategy to be used
 *     when parsing errors occur
 * @property mapping the mapping applied to the row to generate an entity
 */
open class DefaultRowCsvParser<E>(
    protected val exceptionHandling: ErrorHandling = ErrorHandling.WARNING,
    protected val mapping: (Row) -> E,
) : RowCsvParser<E>() {

    override fun parse(row: Row): E? {
        return exceptionHandling.handleParseRow(row) {
            mapping(ErrorHandlingRow(row, exceptionHandling.accumulating()))
        }
    }

}

/**
 * A RowCsvParser for parsing values of a single column. This is
 * for convenience, as no entity spawner has to be provided as in
 * [DefaultRowCsvParser].
 *
 * @param E type of the values to be parsed
 * @constructor create a row based csv parser for the given column, parser
 *     and [ErrorHandling] strategy
 */
open class CsvValueParser<E>(
    valueColumn: String,
    exceptionHandling: ErrorHandling = ErrorHandling.WARNING,
    parser: (String) -> E,
) : DefaultRowCsvParser<E>(
    exceptionHandling,
    mapping = { row -> row(valueColumn, parser) },
)


// UTILITY

/**
 * A [Row]-wrapper that applies error handling when parsing/getting values.
 *
 * @property row the row to be wrapped
 * @property errorHandling the [ErrorHandling] strategy to be applied in
 *     case of errors
 */
class ErrorHandlingRow(
    private val row: Row,
    private val errorHandling: ErrorHandling
) : Row by row {

    override fun get(column: String): String {
        return errorHandling.handleParseValue(row, column) { s -> s }!!
    }

    override operator fun <T> invoke(column: String, converter: (String) -> T): T {
        return errorHandling.handleParseValue(row, column, converter)!!
    }

}

/**
 * A [Row]-wrapper providing a parser for a certain type [T] to be applied
 * when values are obtained from the row.
 *
 * @param T the generic result type of the parser
 * @property row the wrapped row
 * @property parser the parser to be applied when getting values
 */
class TypedRow<T>(
    val row: Row,
    val parser: (String) -> T
) {

    fun <K> wrap(transformation: (T) -> K): TypedRow<K> {
        return TypedRow(row) {
            transformation(parser(it))
        }
    }

    operator fun get(column: String): T {
        return row(column, parser)
    }

}

fun Row.byte() = TypedRow(this, String::toByte)
fun Row.short() = TypedRow(this, String::toShort)
fun Row.int() = TypedRow(this, String::toInt)
fun Row.long() = TypedRow(this, String::toLong)
fun Row.float() = TypedRow(this, String::toFloat)
fun Row.double() = TypedRow(this, String::toDouble)
fun Row.boolean() = TypedRow(this, String::toBoolean)
fun <E> Row.id() = TypedRow(this) { s -> ID<E>(s.toULong()) }

fun <T: Encodable> Row.decode(codePLan: CodePlan<T>) = TypedRow(this) { s ->  codePLan.decode(s.toInt()) }


fun <I> I.distance(unit: DistanceUnit) where I: TypedRow<Int> = this.wrap { it.toDistance(unit) }
//fun <L> L.distance(unit: DistanceUnit) where L: TypedRow<Long> = this.wrap { it.toDistance(unit) }
//fun <D> D.distance(unit: DistanceUnit) where D: TypedRow<Double> = this.wrap { it.toDistance(unit) }

fun <I> I.meters() where I: TypedRow<Int> = this.wrap { it.meters }
//fun <L> L.meters() where L: TypedRow<Long> = this.wrap { it.meters }
//fun <D> D.meters() where D: TypedRow<Double> = this.wrap { it.meters }

fun <I> I.kilometers() where I: TypedRow<Int> = this.wrap { it.kilometers }
//fun <L> L.kilometers() where L: TypedRow<Long> = this.wrap { it.kilometers }
//fun <D> D.kilometers() where D: TypedRow<Double> = this.wrap { it.kilometers }

fun <I> I.currency(unit: CurrencyUnits) where I: TypedRow<Int> = this.wrap { it.toCurrency(unit) }
//fun <L> L.currency(unit: CurrencyUnits) where L: TypedRow<Long> = this.wrap { it.toCurrency(unit) }
//fun <D> D.currency(unit: CurrencyUnits) where D: TypedRow<Double> = this.wrap { it.toCurrency(unit) }

fun <I> I.euros() where I: TypedRow<Int> = this.wrap { it.euros }
class Target(val id: Int)
fun main() {


   val file =
        "\\\\ifv-fs.ifv.kit.edu\\Forschung\\Projekte_intern\\mobitopp\\Input\\transmove\\mobitopp-population\\data\\population\\hamburg_base"

        val parser = DefaultRowCsvParser{ row ->
            Target(
                id = row.int()["personId"]
            )
        }

        val targets = parser.parse(File("$file/person.csv"))

        println("Start")
        val res = targets.toList()
        println("Stop")
        println(res.size)


}
