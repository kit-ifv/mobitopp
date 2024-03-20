package modeling.synthesis

import Builder
import Identifiable
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
interface Resource<E> {
    val name: String
    val source: String
    val elements: Sequence<E>

    /**
     * Create a [Resource] with elements produced by applying the given mapping to this resource.
     * Also, the metadata are updated:
     *   - the resource name should be maintained
     *   - the source description is updated to log the applied mapping
     *
     * @param operation name of the mapping operation to be applied
     * @param mapping the operation to be applied
     * @return a [Resource] with mapped elements and updated metadata
     */
    fun map(operation: String, mapping: (E) -> E?): Resource<E> =
        SequenceResource(name, "$source -> map $operation", elements.map(mapping).filterNotNull() )

    /**
     * Create a [Resource] with filtered elements.
     * Also, the metadata are updated:
     *   - the resource name should be maintained
     *   - the source description is updated to log the applied filter
     *
     * @param operation name of the filter operation to be applied
     * @param predicate the filter predicate to be applied
     * @return a [Resource] with filtered elements and updated metadata
     */
    fun filter(operation: String, predicate: (E) -> Boolean): Resource<E> =
        SequenceResource(name, "$source -> filter $operation", elements.filter(predicate) )

    /**
     * Create a [Resource] containing both the elements of this [Resource] and the given other [Resource].
     * Also, the metadata are updated:
     *   - the resource names are combined
     *   - the source description is updated to log the applied merge
     *
     * @param other the other resource to be merged with this resource
     * @return a [Resource] with filtered elements and updated metadata
     */
    fun merge(other: Resource<E>): Resource<E> {
        val merged = sequenceOf(elements, other.elements).flatten()

        return SequenceResource(
            resourceName = "$name, ${other.name}",
            description = "$source -> merge with ${other.source}",
            merged
        )
    }

}

/**
 * A [Resource] based on a [Sequence] of elements.
 *
 * @param E the generic type of elements
 * @constructor Create a [SequenceResource] with the given metadata and elements
 * @property name the resources name
 * @property description the resources description
 * @property sequence a sequence of elements
 */
class SequenceResource<E>(
    private var resourceName: String,
    private var description: String,
    private var sequence: Sequence<E>
) : Resource<E> {

    override val name: String
        get() = resourceName

    override val source: String
        get() = description


    override val elements: Sequence<E>
        get() = sequence

    override fun map(operation: String, mapping: (E) -> E?): Resource<E> {
        sequence = sequence.map(mapping).filterNotNull()
        description += " -> map $operation"
        return this
    }

    override fun filter(operation: String, predicate: (E) -> Boolean): Resource<E> {
        sequence = sequence.filter(predicate)
        description += " -> filter $operation"
        return this
    }

    override fun merge(other: Resource<E>): Resource<E> {
        sequence = sequenceOf(sequence, other.elements).flatten()
        resourceName = "$resourceName, ${other.name}"
        description += " -> merge with ${other.source}"
        return this
    }

    override fun toString() = "$name ($source)"

}

/**
 * Build all [Builder] elements and create a [Repository].
 * The build step is logged in metadata of the [Repository].
 *
 * @param R generic type of the resource
 * @param B generic type of the builder
 * @param E generic type of the elements
 * @return a repository containing built elements and updated metadata of the resource
 */
fun <R, B, E> R.build(): Repository<E> where B:Builder<E>, R:Resource<B>, E:Identifiable<E> =
    Repository.from(this.name, "$source -> build", this.elements.map { it.build() })

/**
 * Create a [Repository] containing the elements and metadata of the given [Resource].
 *
 * @param R generic type of the resource
 * @param E generic type of the elements
 * @return a repository containing elements and metadata of the resource
 */
fun <R, E> R.asRepository(): Repository<E> where R: Resource<E>, E:Identifiable<E> =
    Repository.from(this)

/**
 * Create a [Repository] containing the given elements and metadata.
 *
 * @param name the repository name
 * @param source
 * @param S
 * @param E
 * @return
 */
fun <S, E> S.asRepository(
    name: String,
    source: String
): Repository<E> where S: Sequence<E>, E:Identifiable<E> =
    Repository.from(name, source, this)


/**
 * Csv resource
 *
 * @param E
 * @constructor Create empty Csv resource
 * @property file
 * @property parser
 * @property delimiter
 */
class CsvResource<E> (
    val file: File,
    val parser: CsvParser<E>,
    val delimiter: String = SEMICOLON

): Resource<E> {
    override val name: String
        get() = file.name

    override val source: String
        get() = file.path

    override val elements: Sequence<E>
        get() {
            val list = mutableListOf<E>()
            return storage?.asSequence() ?:
                parser.parse(CsvReader.of(file, delimiter)).onEach { list.add(it) }.also { storage=list }
        }

    private var storage: List<E>? = null

    override fun toString() = "CSV $name ($source)"

}
