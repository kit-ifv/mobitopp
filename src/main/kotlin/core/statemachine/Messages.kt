package core.statemachine

import core.statemachine.builder.StateData
import kotlin.reflect.KClass

typealias MessageType<T> = KClass<T>
typealias AnyMessageType = KClass<out Message>

interface Message {
    val time: Time // TODO find way to remove time from message interface
}

data class Event<M : Message>(
    val sender: Agent<*>,
    val sendTime: Time,
    val receiver: Agent<M>,
    val content: M
) {
    val receiveTime: Time
        get() = content.time
}

typealias Events = Collection<Event<*>>

interface Send {
    operator fun <M : Message> invoke(message: M, to: Agent<M>)
}

interface SendScope {
    operator fun <R> invoke(state: StateData, scope: (Send) -> R): Pair<Events, R>
}

class ReusableSender : SendScope {
    private val events: MutableList<Event<*>> = mutableListOf()
    private lateinit var sender: Agent<*>
    private var sendTime: Time = 0uL

    private val send = object : Send {
        override fun <M : Message> invoke(message: M, to: Agent<M>) {
            events.add(Event<M>(sender, sendTime, to, message))
        }
    }

    override operator fun <R> invoke(state: StateData, scope: (Send) -> R): Pair<Events, R> {
        events.clear()
        sender = state.agent
        sendTime = state.time
        val result = scope(send)
        return events to result
    }
}
