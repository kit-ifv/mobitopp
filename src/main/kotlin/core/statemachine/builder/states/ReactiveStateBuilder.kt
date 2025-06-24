package core.statemachine.builder.states

import core.statemachine.AnyMessageType
import core.statemachine.Events
import core.statemachine.Message
import core.statemachine.MessageType
import core.statemachine.ReusableSender
import core.statemachine.Send
import core.statemachine.StateTransition
import core.statemachine.builder.FallbackTransitionBuilder
import core.statemachine.builder.MessageResponseBuilder
import core.statemachine.builder.OnEnter
import core.statemachine.builder.FallbackTransition
import core.statemachine.builder.StateBehavior
import core.statemachine.builder.StateBuilder
import core.statemachine.builder.StateData
import core.statemachine.builder.StateResolver
import core.statemachine.builder.StateType
import core.statemachine.builder.TransitionOnMessage

internal class ReactiveStateBuilder<D> (
    override val type: StateType<D>,
    private val onEnter: OnEnter<D>,
) : StateBuilder<D>, MessageResponseBuilder<D> where D : StateData {

    private val onMessageDispatch: MutableMap<AnyMessageType, OnMessageWrapper<D, out Message>> = mutableMapOf()
    private var fallbackTransition: FallbackTransition<D> = { null }

    override fun build(resolver: StateResolver): StateBehavior<D> = ReactiveStateBehavior(
        onEnterScope = onEnter,
        messageDispatcher = onMessageDispatch,
        fallbackTransition = fallbackTransition,
        stateResolver = resolver
    )

    override fun <T : Message> transitionOn(
        message: MessageType<T>,
        onMessage: TransitionOnMessage<D, T>
    ): MessageResponseBuilder<D> {
        require(message !in onMessageDispatch) {
            "Reaction to message $message already defined! "
        }

        onMessageDispatch[message] = OnMessageWrapper<D, T> { d, m, s -> d.onMessage(m, s) }
        return this
    }

    override fun checkTransition(onCheck: FallbackTransition<D>): FallbackTransitionBuilder<D> {
        val previous = fallbackTransition
        fallbackTransition = { send ->
            previous(send) ?: onCheck(send)
        }
        return this
    }

    override fun transitionIf(
        condition: D.() -> Boolean,
        nextState: D.(Send) -> StateData,
    ): FallbackTransitionBuilder<D> = checkTransition { send ->
        condition().takeIf { it }?.let { nextState(send) }
    }
}

private class ReactiveStateBehavior<D : StateData>(
    private val onEnterScope: OnEnter<D>,
    private val messageDispatcher: Map<AnyMessageType, OnMessageWrapper<D, out Message>>,
    private val fallbackTransition: FallbackTransition<D>,
    private val stateResolver: StateResolver,
) : StateBehavior<D> {
    private val sendScope = ReusableSender()

    override fun enter(data: D): Events = sendScope(data) { send ->
        data.onEnterScope(send)
    }.first

    @Suppress("UNCHECKED_CAST")
    private fun <T : Message> resolveOnMessage(message: T): OnMessageWrapper<D, T> {
        val messageType = message::class
        return requireNotNull(messageDispatcher[messageType]) {
            "No behavior defined for message ${messageType.simpleName}: $message"
        }.let {
            it as? OnMessageWrapper<D, T>
        }!!
    }

    override fun processMessage(data: D, message: Message): StateTransition = sendScope(data) { send ->
        val onMessage = resolveOnMessage(message)
        onMessage(data, message, send)?.let { stateResolver.resolve(it) }
    }

    override fun fallbackTransition(data: D): StateTransition = sendScope(data) { send ->
        fallbackTransition(data, send)?.let {
            stateResolver.resolve(it)
        }
    }

    override fun interrupt(data: D): Events {
        TODO("Not yet implemented")
    }
}

private fun interface OnMessageWrapper<D, M> {
    operator fun invoke(data: D, message: M, send: Send): StateData?
}
