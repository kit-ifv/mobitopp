package core.events

import core.statemachine.Event
import utils.collections.append
import utils.units.Time
import java.util.*

interface EventQueue {
    fun add(event: Event<*>)

    fun addAll(events: Collection<Event<*>>)

    fun hasEventsUntil(time: Time): Boolean

    fun popEventsUntil(time: Time): List<Event<*>>
}

class MapEventQueue : EventQueue {
    private val events: NavigableMap<Time, MutableList<Event<*>>> = TreeMap()

    override fun add(event: Event<*>) {
        val timeSlice = events[event.receiveTime] // TODO round to nearest minute
//        require(event.isValid) { "Attempted to add invalid Event to queue: $event" }
        require(
            timeSlice?.let {
                event !in timeSlice
            } ?: true,
        ) { "Attempted to add event to queue that was already added: $event" }

        events.append(event.receiveTime, event)
    }

    override fun addAll(events: Collection<Event<*>>) {
//        require(events.all { it.isValid })
        val targets = events.groupBy { it.receiveTime }.map {
            it.key to it.value.toMutableList()
        }
        targets.forEach { (k, v) ->
            val temp = this.events[k] ?: emptyList()
            require(
                v.all { (it !in temp) },
            )
            v.addAll(temp)
        }

        this.events.putAll(targets)
    }

    override fun hasEventsUntil(time: Time): Boolean = events.keys.any { it <= time }
    override fun popEventsUntil(time: Time): List<Event<*>> {
        val due = events.headMap(time, true)
        val result = due.values.flatten()
        due.clear() // remove from backing map
        return result
    }
//    override fun popEventsUntil(time: Time): List<Event<*>> = events.entries
//        .filter { it.key <= time }
//        .flatMap { events.remove(it.key)!! }
}
