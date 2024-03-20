package utils

import ID
import kotlin.reflect.KClass

internal class IdCounter {
    private var counter: Long = 0

    val next: Long
        get() = counter++

}

internal class IdTracker {
    private val ids: MutableSet<Long> = HashSet()

    fun register(id: Long, tag: String): Long {
        require(id !in ids) {"The requested id $id for $tag is already used!"}
        ids.add(id)
        return id
    }
}


internal object GlobalIdCount {
    private val idCounters: MutableMap<KClass<*>, IdCounter> = mutableMapOf()
    private val idTrackers: MutableMap<KClass<*>, IdTracker> = mutableMapOf()

    private fun getCounter(clazz: KClass<*>): IdCounter {
        require(clazz !in idTrackers)
            {"Ids for ${clazz.simpleName} should be created manually as other ids before."}
        return idCounters[clazz] ?: IdCounter().also { idCounters[clazz] = it }
    }

    private fun getTracker(clazz: KClass<*>): IdTracker {
        require(clazz !in idCounters)
            {"Ids for ${clazz.simpleName} should be drawn as other ids before."}
        return idTrackers[clazz] ?: IdTracker().also { idTrackers[clazz] = it }
    }

    fun drawId(clazz: KClass<*>): Long = getCounter(clazz).next

    fun requestId(clazz: KClass<*>, requestedId: Long): Long =
        getTracker(clazz).register(requestedId, clazz.simpleName!!)

}

fun drawId(clazz: KClass<*>): Long = GlobalIdCount.drawId(clazz)
inline fun <reified E> E.drawId(): ID<E> {
    return ID<E>(drawId(E::class))
}

fun registerId(clazz: KClass<*>, requestedId: Long): Long = GlobalIdCount.requestId(clazz, requestedId)
inline fun <reified E> E.registerId(requestedId: Long): ID<E> {
    return ID<E>(registerId(E::class, requestedId))
}

