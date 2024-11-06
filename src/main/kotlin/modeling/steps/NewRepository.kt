package modeling.steps

import kotlinx.serialization.Serializable
import kotlin.properties.Delegates

@Serializable
@JvmInline
value class NewId<out E>(val value: Long) : Comparable<NewId<*>> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: NewId<*>): Int {
        return value.compareTo(other.value)
    }
}

interface Identifiable<I> {
    val id: I
}

interface NewRepository<out T, I> where T: Identifiable<I> {

    val elements: Sequence<T>

    fun getById(id: I): T?

}

class MutableRepository<T, I> : NewRepository<T, I> where T: Identifiable<I> {

    override val elements: Sequence<T>
        get() = _elements.values.asSequence()

    private val _elements: MutableMap<I, T> = mutableMapOf()

    override fun getById(id: I): T? = _elements[id]

    fun addElements(elements: Sequence<T>) {
        _elements.putAll(elements.associateBy { it.id })
    }

    fun addElements(elements: Collection<T>) {
        _elements.putAll(elements.associateBy { it.id })
    }

}

interface Ent: Identifiable<Int> {
    val name: String
    val age: Int
    val numbers: List<Int>
}

class MutEnt: Ent {
    override lateinit var name: String
    override var age by Delegates.notNull<Int>()
    override var numbers: MutableList<Int> = mutableListOf()
    override val id: Int
        get() = age
}

val entRepo: NewRepository<Ent, Int> = MutableRepository<MutEnt, Int>()