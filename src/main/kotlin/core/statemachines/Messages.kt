package states_cleaned

@JvmInline
value class MessageType<M> private constructor(private val id: ULong) {
    constructor(): this(idCounter++)
    companion object {
        private var idCounter = 0UL
    }
}

interface Message {
    val type: MessageType<out Message>
    val time: Time
//    val sender: Agent<*>
//    val receiver: Agent<*>

}

data class Event<M: Message>(
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
    operator fun <M: Message> invoke(message: M, to: Agent<M>)
}

interface SendScope {
    operator fun invoke(state: StateData, scope: (Send) -> Unit): Events
}

class ReusableSender: SendScope {
    private val events: MutableList<Event<*>> = mutableListOf()
    private lateinit var sender: Agent<*>
    private var sendTime: Time = 0uL

    private val send = object:Send {
        override fun <M : Message> invoke(message: M, to: Agent<M>) {
            events.add(Event<M>(sender, sendTime, to, message))
        }
    }

    override operator fun invoke(state: StateData, scope: (Send) -> Unit): Events {
        events.clear()
        sender = state.agent
        sendTime = state.time
        scope(send)
        return events
    }

}