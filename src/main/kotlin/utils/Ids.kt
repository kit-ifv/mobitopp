package utils

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ID<out E>(val id: Long) : Comparable<ID<*>> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: ID<*>): Int {
        return id.compareTo(other.id)
    }

    /**
     * Robin: I added a method to iterate over ids, I want to use this feature for generating autoincrementing ids
     * in the test cases
     *
     * @return the next higher id.
     */
    fun next(): ID<E> {
        return ID(id + 1)
    }
}

interface Identifiable<I> {
    val id: I
}

//
// internal class IdCounter {
//    private var counter: Long = 0
//
//    val next: Long
//        get() = counter++
// }
//
// internal class IdTracker {
//    private val ids: MutableSet<Long> = HashSet()
//
//    fun register(id: Long, tag: String): Long {
//        require(id !in ids) { "The requested id $id for $tag is already used!" }
//        ids.add(id)
//        return id
//    }
// }
//
// internal object GlobalIdCount {
//    private val idCounters: MutableMap<KClass<*>, IdCounter> = mutableMapOf()
//    private val idTrackers: MutableMap<KClass<*>, IdTracker> = mutableMapOf()
//
//    private fun getCounter(clazz: KClass<*>): IdCounter {
//        require(clazz !in idTrackers) { "Ids for ${clazz.simpleName} should be created manually as other ids before." }
//        return idCounters[clazz] ?: IdCounter().also { idCounters[clazz] = it }
//    }
//
//    private fun getTracker(clazz: KClass<*>): IdTracker {
//        require(clazz !in idCounters) { "Ids for ${clazz.simpleName} should be drawn as other ids before." }
//        return idTrackers[clazz] ?: IdTracker().also { idTrackers[clazz] = it }
//    }
//
//    fun drawId(clazz: KClass<*>): Long = getCounter(clazz).next
//
//    fun requestId(clazz: KClass<*>, requestedId: Long): Long =
//        getTracker(clazz).register(requestedId, clazz.simpleName ?: "UndefinedClass")
// }
//
// fun drawId(clazz: KClass<*>): Long = GlobalIdCount.drawId(clazz)
// inline fun <reified E> E.drawId(): ID<E> {
//    return ID<E>(drawId(E::class))
// }
//
// fun registerId(clazz: KClass<*>, requestedId: Long): Long = GlobalIdCount.requestId(clazz, requestedId)
// inline fun <reified E> E.registerId(requestedId: Long): ID<E> {
//    return ID<E>(registerId(E::class, requestedId))
// }
