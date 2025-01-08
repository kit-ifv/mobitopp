package modeling.steps

import utils.csv.CsvParser
import utils.csv.CsvReader
import utils.csv.SEMICOLON
import java.io.File

/**
 * A Resource represents a data source for a stream of elements.
 * It also provides metadata including name and description of the source.
 *
 * @param E the generic type of the elements provided by the source
 */
interface Resource<out E> {
    val name: String
    val source: String
    val elements: Sequence<E>
}

/**
 * A [Resource] based on a [Sequence] of elements.
 *
 * @param E the generic type of elements
 * @constructor Create a [SequenceResource] with the given metadata and elements
 *
 * @property name the resources name
 * @property source the resources description
 * @property elements a sequence of elements
 */
data class SequenceResource<out E>(
    override val name: String,
    override val source: String,
    override val elements: Sequence<E>
) : Resource<E> {
    override fun toString() = "$name ($source)"
}

/**
 * Create a [Resource] containing the elements of the sequence and the given metadata.
 */
fun <S, E> S.asResource(name: String, source: String): Resource<E> where S : Sequence<E> =
    SequenceResource(name, source, this)

/**
 * Create a [Resource] containing the elements of the iterator and the given metadata.
 */
fun <I, E> I.asResource(name: String, source: String): Resource<E> where I : Iterable<E> =
    SequenceResource(name, source, this.asSequence())

/**
 * A resource providing data from a csv file.
 * The file is only read and parsed once.
 * The created entities are stored in the resource for future access.
 *
 * @param E the generic type of entities created from the csv data
 * @constructor Create empty Csv resource
 * @property file the csv file to be parsed
 * @property parser th parser to be applied
 * @property delimiter the csv delimiter, defaults to ';'
 */
class CsvResource<E>(
    val file: File,
    val parser: CsvParser<E>,
    private val delimiter: String = SEMICOLON,
    private val reusable: Boolean = false,
) : Resource<E> {
    override val name: String
        get() = file.name

    override val source: String
        get() = file.path

    override val elements: Sequence<E>
        get() = rowSequence.elements

    private val rowSequence by lazy {
        parser.parse(CsvReader.of(file, delimiter))
            .asResource(file.name, file.path).let {
                if (reusable) {
                    it.reusable()
                } else {
                    it
                }
            }
    }

    override fun toString() = "CSV $name ($source)"
}

/**
 * A ReusableResource is a [Resource] decorator that stores the elements
 * of the provided sequence when it is computed for the first time (lazy).
 * This might close the sequence and prevent a second use of the elements.
 * This decorator provides the stored elements as a new sequence unlimited times.
 *
 * @param E the generic type of provided entities
 * @property delegate the resource that should be made reusable
 */
class ReusableResource<E>(
    private val delegate: Resource<E>
) : Resource<E> by delegate {
    private var storage: List<E>? = null

    override val elements: Sequence<E>
        get() {
            val list = mutableListOf<E>()
            return storage?.asSequence()
                ?: delegate.elements.onEach { list.add(it) }.also { storage = list }
        }
}

fun <R, E> R.reusable(): Resource<E> where R : Resource<E> = ReusableResource(this)
