package core.events

import utils.collections.append
import utils.units.Time

interface EventQueue {
    fun add(event: Event<*>)

    fun addAll(events: Collection<Event<*>>)

    fun hasEventsUntil(time: Time): Boolean

    fun popEventsUntil(time: Time): List<Event<*>>
}

class MapEventQueue : EventQueue {
    private val events: MutableMap<Time, MutableList<Event<*>>> = mutableMapOf()

    override fun add(event: Event<*>) {
        val timeSlice = events[event.time]
        require(event.isValid) { "Attempted to add invalid Event to queue: $event" }
        require(
            timeSlice?.let {
                event !in timeSlice
            } ?: true
        ) { "Attempted to add event to queue that was already added: $event" }

        events.append(event.time, event)
    }

    override fun addAll(events: Collection<Event<*>>) {
        require(events.all { it.isValid })
        val targets = events.groupBy { it.time }.map {
            it.key to it.value.toMutableList()
        }
        targets.forEach { (k, v) ->
            val temp = this.events[k] ?: emptyList()
            require(
                v.all { (it !in temp) }
            )
            v.addAll(temp)
        }

        this.events.putAll(targets)
    }

    override fun hasEventsUntil(time: Time): Boolean = events.keys.any { it <= time }

    override fun popEventsUntil(time: Time): List<Event<*>> = events.entries
        .filter { it.key <= time }
        .flatMap { events.remove(it.key)!! }
}
