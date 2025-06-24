package states_cleaned


interface StateBuilder<D> where D: StateData {
    val type: StateType<D>
    fun build(resolver: StateResolver): StateBehavior<D>
}

fun interface OnMessageWrapper<D, M> {
    operator fun invoke(data: D, message: M, send: Send)
}

fun interface MessageTransitionWrapper<D, M> {
    operator fun invoke(data: D, message: M): StateData
}


class TransitoryStateBuilderImpl<D>(
    override val type: StateType<D>,
    private val onEnter: OnEnter<D>
): StateBuilder<D>, TransitoryStateBuilder<D> where D: StateData {

    private lateinit var nextTransition: TransitionOnNext<D>

    override fun next(onTransition: TransitionOnNext<D>) {
        nextTransition = onTransition
    }

    override fun build(resolver: StateResolver) = TransitoryStateBehavior(
        onEnterScope = onEnter,
        nextStateTransition = nextTransition,
        stateResolver = resolver,
    )

}


class StateBuilderImpl<D> (
    override val type: StateType<D>,
    private val onEnter: OnEnter<D>,
) : StateBuilder<D>, MessageResponseBuilder<D> where D: StateData {


    private val onMessageDispatch: MutableMap<MessageType<out Message>, OnMessageWrapper<D, out Message>> = mutableMapOf()
    private val messageTransitionDispatch: MutableMap<MessageType<out Message>, MessageTransitionWrapper<D, out Message>> = mutableMapOf()
    private var conditionalTransition: TransitionOnCheck<D> = { null }

    override fun build(resolver: StateResolver) = StateBehaviorImpl(
        onEnterScope = onEnter,
        messageDispatcher = onMessageDispatch,
        messageTransitionDispatch = messageTransitionDispatch,
        conditionalTransition = conditionalTransition,
        stateResolver = resolver
    )

    override fun <T : Message> on(message: MessageType<T>,  onMessage: OnMessage<D, T>): MessageResponseBuilder<D> {
        require(message !in onMessageDispatch) {
            "Reaction to message $message already defined! "
        }

        onMessageDispatch[message] = OnMessageWrapper<D, T> { d, m, s -> d.onMessage(m, s) }
        return this
    }

    override fun <T: Message> transitionOn(message: MessageType<T>, onExit: TransitionOnMessage<D, T>): StateExitBuilder<D> {
        require(message !in messageTransitionDispatch) {
            "Transition on message $message already defined!"
        }

        messageTransitionDispatch[message] = MessageTransitionWrapper<D, T> { d, m -> d.onExit(m) }
        return this
    }

    override fun checkTransition(onCheck: TransitionOnCheck<D>): StateExitBuilder<D> {
        val previous = conditionalTransition
        conditionalTransition = {
            previous() ?: onCheck()
        }
        return this
    }

    override fun transitionIf(
        condition: D.() -> Boolean,
        nextState: D.() -> StateData,
    ): StateExitBuilder<D> = checkTransition {
        condition().takeIf { it }?.let { nextState() }
    }

}

//interface ReusableState<D>: State where D: StateData {
//    var stateData: D
//}

interface StateBehavior<D: StateData> {
    fun enter(data: D): Events
    fun processMessage(data: D, message: Message): Events
    fun checkMessageTransition(data: D, message: Message): State?
    fun checkConditionTransition(data: D): State?
    fun interrupt(data: D): Events
}

class TransitoryStateBehavior<D>(
    private val onEnterScope: OnEnter<D>,
    private val nextStateTransition: TransitionOnNext<D>,
    private val stateResolver: StateResolver,
) : StateBehavior<D> where D: StateData {
    private val sendScope = ReusableSender()

    override fun enter(data: D): Events = sendScope(data) { send ->
        data.onEnterScope(send)
    }

    override fun processMessage(data: D, message: Message): Events {
        throw UnsupportedOperationException("processMessage should not be called on TransitoryState")
    }

    override fun checkMessageTransition(data: D, message: Message): State? {
        throw UnsupportedOperationException("checkMessageTransition should not be called on TransitoryState")
    }

    override fun checkConditionTransition(data: D): State? {
        val nextState = data.nextStateTransition()
        return stateResolver.resolve(nextState)
    }

    override fun interrupt(data: D): Events {
        TODO("Not yet implemented")
    }

}


class StateBehaviorImpl<D: StateData>(
    private val onEnterScope: OnEnter<D>,
    private val messageDispatcher: Map<MessageType<out Message>, OnMessageWrapper<D, out Message>>,
    private val messageTransitionDispatch: Map<MessageType<out Message>, MessageTransitionWrapper<D, out Message>>,
    private val conditionalTransition: TransitionOnCheck<D>,
    private val stateResolver: StateResolver,
): StateBehavior<D> {
    private val sendScope = ReusableSender()

    override fun enter(data: D) : Events = sendScope(data) { send ->
        data.onEnterScope(send)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: Message> resolveOnMessage(message: T): OnMessageWrapper<D, T> {
        val messageType = message.type
        return requireNotNull(messageDispatcher[messageType]) {
            "No behavior defined for message: $messageType, $message"
        }.let {
            it as? OnMessageWrapper<D, T>
        }!!
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: Message> resolveMessageTransition(message: T): MessageTransitionWrapper<D, T> {
        val messageType = message.type
        return requireNotNull(messageTransitionDispatch[messageType]) {
            "No transition defined for message: $messageType, $message"
        }.let {
            it as? MessageTransitionWrapper<D, T>
        }!!
    }

    override fun processMessage(data: D, message: Message): Events = sendScope(data) { send ->
        data.advance(message.time)
        val onMessage = resolveOnMessage(message)
        onMessage(data, message, send)
    }

    override fun checkMessageTransition(data: D, message: Message): State? =
        takeIf {
            message.type in messageTransitionDispatch
        }?.let {
            data.advance(message.time)
            val transition = resolveMessageTransition(message)
            val nextState =transition(data, message)
            stateResolver.resolve(nextState)
        }


    override fun checkConditionTransition(data: D): State? =
        conditionalTransition(data)?.let {
            stateResolver.resolve(it)
        }

    override fun interrupt(data: D): Events {
        TODO("Not yet implemented")
    }
}

class FinalStateBehavior<D>(
    private val onEnterScope: OnEnter<D>,
): StateBehavior<D> where D: StateData {

    private val sendScope = ReusableSender()

    override fun enter(data: D): Events = sendScope(data) { send ->
        data.onEnterScope(send)
    }

    override fun processMessage(data: D, message: Message): Events {
        throw UnsupportedOperationException("processMessage should not be called on FinalStates")
    }

    override fun checkMessageTransition(data: D, message: Message): State? {
        throw UnsupportedOperationException("checkMessageTransition should not be called on FinalState")
    }

    override fun checkConditionTransition(data: D): State? {
        throw UnsupportedOperationException("checkConditionTransition should not be called on FinalState")
    }

    override fun interrupt(data: D): Events {
        TODO("Not yet implemented")
    }

}
