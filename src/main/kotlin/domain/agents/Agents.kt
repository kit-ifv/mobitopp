package domain.agents

import ID
import Identifiable

typealias EventList = List<Event<out Identifiable, out Any, out Any, out Any>>

interface Agent<P, B, S, M>: Identifiable where M: S, P: Identifiable {
    val properties: P
    val behavior: B
    val state: S
    var nextEvent: Event<P, B, S, M>?

    fun nextEvent(): Event<P, B, S, M>? = nextEvent
    fun setNextEvent(newEvent: Event<P, B, S, M>) {
        nextEvent?.invalidate()
        nextEvent = newEvent
    }

    override val id: ID
        get() = properties.id

    fun accept(event: Event<P, B, S, M>): EventList
}

interface MutableAgent<P, B, S, M>: Agent<P, B, S, M> where M: S, P: Identifiable {
    val mutableState: M

    override val state: S
        get() = mutableState

    override fun accept(event: Event<P, B, S, M>): EventList {
        return event.visit(this).onEach {
            updateNextEvent(it)
        }
    }

}

internal fun <P, B, S, M> updateNextEvent(event: Event<P, B, S, M>) where P: Identifiable, M: S {
    val agent: Agent<P, B, S, M> = event.receiver
    agent.setNextEvent(event)
}

interface Event<P, B, S, M> where M: S, P: Identifiable {
    val valid: Boolean
    val receiver: Agent<P, B, S, M>
    fun invalidate()

    fun execute(): EventList {
        return if (!valid) {
            emptyList()
        } else {
            receiver.accept(this)
        }
    }

    fun visit(mutableReceiver: MutableAgent<P, B, S, M>): EventList

}

