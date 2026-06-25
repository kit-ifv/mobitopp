package edu.kit.ifv.core.events
import edu.kit.ifv.core.statemachine.Event
import edu.kit.ifv.utils.collections.append
import edu.kit.ifv.utils.units.Time
import java.util.*

interface EventQueue {
    fun add(event: Event<*>)

    fun addAll(events: Collection<Event<*>>)

    fun hasEventsUntil(time: Time): Boolean

    fun popEventsUntil(time: Time): List<Event<*>>
}

class MapEventQueue : EventQueue {
    private val events: NavigableMap<Long, MutableList<Event<*>>> = TreeMap()

    private fun Time.toInternalNumber() = this.minutesSinceStart

    override fun add(event: Event<*>) {
        val timeSlice = events.getOrPut(event.receiveTime.toInternalNumber()) {
            mutableListOf()
        }
        timeSlice.add(event)// TODO round to nearest minute
//        events.append(event.receiveTime, event)
    }

    override fun addAll(events: Collection<Event<*>>) {

        events.forEach { add(it) }
//        require(events.all { it.isValid })

    }

    override fun hasEventsUntil(time: Time): Boolean = events.keys.any { it <= time.toInternalNumber() }
    override fun popEventsUntil(time: Time): List<Event<*>> {
        val due = events.headMap(time.toInternalNumber(), true)
        val result = due.values.flatten()
        due.clear() // remove from backing map
        return result
    }
//    override fun popEventsUntil(time: Time): List<Event<*>> = events.entries
//        .filter { it.key <= time }
//        .flatMap { events.remove(it.key)!! }
}
