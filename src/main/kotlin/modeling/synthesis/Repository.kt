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

    override fun map(operation: String, mapping: (E) -> E?): Repository<E> =
        super.map(operation, mapping).let { MapRepository(it.elements.toList(), it.name, it.source) }

    override fun filter(operation: String, predicate: (E) -> Boolean): Repository<E> =
        super.filter(operation, predicate).let { MapRepository(it.elements.toList(), it.name, it.source) }

    override fun merge(other: Resource<E>): Repository<E> {
        val merged = super.merge(other)

        return MapRepository(merged.elements.toList(), merged.name, merged.source)
    }

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
        internalState = internalState.finalize(resource)
        delegate = resource.asRepository()
    }

    override fun map(operation: String, mapping: (E) -> E?): Repository<E> =
        throw UnsupportedOperationException("map for target elements not supported")

    override fun filter(operation: String, predicate: (E) -> Boolean): Repository<E> =
        throw UnsupportedOperationException("filter for target elements not supported")

    override fun merge(other: Resource<E>): Repository<E> {
        internalState = internalState.merge(other)
        delegate = (delegate?.merge(other) ?: other).let { MapRepository(it.elements.toList(), it.name, it.source) }
        return delegate!!
    }

    open fun reset() {
        internalState = internalState.reset()
        delegate = null
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
        this.prepare(builders)
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

    fun build() {
        internalState = internalState.build()
        delegate = builders!!.build()
        builders = null
    }

    override fun reset() {
        internalState = internalState.reset()
        delegate = null
        builders = null
    }

    fun reduce(operation: String, predicate: (B) -> Boolean) {
        internalState.reduce(operation)
        builders = builders!!.filter(operation, predicate)
    }

    //TODO replace vs update
    fun update(operation: String, mapping: (B) -> B?) {
        internalState.update(operation)
        builders = builders!!.map(operation, mapping)
    }

    fun prepare(builders: Resource<B>) {
        internalState = internalState.prepare(builders)
        this.builders = builders
    }

//    override fun map(operation: String, mapping: (E) -> E?): Repository<E> =
//        throw UnsupportedOperationException("map for target elements not supported")
//
//    override fun filter(operation: String, predicate: (E) -> Boolean): Repository<E> =
//        throw UnsupportedOperationException("filter for target elements not supported")

    fun mergeBuilders(otherBuilders: Resource<B>) {
        internalState = internalState.mergeBuilders(otherBuilders)
        builders = builders?.merge(otherBuilders) ?: otherBuilders
    }

}

/**
 * Repository state
 *
 * @constructor Create empty Repository state
 */
@Suppress("TooManyFunctions")
enum class RepositoryState {
    /**
     * Uninitialized [RepositoryState]:
     * the elements of the repository have not been set.
     */
    UNINITIALIZED {
        override fun finalize(resource: Resource<*>) = FINISHED

        override fun prepare(resource: Resource<*>) = PREPARING

        override fun update(operation: String) =
            error("Cannot apply mapping '$operation' to repository elements as it has not been prepared yet!")

        override fun reduce(operation: String) =
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

        override fun toString(repository: LateInitRepository<*>): String {
            check(repository.state == UNINITIALIZED)
            return "Uninitialized ${repository.javaClass.simpleName}"
        }

        override fun merge(resource: Resource<*>) = FINISHED

        override fun mergeBuilders(resource: Resource<*>) = PREPARING
    },

    /**
     * Initialized [RepositoryState]:
     * the element-builders of the repository have been set but the elements have not been built yet.
     */
    PREPARING {
        override fun finalize(resource: Resource<*>) =
            error(
                "Cannot initialize repository with final resource " +
                "'$resource' as preparation has already been started!"
            )

        override fun prepare(resource: Resource<*>) =
            error("Cannot prepare repository with '$resource' as preparation has already been started!")

        override fun update(operation: String) = PREPARING

        override fun reduce(operation: String) = PREPARING

        override fun build() = FINISHED

        override fun getElements() =
            error("Cannot get elements of repository as it has not been built yet!")

        override fun getById() =
            error("Cannot get element by id in repository as it has not been built yet!")

        override fun getName() = PREPARING

        override fun getSource() = PREPARING

        override fun toString(repository: LateInitRepository<*>): String {
            check(repository.state == PREPARING)
            return "Initialized ${repository.javaClass.simpleName}[${repository.name}] (${repository.source})"
        }

        override fun merge(resource: Resource<*>) =
            error("Cannot merge finished elements of resource $resource into repository as it has not been built yet!")

        override fun mergeBuilders(resource: Resource<*>) = PREPARING

    },

    /**
     * Finished [RepositoryState]: the elements of the repository have been set.
     */
    FINISHED {
        override fun finalize(resource: Resource<*>) =
            error("Cannot finalize repository with final resource '$resource' as it has already been finished!")

        override fun prepare(resource: Resource<*>) =
            error("Cannot prepare repository with '$resource' as it has already been finished!")

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

        override fun merge(resource: Resource<*>) = FINISHED

        override fun mergeBuilders(resource: Resource<*>) =
            error("Cannot merge Builders of resource $resource into repository as it has already been finished!")

    };

    abstract fun finalize(resource: Resource<*>): RepositoryState
    fun reset() = UNINITIALIZED
    abstract fun prepare(resource: Resource<*>): RepositoryState
    abstract fun update(operation: String): RepositoryState
    abstract fun reduce(operation: String): RepositoryState
    abstract fun build(): RepositoryState
    abstract fun getElements(): RepositoryState
    abstract fun getById(): RepositoryState
    abstract fun getName(): RepositoryState
    abstract fun getSource(): RepositoryState
    abstract fun toString(repository: LateInitRepository<*>): String
    abstract fun merge(resource: Resource<*>): RepositoryState
    abstract fun mergeBuilders(resource: Resource<*>): RepositoryState

}
