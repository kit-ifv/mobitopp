package modeling.agents

import utils.Identifiable

typealias EventList = List<Event<out Any>>

interface Agent<M>: Identifiable<M> {
    var nextEvent: Event<M>?

    fun nextEvent(): Event<M>? = nextEvent
    fun updateNextEvent(newEvent: Event<M>) {
        nextEvent?.invalidate()
        nextEvent = newEvent
    }

    fun accept(event: Event<M>): EventList
}

interface MutableAgent<M>: Agent<M> {
    val mutableEntity: M

    override fun accept(event: Event<M>): EventList {
        return event.visit(mutableEntity).onEach {
            updateNextEvent(it)
        }
    }

}

internal fun <M> updateNextEvent(event: Event<M>)  {
    val agent: Agent<M> = event.receiver
    agent.updateNextEvent(event)
}
