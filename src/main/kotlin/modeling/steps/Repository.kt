package modeling.steps

import utils.Builder
import utils.Identifiable

/**
 * A Repository is a [Resource] with [Identifiable] elements
 * providing random access to elements by id.
 *
 * @param E the generic type of [Identifiable] elements
 * @param I the generic id type
 */
interface Repository<out E, I> : Resource<E> where E : Identifiable<I> {

    /** The repository size. */
    val size: Int
        get() = elements.count()

    /**
     * Get an element from the repository with the given id
     * or null in case the element is absent.
     *
     * @param id the query id
     * @return the element with the given id or null
     */
    fun getById(id: I): E?
}

/**
 * A MapRepository is a [Map]-based [Repository] implementation.
 *
 * @param E the generic type of contained [Identifiable] elements
 * @param elements the elements contained in the [Repository]
 * @property name the name of the repository
 * @property source the source description of the repository
 */
class MapRepository<out E, I>(
    elements: Collection<E>,
    override val name: String,
    override val source: String,
) : Repository<E, I> where E : Identifiable<I> {
    private val idMap: Map<I, E> = elements.associateBy { it.id }

    constructor(resource: Resource<E>) : this(
        elements = resource.elements.toList(),
        name = resource.name,
        source = resource.source
    )

    override val size: Int
        get() = idMap.size
    override val elements: Sequence<E>
        get() = idMap.values.asSequence()

    override fun getById(id: I): E? = idMap[id]

    override fun toString() = "MapRepository[$name] ($source)"
}

/**
 * RepositoryBuilder is a [Repository] that can manage
 * [Builder]s before building them into the repository's elements.
 *
 * @param B the generic type of the [Builder]s
 * @param E the generic type of the [Identifiable] elements
 * @param I the generic type of ids used by the built elements
 * @constructor Create an [RepositoryState.UNINITIALIZED] [RepositoryBuilder]
 */
open class RepositoryBuilder<B, out E, I>() : Repository<E, I> where B : Builder<E>, E : Identifiable<I> {
    private var builders: Resource<B>? = null
    private var elems: Repository<E, I>? = null
    private var internalState: RepositoryState = RepositoryState.UNINITIALIZED

    /**
     * Create a [RepositoryBuilder] initialized with the given builders.
     *
     * @param builders a resource of initial builders
     * @constructor Create a [RepositoryState.PREPARING] [RepositoryBuilder]
     */
    constructor(builders: Resource<B>) : this() {
        this.addBuilders(builders)
    }

    override val name: String
        get() {
            internalState = internalState.getName()
            return (elems ?: builders)!!.name
        }

    override val source: String
        get() {
            internalState = internalState.getSource()
            return (elems ?: builders)!!.source
        }

    /**
     * The current [RepositoryState] of the RepositoryBuilder.
     * It can be: uninitialized, preparing or finished.
     */
    val state: RepositoryState
        get() = internalState

    override val elements: Sequence<E>
        get() {
            internalState = internalState.getElements()
            return elems!!.elements
        }

    override fun getById(id: I): E? {
        internalState = internalState.getById()
        return elems!!.getById(id)
    }

//    /**
//     * Prepare the repository by adding the [Builder]s from the given [Resource].
//     * The prepare step is logged in the metadata of the [Repository].
//     *
//     * @param builders the builders to be added to the [RepositoryBuilder]
//     *///TODO duplicate to merge builders
//    fun prepare(builders: Resource<B>) {
//        internalState = internalState.prepare(builders)
//        this.builders = builders
//    }

    /**
     * Filter the set of [Builder]s using the given filter condition.
     * The filter step is logged in the metadata of the [Repository].
     *
     * @param operation name of the filter operation
     * @param predicate the filter condition
     */
    fun filter(operation: String, predicate: (B) -> Boolean) {
        internalState.filter(operation)
        builders = builders!!.let {
            it.elements.filter(predicate).asResource(it.name, "${it.source} -> filter $operation")
        }
    }

    /**
     * Update the set of [Builder]s by applying the given mapping to each of them.
     * The update step is logged in the metadata of the [Repository].
     *
     * @param operation the name of the mapping operation (used for tracking)
     * @param mapping the mapping operation to be applied
     */
    fun update(operation: String, mapping: (B) -> B?) {
        internalState.update(operation)
        builders = builders!!.let {
            it.elements.map(mapping).filterNotNull().asResource(it.name, "${it.source} -> map $operation")
        }
    }

    /**
     * Update the set of [Builder]s by mapping the entire set in one chunk.
     * The update all step is logged in the metadata of the [Repository].
     *
     * @param operation the name of the mapping operation (used for tracking)
     * @param mapping the mapping operation to be applied
     */
    fun updateAll(operation: String, mapping: (Sequence<B>) -> Sequence<B>) {
        internalState.update(operation)
        builders = builders!!.let {
            mapping(it.elements).asResource(it.name, "${it.source} -> map all $operation")
        }
    }

    /**
     * Add the [Builder]s from the given [Resource] to the [RepositoryBuilder].
     * If no builders were added yet, the given resource is used as initial set of [Builder]s.
     * The add builders step is logged in the metadata of the [Repository].
     *
     * @param other a resource of builders to be added to the [RepositoryBuilder]
     */
    fun addBuilders(other: Resource<B>) {
        internalState = internalState.addBuilders(other)
        builders = builders?.let {
            sequenceOf(it.elements, other.elements).flatten().asResource(
                name = "${it.name}, ${other.name}",
                source = "${it.source} -> merge with ${other.source}"
            )
        } ?: other
    }

    /** Finish the Repository by building all [Builder]s to create the final entities.  */
    fun build() {
        internalState = internalState.build()
        elems = MapRepository(builders!!.build().reusable())
        builders = null
    }

    /** Reset the [RepositoryBuilder] to the uninitialized state. */
    fun reset() {
        internalState = internalState.reset()
        elems = null
        builders = null
    }

    override fun toString() = state.toString(this)
}

/**
 * Repository state represents the state of a [RepositoryBuilder].
 * It can be:
 * - uninitialized: no [Builder]s were added yet; update/reduce/build and access to [Resource] metadata is not supported
 * - preparing: some [Builder]s were added; access to final elements is not supported
 * - finished: [Builder]s were transformed to elements: adding [Builder]s is not supported
 * @constructor Create empty Repository state
 */
@Suppress("TooManyFunctions")
enum class RepositoryState {
    /**
     * Uninitialized [RepositoryState]:
     * neither the elements not the element-builders of the repository have not been set.
     */
    UNINITIALIZED {

        override fun update(operation: String) =
            error("Cannot apply mapping '$operation' to repository elements as it has not been prepared yet!")

        override fun filter(operation: String) =
            error("Cannot apply filter '$operation' to repository elements as it has not been prepared yet!")

        override fun build() =
            error("Cannot build repository elements as it has not been prepared yet!")

        override fun getElements() =
            error("Cannot get elements of repository as it has not been finished yet!")

        override fun getById() =
            error("Cannot get element by id in repository as it has not been finished yet!")

        override fun getName() =
            error("Cannot get name of repository as it has not been finished/prepared yet!")

        override fun getSource() =
            error("Cannot get source of repository as it has not been finished/prepared yet!")

        override fun toString(repository: RepositoryBuilder<*, *, *>): String {
            check(repository.state == UNINITIALIZED)
            return "Uninitialized ${repository.javaClass.simpleName}"
        }

        override fun addBuilders(resource: Resource<*>) = PREPARING
    },

    /**
     * Initialized [RepositoryState]:
     * the element-builders of the repository have been added but the elements have not been built yet.
     */
    PREPARING {

        override fun update(operation: String) = PREPARING

        override fun filter(operation: String) = PREPARING

        override fun build() = FINISHED

        override fun getElements() =
            error("Cannot get elements of repository as it has not been built yet!")

        override fun getById() =
            error("Cannot get element by id in repository as it has not been built yet!")

        override fun getName() = PREPARING

        override fun getSource() = PREPARING

        override fun toString(repository: RepositoryBuilder<*, *, *>): String {
            check(repository.state == PREPARING)
            return "Initialized ${repository.javaClass.simpleName}[${repository.name}] (${repository.source})"
        }

        override fun addBuilders(resource: Resource<*>) = PREPARING
    },

    /**
     * Finished [RepositoryState]: the elements of the repository have been built/finalized.
     */
    FINISHED {

        override fun update(operation: String) =
            error("Cannot apply finished '$operation' to repository elements as it has already been finished!")

        override fun filter(operation: String) =
            error("Cannot apply finished '$operation' to repository elements  it has already been finished!")

        override fun build() =
            error("Cannot build repository as it has already been finished!")

        override fun getElements() = FINISHED

        override fun getById() = FINISHED

        override fun getName() = FINISHED

        override fun getSource() = FINISHED

        override fun toString(repository: RepositoryBuilder<*, *, *>): String {
            check(repository.state == FINISHED)
            return "Finished ${repository.javaClass.simpleName}[${repository.name}] (${repository.source})"
        }

        override fun addBuilders(resource: Resource<*>) =
            error("Cannot merge Builders of resource $resource into repository as it has already been finished!")
    };

    abstract fun update(operation: String): RepositoryState
    abstract fun filter(operation: String): RepositoryState
    abstract fun addBuilders(resource: Resource<*>): RepositoryState
    abstract fun build(): RepositoryState
    fun reset() = UNINITIALIZED

    abstract fun getElements(): RepositoryState
    abstract fun getById(): RepositoryState
    abstract fun getName(): RepositoryState
    abstract fun getSource(): RepositoryState
    abstract fun toString(repository: RepositoryBuilder<*, *, *>): String
}

/**
 * Build all [Builder]s and create a [Repository].
 * The build step is logged in the metadata of the [Repository].
 *
 * @param R generic type of the resource
 * @param B generic type of the builder
 * @param E generic type of the elements
 * @return a repository containing built elements and updated metadata of the resource
 */
fun <R, B, E> R.build(): Resource<E> where B : Builder<E>, R : Resource<B>, E : Identifiable<*> =
    this.elements.map { it.build() }.asResource(this.name, "$source -> build")

/**
 * Create a [Repository] containing the elements and metadata of the given [Resource].
 *
 * @param R generic type of the resource
 * @param E generic type of the elements
 * @return a repository containing elements and metadata of the resource
 */
fun <R, E, I> R.asRepository(): Repository<E, I> where R : Resource<E>, E : Identifiable<I> =
    MapRepository(this.elements.toList(), this.name, this.source)
