package core.statemachine

import core.statemachine.builder.StateData
import utils.units.AbsoluteTime
import kotlin.reflect.KClass

typealias MessageType<T> = KClass<T>
typealias AnyMessageType = KClass<out Message>

interface Message

data class Event<M : Message>(
    val sender: Agent<*>,
    val sendTime: AbsoluteTime,
    val receiver: Agent<in M>,
    val receiveTime: AbsoluteTime,
    val content: M
) : Comparable<Event<*>> {
    fun execute(): Events = receiver.processEvent(this)

    override fun compareTo(other: Event<*>) = receiveTime.compareTo(other.receiveTime)

    override fun toString() = "[$receiveTime, ${content::class.simpleName}]"
}

typealias Events = Collection<Event<*>>

interface Send {
    operator fun <M : Message> invoke(message: M, to: Agent<in M>, at: AbsoluteTime)
    fun <M : Message> now(message: M, to: Agent<in M>)
}

interface SendScope {
    operator fun <R> invoke(scope: (Send) -> R): Pair<Events, R>
}

fun <R> sendScope(data: StateData, scope: (Send) -> R): Pair<Events, R> =
    SingleUseSendScope(data).invoke(scope)

private class SingleUseSendScope(
    private val sendTime: AbsoluteTime,
    private val sender: Agent<*>,
) : Send, SendScope {

    constructor(data: StateData) : this(data.time, data.agent)

    private val events: MutableList<Event<*>> = mutableListOf()

    override fun <R> invoke(scope: (Send) -> R): Pair<Events, R> {
        events.clear()
        val result = scope(this)
        return events to result
    }

    override fun <M : Message> invoke(message: M, to: Agent<in M>, at: AbsoluteTime) {
        events.add(Event<M>(sender, sendTime, to, at, message))
    }

    override fun <M : Message> now(message: M, to: Agent<in M>) {
        events.add(Event<M>(sender, sendTime, to, sendTime, message))
    }
}
