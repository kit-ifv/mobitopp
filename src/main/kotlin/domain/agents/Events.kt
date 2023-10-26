package domain.agents

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

typealias Time = Duration

interface Event<M>: Comparable<Event<*>> {
    val time: Time
    val priority: Int
    var valid: Boolean
    val receiver: Agent<M>
    fun invalidate() {
        valid = false
    }

    fun execute(): EventList {
        return if (!valid) {
            emptyList()
        } else {
            receiver.accept(this)
        }
    }

    fun visit(mutableReceiver: M): EventList

    override fun compareTo(other: Event<*>): Int {
        return Comparator.comparing{ e: Event<*> -> e.time }
                         .thenComparing { e: Event<*> -> e.priority }
                         .compare(this, other)
    }

}


fun <M, K, V> M.append(key: K, value: V) where M: MutableMap<K, MutableList<V>> {
    if (key !in this) {
        this[key] = mutableListOf(value)
    } else {
        this[key]!!.add(value)
    }
}

class MapEventQueue {
    private val events: MutableMap<Time, MutableList<Event<*>>> = mutableMapOf()

    fun add(event: Event<*>) {
        val timeSlice = events[event.time]
        require(event.valid) {"Attempted to add invalid Event to queue: $event"}
        require(timeSlice?.let { event !in timeSlice } ?: true)
                {"Attempted to add event to queue that was already added: $event"}

        events.append(event.time, event)
    }

    fun addAll(events: Collection<Event<*>>) = events.forEach { add(it) }

    fun hasEventsUntil(time: Time) = events.keys.any { it <= time }

    fun popEventsUntil(time: Time) = events.entries
                                           .filter { it.key <= time }
                                           .flatMap { events.remove(it.key)!! }

}

class Simulator(
    initEvents: Collection<Event<*>>,
    private val queue: MapEventQueue = MapEventQueue(),
    val timeStep: Duration = 1.minutes
) {

    init {
        queue.addAll(initEvents)
    }


    fun run(period: Duration) {
        val start = 0.minutes
        var time = start + 0.minutes

        while ((time - start) <= period) {
            val currentEvents = queue.popEventsUntil(time)

            val newEvents = processInstantEvents(currentEvents, time)
            queue.addAll(newEvents)

            time += timeStep
        }

    }

    private fun processInstantEvents(events: Collection<Event<*>>, now: Time): Collection<Event<*>> {
        val instant = events.filter { it.time <= now}
        val latent = events.filter { it.time > now}.toMutableList()

        latent.addAll(
            instant.flatMap { processInstantEvents(it.execute(), now) }
        )

        return latent
    }


}


fun main() {
    val period = 10.minutes
    val start = TimeSource.Monotonic.markNow().let { it.minus(it) }
    var time = start + 0.minutes

    while (time - start <= period) {
        println(time)
        time += 1.minutes
    }
}