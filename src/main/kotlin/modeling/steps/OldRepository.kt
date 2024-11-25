package modeling.steps
//
// import utils.Builder
// import utils.Identifiable
//
// /**
// * A Repository is a [Resource] with [Identifiable] elements
// * providing random access to elements by id.
// *
// * @param E the generic type of [Identifiable] elements
// * @param I the generic id type
// */
// interface Repository<out E, I> : Resource<E> where E : Identifiable<I> {
//
//    /** The repository size. */
//    val size: Int
//        get() = elements.count()
//
//    /**
//     * Get an element from the repository with the given id
//     * or null in case the element is absent.
//     *
//     * @param id the query id
//     * @return the element with the given id or null
//     */
//    fun getById(id: I): E?
//
//    fun clear()
// }
//
// /**
// * A MapRepository is a [Map]-based [Repository] implementation.
// *
// * @param E the generic type of contained [Identifiable] elements
// * @param elements the elements contained in the [Repository]
// * @property name the name of the repository
// * @property source the source description of the repository
// */
// class MapRepository<out E, I>(
//    elements: Collection<E>,
//    override val name: String,
//    override val source: String,
// ) : Repository<E, I> where E : Identifiable<I> {
//    private val idMap: MutableMap<I, E> = elements.associateBy { it.id }.toMutableMap()
//
//    constructor(resource: Resource<E>) : this(
//        elements = resource.elements.toList(),
//        name = resource.name,
//        source = resource.source
//    )
//
//    override val size: Int
//        get() = idMap.size
//    override val elements: Sequence<E>
//        get() = idMap.values.asSequence()
//
//    override fun getById(id: I): E? = idMap[id]
//
//    override fun toString() = "MapRepository[$name] ($source)"
//
//    override fun clear() {
//        idMap.clear()
//    }
// }
//
// /**
// * RepositoryBuilder is a [Repository] that can manage
// * [Builder]s before building them into the repository's elements.
// *
// * @param B the generic type of the [Builder]s
// * @param E the generic type of the [Identifiable] elements
// * @param I the generic type of ids used by the built elements
// * @constructor Create an [RepositoryState.UNINITIALIZED] [RepositoryBuilder]
// *
// *
// * Resources are named containers for generic element.
// * They provide these elements as a sequence and hold metadata: a name and the source of these elements.
// * Resources can provide lazy access to elements.
// *
// * [Repository]s are [Resource]s where the elements are [Identifiable].
// * Repositories provide access to single elements through their ID. (The default implementation is a [MapRepository]).
// *
// * A [RepositoryBuilder] allows late initialization of the contained elements through element [Builder]s.
// * A [RepositoryBuilder] has one of three states: uninitialized (no elements / builders in the repository),
// * preparing (builders can be added, filtered, modified), finished (builders were built to final elements).
// *
// */
// open class RepositoryBuilder<B, out E, I>() : Repository<E, I> where B : Builder<E>, E : Identifiable<I> {
//    private var builders: Resource<B>? = null
//    private var elems: Repository<E, I>? = null
//    private var internalState: RepositoryState = RepositoryState.UNINITIALIZED
//
//    /**
//     * Create a [RepositoryBuilder] initialized with the given builders.
//     *
//     * @param builders a resource of initial builders
//     * @constructor Create a [RepositoryState.PREPARING] [RepositoryBuilder]
//     */
//    constructor(builders: Resource<B>) : this() {
//        this.addBuilders(builders)
//    }
//
//    override val name: String
//        get() {
//            // internalState = internalState.performGetName()
//            return (elems ?: builders)?.name ?: "Empty Repository"
//        }
//
//    override val source: String
//        get() {
//            internalState = internalState.performGetSource()
//            return (elems ?: builders)!!.source
//        }
//
//    /**
//     * The current [RepositoryState] of the RepositoryBuilder.
//     * It can be: uninitialized, preparing or finished.
//     */
//    val state: RepositoryState
//        get() = internalState
//
//    override val elements: Sequence<E>
//        get() {
//            internalState = internalState.performGetElements()
//            return elems!!.elements
//        }
//
//    override fun getById(id: I): E? {
//        internalState = internalState.performGetById()
//        return elems!!.getById(id)
//    }
//
//    /**
//     * Filter the set of [Builder]s using the given filter condition.
//     * The filter step is logged in the metadata of the [Repository].
//     *
//     * @param operation name of the filter operation
//     * @param predicate the filter condition
//     */
//    fun filter(operation: String, predicate: (B) -> Boolean) {
//        internalState.performFilter(operation)
//        builders = builders!!.let {
//            it.elements.filter(predicate).asResource(it.name, "${it.source} -> filter $operation")
//        }
//        println("Finished Filtering")
//    }
//
//    /**
//     * Update the set of [Builder]s by applying the given mapping to each of them.
//     * The update step is logged in the metadata of the [Repository].
//     *
//     * @param operation the name of the mapping operation (used for tracking)
//     * @param mapping the mapping operation to be applied
//     */
//    fun update(operation: String, mapping: (B) -> B?) {
//        internalState.performUpdate(operation)
//        builders = builders!!.let {
//            it.elements.map(mapping).filterNotNull().asResource(it.name, "${it.source} -> map $operation")
//        }
//    }
//
//    /**
//     * Update the set of [Builder]s by mapping the entire set in one chunk.
//     * The update all step is logged in the metadata of the [Repository].
//     *
//     * @param operation the name of the mapping operation (used for tracking)
//     * @param mapping the mapping operation to be applied
//     */
//    fun updateAll(operation: String, mapping: (Sequence<B>) -> Sequence<B>) {
//        internalState.performUpdate(operation)
//        builders = builders!!.let {
//            mapping(it.elements).asResource(it.name, "${it.source} -> map all $operation")
//        }
//    }
//
//    /**
//     * Add the [Builder]s from the given [Resource] to the [RepositoryBuilder].
//     * If no builders were added yet, the given resource is used as initial set of [Builder]s.
//     * The add builders step is logged in the metadata of the [Repository].
//     *
//     * @param other a resource of builders to be added to the [RepositoryBuilder]
//     */
//    fun addBuilders(other: Resource<B>) {
//        internalState = internalState.performAddBuilders(other)
//        builders = builders?.let {
//            sequenceOf(it.elements, other.elements).flatten().asResource(
//                name = "${it.name}, ${other.name}",
//                source = "${it.source} -> merge with ${other.source}"
//            )
//        } ?: other
//    }
//
//    /** Finish the Repository by building all [Builder]s to create the final entities.  */
//    fun build() {
//        internalState = internalState.performBuild()
//        elems = MapRepository(builders!!.build())
//        builders = null
//    }
//
//    override fun clear() {
//        internalState = RepositoryState.UNINITIALIZED
//        elems?.clear()
//        elems = null
//        builders = null
//    }
//
//    /** Resets the repository removing all builders or entities */
//    fun reset() {
//        internalState = RepositoryState.UNINITIALIZED
//        builders = null
//        elems = null
//    }
//
//    override fun toString() = state.performToString(this)
// }
//
// /**
// * Repository state represents the state of a [RepositoryBuilder].
// * It can be:
// * - uninitialized: no [Builder]s were added yet; update/reduce/build and access to [Resource] metadata is not supported
// * - preparing: some [Builder]s were added; access to final elements is not supported
// * - finished: [Builder]s were transformed to elements: adding [Builder]s is not supported
// *
// * This holds the state transitions of a state machine.
// * Each operation returns the repository state after performing an operation.
// * If an operation is invalid in some state, an error message is thrown.
// */
// @Suppress("TooManyFunctions")
// enum class RepositoryState {
//    /**
//     * Uninitialized [RepositoryState]:
//     * neither the elements not the element-builders of the repository have not been set.
//     */
//    UNINITIALIZED {
//
//        override fun performUpdate(operation: String) =
//            error("Cannot apply mapping '$operation' to repository elements as it has not been prepared yet!")
//
//        override fun performFilter(operation: String) =
//            error("Cannot apply filter '$operation' to repository elements as it has not been prepared yet!")
//
//        override fun performBuild() =
//            error("Cannot build repository elements as it has not been prepared yet!")
//
//        override fun performGetElements() =
//            error("Cannot get elements of repository as it has not been finished yet!")
//
//        override fun performGetById() =
//            error("Cannot get element by id in repository as it has not been finished yet!")
//
//        override fun performGetName() =
//            error("Cannot get name of repository as it has not been finished/prepared yet!")
//
//        override fun performGetSource() =
//            error("Cannot get source of repository as it has not been finished/prepared yet!")
//
//        override fun performToString(repository: RepositoryBuilder<*, *, *>): String {
//            check(repository.state == UNINITIALIZED)
//            return "Uninitialized ${repository.javaClass.simpleName}"
//        }
//
//        override fun performAddBuilders(resource: Resource<*>) = PREPARING
//    },
//
//    /**
//     * Initialized [RepositoryState]:
//     * the element-builders of the repository have been added but the elements have not been built yet.
//     */
//    PREPARING {
//
//        override fun performUpdate(operation: String) = PREPARING
//
//        override fun performFilter(operation: String) = PREPARING
//
//        override fun performBuild() = FINISHED
//
//        override fun performGetElements() =
//            error("Cannot get elements of repository as it has not been built yet!")
//
//        override fun performGetById() =
//            error("Cannot get element by id in repository as it has not been built yet!")
//
//        override fun performGetName() = PREPARING
//
//        override fun performGetSource() = PREPARING
//
//        override fun performToString(repository: RepositoryBuilder<*, *, *>): String {
//            check(repository.state == PREPARING)
//            return "Initialized ${repository.javaClass.simpleName}[${repository.name}] (${repository.source})"
//        }
//
//        override fun performAddBuilders(resource: Resource<*>) = PREPARING
//    },
//
//    /**
//     * Finished [RepositoryState]: the elements of the repository have been built/finalized.
//     */
//    FINISHED {
//
//        override fun performUpdate(operation: String) =
//            error("Cannot apply finished '$operation' to repository elements as it has already been finished!")
//
//        override fun performFilter(operation: String) =
//            error("Cannot apply finished '$operation' to repository elements  it has already been finished!")
//
//        override fun performBuild() =
//            error("Cannot build repository as it has already been finished!")
//
//        override fun performGetElements() = FINISHED
//
//        override fun performGetById() = FINISHED
//
//        override fun performGetName() = FINISHED
//
//        override fun performGetSource() = FINISHED
//
//        override fun performToString(repository: RepositoryBuilder<*, *, *>): String {
//            check(repository.state == FINISHED)
//            return "Finished ${repository.javaClass.simpleName}[${repository.name}] (${repository.source})"
//        }
//
//        override fun performAddBuilders(resource: Resource<*>) =
//            error("Cannot merge Builders of resource $resource into repository as it has already been finished!")
//    };
//
//    abstract fun performUpdate(operation: String): RepositoryState
//    abstract fun performFilter(operation: String): RepositoryState
//    abstract fun performAddBuilders(resource: Resource<*>): RepositoryState
//    abstract fun performBuild(): RepositoryState
//    abstract fun performGetElements(): RepositoryState
//    abstract fun performGetById(): RepositoryState
//    abstract fun performGetName(): RepositoryState
//    abstract fun performGetSource(): RepositoryState
//    abstract fun performToString(repository: RepositoryBuilder<*, *, *>): String
// }
//
// /**
// * Build all [Builder]s and create a [Repository].
// * The build step is logged in the metadata of the [Repository].
// *
// * @param R generic type of the resource
// * @param B generic type of the builder
// * @param E generic type of the elements
// * @return a repository containing built elements and updated metadata of the resource
// */
// fun <R, B, E> R.build(): Resource<E> where B : Builder<E>, R : Resource<B>, E : Identifiable<*> {
//    val target = this.elements.map { it.build() }.asResource(this.name, "$source -> build")
//    return target
// }
//
// /**
// * Create a [Repository] containing the elements and metadata of the given [Resource].
// *
// * @param R generic type of the resource
// * @param E generic type of the elements
// * @return a repository containing elements and metadata of the resource
// */
// fun <R, E, I> R.asRepository(): Repository<E, I> where R : Resource<E>, E : Identifiable<I> =
//    MapRepository(this.elements.toList(), this.name, this.source)
