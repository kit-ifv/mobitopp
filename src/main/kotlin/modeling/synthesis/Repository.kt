package modeling.synthesis

import Builder
import ID
import Identifiable


/**
 * A Repository is a [Resource] if [Identifiable] elements
 * providing random access to elements by [ID].
 *
 * @param E the generic type of [Identifiable] elements
 */
interface Repository<E>: Resource<E> where E: Identifiable<E> {

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
    fun getById(id: ID<E>): E?

    companion object {

        /**
         * Create a [Repository] from the given [Resource].
         *
         * @param resource the resource to be converted into a repository.
         * @param E the generic type of [Identifiable] elements
         * @return the new [Repository]
         */
        fun <E> from(resource: Resource<E>): Repository<E> where E: Identifiable<E>
                = MapRepository(resource.elements.toList(), name=resource.name, source=resource.source)

        /**
         * Create a [Repository] from the given [Sequence].
         *
         * @param name name of the repository
         * @param source source description of the repository
         * @param resource the sequence to be converted into a repository.
         * @param E the generic type of [Identifiable] elements
         * @return the new [Repository]
         */
        fun <E> from(name: String, source: String, resource: Sequence<E>): Repository<E> where E: Identifiable<E>
                = MapRepository(resource.toList(), name, source=source)
    }

} //TODO think about add operation?

/**
 * A MapRepository is a [Map]-based [Repository] implementation.
 *
 * @param E the generic type of contained [Identifiable] elements
 * @param elements the elements contained in the [Repository]
 * @property name the name of the repository
 * @property source the source description of the repository
 */
class MapRepository<E>(
    elements: Collection<E>,
    override val name: String,
    override val source: String,
): Repository<E> where E: Identifiable<E> {
    private val idMap: Map<ID<E>, E> = elements.associateBy { it.id }

    override val size: Int
        get() = idMap.size
    override val elements: Sequence<E>
        get() = idMap.values.asSequence()

    override fun getById(id: ID<E>): E? = idMap[id]

    override fun map(operation: String, mapping: (E) -> E?): Resource<E> =
        super.map(operation, mapping).let { MapRepository(it.elements.toList(), it.name, it.source) }

    override fun filter(operation: String, predicate: (E) -> Boolean): Resource<E> =
        super.filter(operation, predicate).let { MapRepository(it.elements.toList(), it.name, it.source) }

    override fun toString() = "MapRepository[$name] ($source)"
}

/**
 * LateInitRepository is a [Repository] implementation that allows
 * for late initialization of the repository elements.
 *
 * @param E the generic type of contained [Identifiable] elements
 * @constructor creates an [RepositoryState.UNINITIALIZED] LateInitRepository
 */
open class LateInitRepository<E>: Repository<E> where E: Identifiable<E> {
    protected var delegate: Repository<E>? = null
    protected var internalState: RepositoryState = RepositoryState.UNINITIALIZED

    override val name: String
        get() {
            internalState = internalState.getName()
            return delegate!!.name
        }

    override val source: String
        get() {
            internalState = internalState.getSource()
            return delegate!!.source
        }

    val state: RepositoryState
        get() = internalState

    override val elements: Sequence<E>
        get() {
            internalState = internalState.getElements()
            return delegate!!.elements
        }

    override fun getById(id: ID<E>): E? {
        internalState = internalState.getById()
        return delegate!!.getById(id)
    }

    /**
     * Initialize the repository elements with the given [Resource]
     *
     * @param resource the resource of [Identifiable] elements to be added to this [Repository]
     */
    open fun initialize(resource: Resource<E>) {
        internalState = internalState.finalized(resource)
        delegate = resource.asRepository()
    }

    override fun toString() = state.toString(this)

}


/**
 * BuilderRepository is a [LateInitRepository] that can manage
 * [Builder]s before building them into the repository's elements.
 *
 * @param B the generic type of the [Builder]s
 * @param E the generic type of the [Identifiable] elements
 * @constructor Create an uninitialized [BuilderRepository]
 */
class BuilderRepository<B, E>(): LateInitRepository<E>() where B: Builder<E>, E: Identifiable<E> {
    private var builders: Resource<B>? = null

    constructor(builders: Resource<B>): this() {
        this.initializeBuilders(builders)
    }

    override val name: String
        get() {
            internalState = internalState.getName()
            return delegate?.name ?: builders!!.name
        }

    override val source: String
        get() {
            internalState = internalState.getSource()
            return delegate?.source ?: builders!!.source
        }

    /** Build */
    fun build() {
        internalState = internalState.build()
        delegate = builders!!.build()
        builders = null
    }

    /**
     * Reduce
     *
     * @param operation
     * @param predicate
     * @receiver
     */
    fun reduce(operation: String, predicate: (B) -> Boolean) {
        internalState.reduce(operation)
        builders = builders!!.filter(operation, predicate)
    }

    /**
     * Update
     *
     * @param operation
     * @param mapping
     * @receiver
     *///TODO replace vs update
    fun update(operation: String, mapping: (B) -> B?) {
        internalState.update(operation)
        builders = builders!!.map(operation, mapping)
    }

    /**
     * Initialize builders
     *
     * @param builders
     */
    fun initializeBuilders(builders: Resource<B>) {
        internalState = internalState.initialize(builders)
        this.builders = builders
    }


    override fun map(operation: String, mapping: (E) -> E?): Resource<E> =
        throw UnsupportedOperationException("map for target elements not supported")

    override fun filter(operation: String, predicate: (E) -> Boolean): Resource<E> =
        throw UnsupportedOperationException("filter for target elements not supported")

}

/**
 * Repository state
 *
 * @constructor Create empty Repository state
 */
enum class RepositoryState {
    /**
     * Uninitialized [RepositoryState]:
     * the elements of the repository have not been set.
     */
    UNINITIALIZED {
        override fun finalized(resource: Resource<*>) = FINISHED

        override fun initialize(resource: Resource<*>) = INITIALIZED

        override fun update(operation: String) =
            error("Cannot apply mapping '$operation' to repository elements as it has not been initialized yet!")

        override fun reduce(operation: String) =
            error("Cannot apply filter '$operation' to repository elements as it has not been initialized yet!")

        override fun build() =
            error("Cannot build repository elements as it has not been initialized yet!")

        override fun getElements() =
            error("Cannot get elements of repository as it has not been initialized yet!")

        override fun getById() =
            error("Cannot get element by id in repository as it has not been initialized yet!")

        override fun getName() =
            error("Cannot get name of repository as it has not been initialized yet!")

        override fun getSource() =
            error("Cannot get source of repository as it has not been initialized yet!")

        override fun toString(repository: LateInitRepository<*>): String {
            check(repository.state == UNINITIALIZED)
            return "Uninitialized ${repository.javaClass.simpleName}"
        }
    },

    /**
     * Initialized [RepositoryState]:
     * the element-builders of the repository have been set but the elements have not been built yet.
     */
    INITIALIZED { //TODO rename to BUILDING -> requires rewriting all error messages and some tests :(
        override fun finalized(resource: Resource<*>) =
            error("Cannot finalize repository with final resource $resource as it has already been initialized!")

        override fun initialize(resource: Resource<*>) =
            error("Cannot init repository with $resource as it has already been initialized!")

        override fun update(operation: String) = INITIALIZED

        override fun reduce(operation: String) = INITIALIZED

        override fun build() = FINISHED

        override fun getElements() =
            error("Cannot get elements of repository as it has not been finished yet!")

        override fun getById() =
            error("Cannot get element by id in repository as it has not been finished yet!")

        override fun getName() = INITIALIZED

        override fun getSource() = INITIALIZED

        override fun toString(repository: LateInitRepository<*>): String {
            check(repository.state == INITIALIZED)
            return "Initialized ${repository.javaClass.simpleName}[${repository.name}] (${repository.source})"
        }

    },

    /**
     * Finished [RepositoryState]: the elements of the repository have been set.
     */
    FINISHED { //TODO rename to INITIALIZED
        override fun finalized(resource: Resource<*>) =
            error("Cannot finalize repository with final resource $resource as it has already been finished!")

        override fun initialize(resource: Resource<*>) =
            error("Cannot init repository with $resource as it has already been finished!")

        override fun update(operation: String) =
            error("Cannot apply finished '$operation' to repository elements as it has already been finished!")

        override fun reduce(operation: String) =
            error("Cannot apply finished '$operation' to repository elements  it has already been finished!")

        override fun build() =
            error("Cannot build repository as it has already been finished!")

        override fun getElements() = FINISHED

        override fun getById() = FINISHED

        override fun getName() = FINISHED

        override fun getSource() = FINISHED

        override fun toString(repository: LateInitRepository<*>): String {
            check(repository.state == FINISHED)
            return "Finished ${repository.javaClass.simpleName}[${repository.name}] (${repository.source})"
        }

    };

    /**
     * Finalized
     *
     * @param resource
     * @return
     */
    abstract fun finalized(resource: Resource<*>): RepositoryState

    /**
     * Initialize
     *
     * @param resource
     * @return
     */
    abstract fun initialize(resource: Resource<*>): RepositoryState

    /**
     * Update
     *
     * @param operation
     * @return
     */
    abstract fun update(operation: String): RepositoryState

    /**
     * Reduce
     *
     * @param operation
     * @return
     */
    abstract fun reduce(operation: String): RepositoryState

    /**
     * Build
     *
     * @return
     */
    abstract fun build(): RepositoryState

    /**
     * Get elements
     *
     * @return
     */
    abstract fun getElements(): RepositoryState

    /**
     * Get by id
     *
     * @return
     */
    abstract fun getById(): RepositoryState

    /**
     * Get name
     *
     * @return
     */
    abstract fun getName(): RepositoryState

    /**
     * Get source
     *
     * @return
     */
    abstract fun getSource(): RepositoryState

    /**
     * To string
     *
     * @param repository
     * @return
     */
    abstract fun toString(repository: LateInitRepository<*>): String

}