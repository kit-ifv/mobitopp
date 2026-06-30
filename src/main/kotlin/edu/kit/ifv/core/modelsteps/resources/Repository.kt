@file:JvmName("OldRepositoryKt")

package edu.kit.ifv.core.modelsteps.resources
import edu.kit.ifv.core.modelsteps.Context
import edu.kit.ifv.core.modelsteps.validation.validateCondition
import edu.kit.ifv.utils.Identifiable
import edu.kit.ifv.utils.collections.enforceIndent
import edu.kit.ifv.utils.collections.replaceOrRemoveAll

/**
 * A Repository is a specialized [Resource] where elements are [Identifiable] by an ID of type [I].
 *
 * Repositories are the primary way to manage and access entities within a simulation.
 * They support lookup by ID and provide metadata like [size] and [sealed] status.
 *
 * @param T the type of entities in the repository
 * @param I the type of entity identifiers
 */
interface Repository<out T, I> : Resource<T> where T : Identifiable<I> {

    /** The name of the repository. */
    override val name: String

    /** The description of the repository source. */
    override val source: String

    /** Whether the repository is sealed (cannot be modified further). */
    val sealed: Boolean

    /** A sequence of all elements in the repository. */
    override val elements: Sequence<T>

    /**
     * Checks if the repository contains an element with the given [id].
     *
     * @param id the identifier to check
     * @return true if an element with the [id] exists, false otherwise
     */
    operator fun contains(id: I): Boolean = find(id) != null

    /**
     * Finds an element by its [id].
     *
     * @param id the identifier to look for
     * @return the element if found, or null otherwise
     */
    fun find(id: I): T? = getById(id)

    /**
     * Retrieves an element by its [id].
     *
     * @param id the identifier to look for
     * @return the element if found, or null otherwise
     */
    @Deprecated(
        "getById does not imply nullabilty by its name, use find instead. In case you compare against null " +
            "just to check whether the key is present use operator contains instead.",
    )
    fun getById(id: I): T?

    /**
     * Retrieves an element by its [id] using the index operator.
     *
     * @param id the identifier to look for
     * @return the element if found, or null otherwise
     */
    operator fun get(id: I) = getById(id)

    /**
     * Retrieves an element by its [id].
     *
     * @param id the identifier to look for
     * @return the element if found, or null otherwise
     */
    fun getValue(id: I) = getById(id)
        ?: throw NoSuchElementException(
            "Cannot find id [$id] in repository [$name], Repository contains [${elements.toList().size}] elements." +
                "${elements.map { it.id }.toList()}",
        )

    /** The number of elements in the repository. */
    val size: Int

    /**
     * @return true if the repository contains no elements, false otherwise
     */
    fun isEmpty(): Boolean
}

/**
 * A [MutableRepository] extends [Repository] with modification operations.
 *
 * It allows adding, filtering, updating, and transforming elements.
 * Once [seal] is called, no further modifications are allowed.
 *
 * @param T the type of entities in the repository
 * @param I the type of entity identifiers
 */
interface MutableRepository<T, I> : Repository<T, I> where T : Identifiable<I> {
    /**
     * Adds elements from a [Sequence] to the repository.
     *
     * @param operation a description of the operation for the changelog
     * @param elements the sequence of elements to add
     */
    fun addElements(operation: String, elements: Sequence<T>)

    /**
     * Adds elements from a [Collection] to the repository.
     *
     * @param operation a description of the operation for the changelog
     * @param elements the collection of elements to add
     */
    fun addElements(operation: String, elements: Collection<T>)

    /**
     * Filters elements in the repository using the provided [predicate].
     *
     * @param operation a description of the operation for the changelog
     * @param predicate the condition to keep elements
     */
    fun filterElements(operation: String, predicate: (T) -> Boolean)

    /**
     * Filters elements in the repository based on their IDs using the provided [predicate].
     *
     * @param operation a description of the operation for the changelog
     * @param predicate the condition to keep identifiers
     */
    fun filterIds(operation: String, predicate: (I) -> Boolean)

    /**
     * Applies an [action] to each element in the repository.
     *
     * @param operation a description of the operation for the changelog
     * @param action the function to apply to each element
     */
    fun updateEach(operation: String, action: (T) -> Unit)

    /**
     * Applies an [action] to all elements in the repository at once.
     *
     * @param operation a description of the operation for the changelog
     * @param action the function to apply to the collection of elements
     */
    fun updateAll(operation: String, action: (Collection<T>) -> Unit)

    /**
     * Transforms each element in the repository using the [mapping] function.
     * If the mapping returns null, the element is removed.
     *
     * @param operation a description of the operation for the changelog
     * @param mapping the transformation function
     */
    fun transformEach(operation: String, mapping: (T) -> T?)

    /**
     * Transforms the entire set of elements using the [mapping] function.
     *
     * @param operation a description of the operation for the changelog
     * @param mapping the transformation function for the collection
     */
    fun transformAll(operation: String, mapping: (Collection<T>) -> Collection<T>)

    /**
     * Removes all elements from the repository.
     */
    fun clear()

    /**
     * Seals the repository, preventing any further modifications.
     */
    fun seal()
}

private const val CLEAR = "clear repository"
private const val SEAL = "seal repository"

private const val MAX_ELEMENTS_IN_TO_STRING = 20

/**
 * A [MutableRepository] implementation backed by a [MutableMap].
 *
 * @param T the type of entities in the repository
 * @param I the type of entity identifiers
 * @property name the name of the repository
 */
class MapRepository<T, I>(override val name: String) : MutableRepository<T, I> where T : Identifiable<I> {

    /** The source description, including the operation changelog. */
    override val source: String
        get() = name + "\n" + changelog

    private var changelog: String = ""

    private fun updateChangelog(operation: String) {
        changelog += "+ $operation\n"
    }

    /** A sequence of all elements stored in the map. */
    override val elements: Sequence<T>
        get() = _elements.values.asSequence()

    private val _elements: MutableMap<I, T> = mutableMapOf()

    /**
     * @param id the identifier to look for
     * @return the element if found, null otherwise
     */
    override fun getById(id: I): T? = _elements[id]

    /**
     * @return true if the map is empty, false otherwise
     */
    override fun isEmpty(): Boolean = _elements.isEmpty()

    /** The number of entries in the map. */
    override val size: Int
        get() = _elements.size

    /** Whether the repository is sealed. */
    override var sealed = false

    private fun requireNotSealed(operation: String) {
        if (sealed) {
            error(
                "Repository $name has already been sealed and can no longer be updated!\n" +
                    "attempted mutating action: $operation\n" +
                    "Repository changelog:\n" +
                    source,
            )
        }
    }

    override fun addElements(operation: String, elements: Sequence<T>) = addElements(operation, elements.toList())

    override fun addElements(operation: String, elements: Collection<T>) {
        requireNotSealed(operation)
        updateChangelog("add elements: $operation")
        _elements.putAll(elements.associateBy { it.id })
    }

    override fun filterElements(operation: String, predicate: (T) -> Boolean) {
        requireNotSealed(operation)
        updateChangelog("filter elements: $operation")
        _elements.entries.removeIf { !predicate(it.value) }
    }

    override fun filterIds(operation: String, predicate: (I) -> Boolean) {
        requireNotSealed(operation)
        updateChangelog("filter ids: $operation")
        _elements.keys.removeIf { !predicate(it) }
    }

    override fun updateEach(operation: String, action: (T) -> Unit) {
        requireNotSealed(operation)
        updateChangelog("update each element: $operation")
        _elements.values.forEach(action)
    }

    override fun updateAll(operation: String, action: (Collection<T>) -> Unit) {
        requireNotSealed(operation)
        updateChangelog("update all elements: $operation")
        action(_elements.values)
    }

    override fun transformEach(operation: String, mapping: (T) -> T?) {
        requireNotSealed(operation)
        updateChangelog("replace each element: $operation")
        _elements.replaceOrRemoveAll { _, value -> mapping(value) }
    }

    override fun transformAll(operation: String, mapping: (Collection<T>) -> Collection<T>) {
        requireNotSealed(operation)
        updateChangelog("replace all elements: $operation")
        val newElements = mapping(_elements.values).associateBy { it.id }
        _elements.clear()
        _elements.putAll(newElements)
    }

    override fun clear() {
        requireNotSealed(CLEAR)
        updateChangelog(CLEAR)
        _elements.clear()
    }

    override fun seal() {
        requireNotSealed(SEAL)
        updateChangelog(SEAL)
        sealed = true
    }

    /**
     * @return a formatted string representation of the repository and its first few elements.
     */
    @Suppress("MagicNumber")
    override fun toString(): String = """
        |Repository '$name':
        |  source:
        |${source.enforceIndent(4)}
        |  elements ($size):
        |    ${
        elements.take(MAX_ELEMENTS_IN_TO_STRING).toList().let { elementsToString(it) }
    }
    """.trimMargin()

    private fun elementsToString(it: List<T>) = it.joinToString(", ") + if (it.size > MAX_ELEMENTS_IN_TO_STRING) {
        ", ..."
    } else {
        ""
    }
}

/**
 * Converts a [Resource] to a [MapRepository].
 *
 * @receiver The resource to be converted.
 * @param R the resource type
 * @param E the entity type
 * @param I the identifier type
 * @return a [MapRepository] containing all elements from the resource
 */
fun <R, E, I> R.asRepository() where R : Resource<E>, E : Identifiable<I> = MapRepository<E, I>(name).also {
    it.addElements(this.source, this.elements)
}

/**
 * Validates that a [repository] is not sealed.
 *
 * @receiver The context used for reporting.
 * @param C the context type
 * @param repository the repository to check
 * @param step the name of the step performing the check
 * @return true if the repository is not sealed, false otherwise
 */
fun <C : Context> C.validateNotSealed(repository: MutableRepository<*, *>, step: String) = validateCondition(
    { "repository ${repository.name} was sealed before execution of step: ${step}\n$repository" },
    isError = true,
) {
    !repository.sealed
}
