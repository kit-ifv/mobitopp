package core.statemachine.builder.states

import core.statemachine.AnyMessageType
import core.statemachine.Events
import core.statemachine.Message
import core.statemachine.MessageType
import core.statemachine.Send
import core.statemachine.StateTransition
import core.statemachine.builder.AnyStateType
import core.statemachine.builder.FallbackTransition
import core.statemachine.builder.FallbackTransitionBuilder
import core.statemachine.builder.MessageResponseBuilder
import core.statemachine.builder.OnEnter
import core.statemachine.builder.StateBehavior
import core.statemachine.builder.StateBuilder
import core.statemachine.builder.StateData
import core.statemachine.builder.StateResolver
import core.statemachine.builder.TransitionOnMessage
import core.statemachine.sendScope

/**
 * Implementation of [StateBuilder] and [MessageResponseBuilder] for reactive states.
 * Reactive states respond to messages but do not automatically transition to other states.
 *
 * @param D The type of state data this builder works with
 * @property type The type of state this builder creates
 * @property onEnter Function to execute when entering this state
 */
internal class ReactiveStateBuilder<D> (
    override val type: AnyStateType,
    private val onEnter: OnEnter<D>,
) : StateBuilder<D>, MessageResponseBuilder<D> where D : StateData {

    /**
     * Map of message types to their handlers.
     * This is used to dispatch messages to the appropriate handler based on their type.
     */
    private val onMessageDispatch: MutableMap<AnyMessageType, OnMessageWrapper<D, out Message>> = mutableMapOf()

    /**
     * Function to check if a state should transition without receiving a message.
     * By default, this function always returns null, meaning no transition occurs.
     */
    private var fallbackTransition: FallbackTransition<D> = { null }

    /**
     * Builds a 'reactive' state behavior.
     *
     * @param resolver The resolver used to resolve state references
     * @return A state behavior for reactive states
     */
    override fun build(resolver: StateResolver): StateBehavior<D> = ReactiveStateBehavior(
        onEnterScope = onEnter,
        messageDispatcher = onMessageDispatch,
        fallbackTransition = fallbackTransition,
        stateResolver = resolver
    )

    /**
     * Defines a transition that occurs when a specific message is received.
     *
     * @param T The type of message that triggers this transition
     * @param message The type of message to respond to
     * @param onMessage Function to handle the message and determine the next state
     * @return This builder for method chaining
     * @throws IllegalArgumentException if a handler for this message type has already been defined
     */
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

    /**
     * Defines a function to check if a state should transition without receiving a message.
     * This function is chained with any previously defined fallback transitions.
     * The previously defined fallback transitions have precedence!
     *
     * @param onCheck Function to check if a transition should occur
     * @return This builder for method chaining
     */
    override fun checkTransition(onCheck: FallbackTransition<D>): FallbackTransitionBuilder<D> {
        val previous = fallbackTransition
        fallbackTransition = { send ->
            previous(send) ?: onCheck(send)
        }
        return this
    }

    /**
     * Defines a conditional transition that occurs if a condition is met.
     * This is a convenience method that wraps checkTransition.
     *
     * @param condition Function that returns true if the transition should occur
     * @param nextState Function that returns the next state data
     * @return This builder for method chaining
     */
    override fun transitionIf(
        condition: D.() -> Boolean,
        nextState: D.(Send) -> StateData,
    ): FallbackTransitionBuilder<D> = checkTransition { send ->
        condition().takeIf { it }?.let { nextState(send) }
    } // TODO extract as extension method on FallbackTransitionBuilder, similar to MessageResponseBuilder.on
}

/**
 * Implementation of [StateBehavior] for reactive states.
 * Reactive states respond to messages but do not necessarily immediately transition to other states.
 *
 * @param D The type of state data this behavior works with
 * @property onEnterScope Function to execute when entering this state
 * @property messageDispatcher Map of message types to their handlers
 * @property fallbackTransition Function to check if a state should transition without receiving a message
 * @property stateResolver Resolver used to resolve state references
 */
private class ReactiveStateBehavior<D : StateData>(
    private val onEnterScope: OnEnter<D>,
    private val messageDispatcher: Map<AnyMessageType, OnMessageWrapper<D, out Message>>,
    private val fallbackTransition: FallbackTransition<D>,
    private val stateResolver: StateResolver,
) : StateBehavior<D> {

    /**
     * Called when the state machine enters this state.
     * Executes the onEnterScope function and collects any events generated.
     *
     * @param data The state data for this state
     * @return Events generated by entering this state
     */
    override fun enter(data: D): Events = sendScope(data) { send ->
        data.onEnterScope(send)
    }.first

    /**
     * Resolves a message to its handler based on its type.
     *
     * @param T The type of message to resolve
     * @param data The state data for this state
     * @param message The message to resolve
     * @return The handler for this message type
     * @throws IllegalArgumentException if no handler is defined for this message type
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Message> resolveOnMessage(data: StateData, message: T): OnMessageWrapper<D, T> {
        val messageType = message::class
        return requireNotNull(messageDispatcher[messageType]) {
            "No behavior defined in state $data for message ${messageType.simpleName}: $message"
        }.let {
            it as? OnMessageWrapper<D, T>
        }!!
    }

    /**
     * Processes a message and determines if a state transition should occur.
     * Resolves the message to its handler and executes it.
     *
     * @param data The state data for this state
     * @param message The message to process
     * @return A [StateTransition] containing sent [Message]s and the potential next [State]
     */
    override fun processMessage(data: D, message: Message): StateTransition = sendScope(data) { send ->
        val onMessage = resolveOnMessage(data, message)
        onMessage(data, message, send)?.let { stateResolver.resolve(it) }
    }

    /**
     * Determines if a state transition should occur without receiving a message.
     * Executes the fallbackTransition function and resolves any resulting state.
     *
     * @param data The state data for this state
     * @return A [StateTransition] containing sent [Message]s and the potential next [State]
     */
    override fun fallbackTransition(data: D): StateTransition = sendScope(data) { send ->
        fallbackTransition(data, send)?.let {
            stateResolver.resolve(it)
        }
    }

    /**
     * Called when the state machine is interrupted.
     * This method is not implemented for reactive states.
     *
     * @param data The state data for this state
     * @return This method always throws an exception
     * @throws UnsupportedOperationException always, as interrupt is not implemented for reactive states
     */
    override fun interrupt(data: D): Events {
        throw UnsupportedOperationException("interrupt should not be called on ReactiveStates")
    }
}

/**
 * Functional interface for wrapping message handlers to move the receiver D to the parameter list.
 * This is used to provide type safety when dispatching messages to their handlers.
 *
 * @param D The type of state data
 * @param M The type of message
 */
private fun interface OnMessageWrapper<D, M> {
    /**
     * Invokes the message handler with the given data, message, and send function.
     *
     * @param data The state data for this state
     * @param message The message to handle
     * @param send Function for sending messages
     * @return The next state data, or null if no transition should occur
     */
    operator fun invoke(data: D, message: M, send: Send): StateData?
}
