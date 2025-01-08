@file:JvmName("OldRepositoryKt")

package modeling.steps

import modeling.validation.validateScope
import utils.Identifiable
import utils.collections.enforceIndent
import utils.collections.replaceOrRemoveAll

// @Serializable
// @JvmInline
// value class NewId<out E>(val value: Long) : Comparable<NewId<*>> {
//    /**
//     * Compares this object with the specified object for order. Returns zero if this object is equal
//     * to the specified [other] object, a negative number if it's less than [other], or a positive number
//     * if it's greater than [other].
//     */
//    override fun compareTo(other: NewId<*>): Int {
//        return value.compareTo(other.value)
//    }
// }

// interface Identifiable<I> {
//    val id: I
// }

interface Repository<out T, I> : Resource<T> where T : Identifiable<I> {

    override val name: String
    override val source: String
    val sealed: Boolean

    override val elements: Sequence<T>
    fun getById(id: I): T?

    val size: Int
    fun isEmpty(): Boolean
}

interface MutableRepository<T, I> : Repository<T, I> where T : Identifiable<I> {
    fun addElements(operation: String, elements: Sequence<T>)
    fun addElements(operation: String, elements: Collection<T>)
    fun filterElements(operation: String, predicate: (T) -> Boolean)
    fun filterIds(operation: String, predicate: (I) -> Boolean)
    fun updateEach(operation: String, action: (T) -> Unit)
    fun transformEach(operation: String, mapping: (T) -> T?)
    fun transformAll(operation: String, mapping: (Collection<T>) -> Collection<T>)
    fun clear()
    fun seal()
}

private const val CLEAR = "clear repository"
private const val SEAL = "seal repository"

private const val MAX_ELEMENTS_IN_TO_STRING = 20

class MapRepository<T, I>(
    override val name: String
) : MutableRepository<T, I> where T : Identifiable<I> {

    override val source: String
        get() = name + "\n" + changelog

    private var changelog: String = ""

    private fun updateChangelog(operation: String) {
        changelog += "+ $operation\n"
    }

    override val elements: Sequence<T>
        get() = _elements.values.asSequence()

    private val _elements: MutableMap<I, T> = mutableMapOf()

    override fun getById(id: I): T? = _elements[id]

    override fun isEmpty(): Boolean = _elements.isEmpty()

    override val size: Int
        get() = _elements.size

    override var sealed = false

    private fun requireNotSealed(operation: String) {
        if(sealed) {
            error(
                "Repository $name has already been sealed and can no longer be updated!\n" +
                    "attempted mutating action: $operation\n" +
                    "Repository changelog:\n" +
                    source
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

    @Suppress("MagicNumber")
    override fun toString(): String = """
        |Repository '$name':
        |  source:
        |${source.enforceIndent(4)}
        |  elements ($size):
        |    ${elements.take(MAX_ELEMENTS_IN_TO_STRING).toList().let { elementsToString(it) }
    }
    """.trimMargin()

    private fun elementsToString(it: List<T>) =
        it.joinToString(", ") + if (it.size > MAX_ELEMENTS_IN_TO_STRING) {
            ", ..."
        } else {
            ""
        }
}

fun <R, E, I> R.asRepository() where R: Resource<E>, E: Identifiable<I> = MapRepository<E, I>(name).also {
    it.addElements(this.source, this.elements)
}

fun validateNotSealed(
    repository: MutableRepository<*, *>,
    step: ModelStep
) = validateScope(
    "Validate repository ${repository.name} is not sealed:"
) {
    require(!repository.sealed) {
        "Error: repository ${repository.name} was sealed before execution of step: ${step.name}\n$repository"
    }
}

//
// interface Ent: Identifiable<Int> {
//    val name: String
//    val age: Int
//    val numbers: List<Int>
// }
//
// class MutEnt: Ent {
//    override lateinit var name: String
//    override var age by Delegates.notNull<Int>()
//    override var numbers: MutableList<Int> = mutableListOf()
//    override val id: Int
//        get() = age
// }
//
// val entRepo: Repository<Ent, Int> = MutableRepository<MutEnt, Int>("test")
