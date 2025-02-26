package proofofconcept

//
// @Serializable
// @JvmInline
// internal value class NEWID<in E>(val value: Long) : Comparable<NEWID<*>> {
//    override fun compareTo(other: NEWID<*>): Int {
//        return value.compareTo(other.value)
//    }
// }
//
// internal interface Identify {
//    val id: NEWID<*>
// }
//
// internal interface Repo<out T> where T : Identify {
//    val sealed: Boolean
//
//    val elements: Sequence<T>
//    fun getById(id: NEWID<T>): T?
//
//    val size: Int
//    fun isEmpty(): Boolean
// }
//
// internal interface MutRepo<T> : Repo<T> where T : Identify {
//
//    fun addElements(operation: String, elements: Collection<T>)
// }
//
// internal class MapRepo<T> : MutRepo<T> where T : Identify {
//    override val elements: Sequence<T>
//        get() = _elements.values.asSequence()
//
//    private val _elements: MutableMap<NEWID<T>, T> = mutableMapOf()
//
//    override fun addElements(operation: String, elements: Collection<T>) {
//        _elements.putAll(elements.associateBy { it.id })
//    }
//
//    override val sealed: Boolean = false
//
//    override val size: Int = _elements.size
//
//    override fun isEmpty(): Boolean = _elements.isEmpty()
//
//    override fun getById(id: NEWID<T>): T? = _elements[id]
// }
//
// internal typealias EntityId = NEWID<Entity>
//
// internal abstract class Entity(
//    final override val id: EntityId,
// ) : Identify {
//
//    abstract val age: Int
// }
//
// internal class MutEntity(
//    id: EntityId
// ) : Entity(id) {
//    override var age: Int = 0
// }
//
// internal fun foo(): Repo<Entity> {
//    return MapRepo<MutEntity>()
// }
