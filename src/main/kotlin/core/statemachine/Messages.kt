package core.statemachine

import core.statemachine.builder.StateData
import kotlin.reflect.KClass

typealias MessageType<T> = KClass<T>
typealias AnyMessageType = KClass<out Message>

interface Message

data class Event<M : Message>(
    val sender: Agent<*>,
    val sendTime: Time,
    val receiver: Agent<out M>,
    val receiveTime: Time,
    val content: M
)

typealias Events = Collection<Event<*>>

interface Send {
    operator fun <M : Message> invoke(message: M, to: Agent<out M>, at: Time)
    fun <M : Message> now(message: M, to: Agent<out M>)
    // TODO fun self(message: T, at: Time)
}

interface SendScope {
    operator fun <R> invoke(state: StateData, scope: (Send) -> R): Pair<Events, R>
}

class ReusableSender : SendScope {
    private val events: MutableList<Event<*>> = mutableListOf()
    private lateinit var sender: Agent<*>
    private var sendTime: Time = 0uL

    private val send = object : Send {
        override fun <M : Message> invoke(message: M, to: Agent<out M>, at: Time) {
            events.add(Event<M>(sender, sendTime, to, at, message))
        }

        override fun <M : Message> now(message: M, to: Agent<out M>) = invoke(message, to, sendTime)
        // TODO override fun self(message: T, at: Time) = invoke(message, sender, at),
        //  maybe as extension method with receiver context?
    }

    override operator fun <R> invoke(state: StateData, scope: (Send) -> R): Pair<Events, R> {
        events.clear()
        sender = state.agent
        sendTime = state.time
        val result = scope(send)
        return events to result
    }
}
