package core.modelsteps.resources

import utils.csv.CsvParser
import utils.csv.CsvReader
import utils.csv.SEMICOLON
import java.nio.file.Path

/**
 * A Resource represents a data source for a stream of elements.
 * It also provides metadata including name and description of the source.
 *
 * @param E the generic type of the elements provided by the source
 */
interface Resource<out E> {
    /** The name of the resource. */
    val name: String

    /** The description of the resource source (e.g., file path, database query). */
    val source: String

    /** A sequence of elements provided by this resource. */
    val elements: Sequence<E>
}

/**
 * A [Resource] based on a [Sequence] of elements.
 *
 * @param E the generic type of elements
 * @property name the resources name
 * @property source the resources description
 * @property elements a sequence of elements
 */
data class SequenceResource<out E>(
    override val name: String,
    override val source: String,
    override val elements: Sequence<E>
) : Resource<E> {
    /**
     * @return A string representation containing name and source.
     */
    override fun toString() = "$name ($source)"
}

/**
 * A [Resource] that lazily initializes its elements using a provided lambda.
 *
 * @param E the generic type of elements
 * @property name the resources name
 * @property source the resources description
 * @param lambda a function to create the sequence of elements
 */
data class LazyResource<out E>(
    override val name: String,
    override val source: String,
    private val lambda: () -> Sequence<E>,
) : Resource<E> {
    /** The lazily initialized sequence of elements. */
    override val elements by lazy { lambda() }
}

/**
 * Creates a [Resource] containing the elements of the sequence and the given metadata.
 *
 * @receiver The sequence of elements to be wrapped.
 * @param S the sequence type
 * @param E the element type
 * @param name the name of the resource
 * @param source the source description of the resource
 * @return a [SequenceResource] instance
 */
fun <S, E> S.asResource(name: String, source: String): Resource<E> where S : Sequence<E> =
    SequenceResource(name, source, this)

/**
 * Creates a [Resource] containing the elements of the iterator and the given metadata.
 *
 * @receiver The iterable collection of elements to be wrapped.
 * @param I the iterable type
 * @param E the element type
 * @param name the name of the resource
 * @param source the source description of the resource
 * @return a [SequenceResource] instance
 */
fun <I, E> I.asResource(name: String, source: String): Resource<E> where I : Iterable<E> =
    SequenceResource(name, source, this.asSequence())

/**
 * A resource providing data from a CSV file.
 * The file is only read and parsed once.
 * The created entities are stored in the resource for future access.
 *
 * @param E the generic type of entities created from the CSV data
 * @property path the path to the CSV file to be parsed
 * @property parser the parser to be applied
 * @property delimiter the CSV delimiter, defaults to ';'
 * @property reusable if true, the resource can be read multiple times by storing elements in memory
 */
class CsvResource<E>(
    val path: Path,
    val parser: CsvParser<E>,
    private val delimiter: String = SEMICOLON,
    private val reusable: Boolean = false,
) : Resource<E> {
    /** The name of the CSV file. */
    override val name: String
        get() = path.fileName.toString()

    /** The string representation of the file path. */
    override val source: String
        get() = path.toString()

    /** The sequence of entities parsed from the CSV file. */
    override val elements: Sequence<E>
        get() = rowSequence.elements

    private val rowSequence by lazy {
        parser.parse(CsvReader.of(path, delimiter))
            .asResource(name, source).let {
                if (reusable) {
                    it.reusable()
                } else {
                    it
                }
            }
    }

    /**
     * @return A string representation of the CSV resource.
     */
    override fun toString() = "CSV $name ($source)"
}

/**
 * A [ReusableResource] is a [Resource] decorator that stores the elements
 * of the provided sequence when it is computed for the first time (lazy).
 * This decorator provides the stored elements as a new sequence unlimited times.
 *
 * @param E the generic type of provided entities
 * @property delegate the resource that should be made reusable
 */
class ReusableResource<E>(
    private val delegate: Resource<E>
) : Resource<E> by delegate {
    private var storage: List<E>? = null

    /**
     * Returns a sequence of elements. If they have already been read once,
     * they are returned from an internal memory storage.
     */
    override val elements: Sequence<E>
        get() {
            val list = mutableListOf<E>()
            return storage?.asSequence()
                ?: delegate.elements.onEach { list.add(it) }.also { storage = list }
        }
}

/**
 * Decorates a [Resource] to make it reusable by storing its elements in memory after the first read.
 *
 * @receiver The resource to be made reusable.
 * @param R the resource type
 * @param E the element type
 * @return a [ReusableResource] instance
 */
fun <R, E> R.reusable(): Resource<E> where R : Resource<E> = ReusableResource(this)
