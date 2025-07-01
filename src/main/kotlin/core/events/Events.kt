package core.events

import utils.units.Time

interface Agent<E> {
    val entity: E
    var nextEvent: Event<E>? // TODO protect from modification outside

    fun updateNextEvent(newEvent: Event<E>) {
        nextEvent?.invalidate()
        nextEvent = newEvent
    }

//    var location: Location
}

abstract class Event<E>(
    val time: Time,
    val priority: Int,
    val agent: Agent<E>,
) : Comparable<Event<*>> {
    val isValid: Boolean
        get() = valid

    protected var valid: Boolean = true

    fun invalidate() {
        valid = false
    }

    fun execute(): List<Event<*>> {
        return if (!valid) {
            emptyList()
        } else {
            process(agent.entity).onEach {
                updateNextEvent(it)
            }
        }
    }

    abstract fun process(entity: E): List<Event<*>>

    override fun compareTo(other: Event<*>): Int {
        return Comparator.comparing { e: Event<*> -> e.time }
            .thenComparing { e: Event<*> -> e.priority }
            .compare(this, other)
    }
}

internal fun <E> updateNextEvent(event: Event<E>) {
    val agent: Agent<E> = event.agent
    agent.updateNextEvent(event)
}
