package core.statemachine.builder

import core.statemachine.Agent
import core.statemachine.Message
import core.statemachine.MessageType
import core.statemachine.Send
import utils.units.AbsoluteTime

/**
 * Type alias for a function that is called when a state is entered.
 * The function has access to the state data and can send messages.
 *
 * @param D The type of state data
 */
typealias OnEnter<D> = D.(Send) -> Unit

/**
 * Type alias for a function that is called when a message is received.
 * The function has access to the state data, the message, and can send messages.
 *
 * @param D The type of state data
 * @param M The type of message
 */
typealias OnMessage<D, M> = D.(M, Send) -> Unit

/**
 * Type alias for a function that is called when a message is received and may cause a state transition.
 * The function has access to the state data, the message, can send messages, and returns the next state data or null.
 *
 * @param D The type of state data
 * @param M The type of message
 */
typealias TransitionOnMessage<D, M> = D.(M, Send) -> StateData?

/**
 * Type alias for a function that is called to check if a state should transition without receiving a message.
 * The function has access to the state data, can send messages, and returns the next state data or null.
 *
 * @param D The type of state data
 */
typealias FallbackTransition<D> = D.(Send) -> StateData?

/**
 * Type alias for a function that is called to determine the next state for a transitory state.
 * The function has access to the state data, can send messages, and returns the next state data.
 *
 * @param D The type of state data
 */
typealias TransitionToNext<D> = D.(Send) -> StateData

/**
 * Interface for building state machines for specific agent types.
 * This interface provides methods for defining different types of states and their behaviors.
 *
 * @param A The type of agent this state machine builder is for
 */
interface StateMachineBuilder<A> where A : Agent<*> {

    /**
     * Defines the start state for this state machine.
     * The start state is the first state an agent enters when the state machine starts.
     *
     * @param D The type of state data for this state
     * @param state The type of state to define
     * @param initialize Function to initialize the state data
     * @param onEnter Optional function to execute when entering this state
     * @return A builder for defining message responses for this state
     */
    fun <D : StateData> start(
        state: StateType<D>,
        initialize: (AbsoluteTime, A) -> D,
        onEnter: OnEnter<D>? = null
    ): MessageResponseBuilder<D>

    /**
     * Defines a 'reactive' state for this state machine.
     * Reactive states respond to messages but do not necessarily immediately transition to other states.
     *
     * @param D The type of state data for this state
     * @param state The type of state to define
     * @param onEnter Optional function to execute when entering this state
     * @return A builder for defining message responses for this state
     */
    fun <D> state(state: StateType<D>, onEnter: OnEnter<D>? = null): MessageResponseBuilder<D> where D : StateData

    /**
     * Defines a transitory state for this state machine.
     * Transitory states immediately transition to another state without receiving a message
     * or passing time within the state.
     *
     * @param D The type of state data for this state
     * @param state The type of state to define
     * @param onEnter Optional function to execute when entering this state
     * @return A builder for defining the mandatory transition for this state
     */
    fun <D> transState(
        state: StateType<D>,
        onEnter: OnEnter<D>? = null
    ): MandatoryTransitionBuilder<D> where D : StateData
    // TODO actually there is no need for an onEnter function in transStates,
    // the onEnter logic can always be prepended to the next transition code block
    // maybe only for structuring purposes

    /**
     * Defines a final state for this state machine.
     * Final states do not transition to other states and represent the end of a state machine.
     *
     * @param D The type of state data for this state
     * @param state The type of state to define
     * @param onEnter Optional function to execute when entering this state
     */
    fun <D> finState(state: StateType<D>, onEnter: OnEnter<D>? = null) where D : StateData
}

/**
 * Interface for building fallback transitions for non-transitory states.
 *
 * Fallback transitions are used when a state should transition 'spontaneously'
 * without receiving a message e.g., based on state variables in the [StateData].
 * They are checked after each invocation of the enter state and process message behavior.
 *
 * After declaring the first fallback transition in the flow builder pattern,
 * only additional fallback transitions can be specified, but no more message-based transitions.
 * This enforces a structure when building a state: enter logic > message handling > fallback transitions.
 *
 * If multiple fallback transitions are defined they are checked in order of definition:
 * the first transition function that returns a new state determines the next state.
 *
 * @param D The type of state data this builder works with
 */
interface FallbackTransitionBuilder<D> where D : StateData {

    /**
     * Defines a function to check if a state should transition without receiving a message.
     *
     * @param onCheck Function to check if a transition should occur,
     *      it returns the new state data if a transition if required
     * @return This builder for method chaining
     */
    fun checkTransition(onCheck: FallbackTransition<D>): FallbackTransitionBuilder<D>

    /**
     * Defines a conditional transition that occurs if a condition is met.
     *
     * @param condition Function that returns true if the transition should occur
     * @param nextState Function that returns the next state data
     * @return This builder for method chaining
     */
    fun transitionIf(condition: D.() -> Boolean, nextState: D.(Send) -> StateData): FallbackTransitionBuilder<D>
}

/**
 * Interface for building message responses for states.
 * Message responses define how states respond to messages and when they should transition to other states.
 *
 * Also implements [FallbackTransitionBuilder] for method chaining in the flow builder pattern:
 * After declaring the first fallback transition in the flow builder pattern,
 * only additional fallback transitions can be specified, but no more message-based transitions.
 * This enforces a structure when building a state: enter logic > message handling > fallback transitions.
 *
 * @param D The type of state data this builder works with
 */
interface MessageResponseBuilder<D> : FallbackTransitionBuilder<D> where D : StateData {

    /**
     * Defines a transition that occurs when a specific message is received.
     *
     * @param T The type of message that triggers this transition
     * @param message The type of message to respond to
     * @param onMessage Function to handle the message and determine the (potential) next state
     * @return This builder for method chaining
     */
    fun <T> transitionOn(
        message: MessageType<T>,
        onMessage: TransitionOnMessage<D, T>
    ): MessageResponseBuilder<D> where T : Message
}

/**
 * Extension function that defines message processing behavior, that does not cause a state transition.
 * This is a convenience function that wraps transitionOn and always returns null for the next state.
 *
 * @param D The type of state data
 * @param T The type of message to handle
 * @param message The type of message to respond to
 * @param onMessage Function to handle the message
 * @return This builder for method chaining
 */
fun <D : StateData, T> MessageResponseBuilder<D>.on(
    message: MessageType<T>,
    onMessage: OnMessage<D, T>
): MessageResponseBuilder<D> where T : Message {
    return transitionOn(message) { message, send ->
        onMessage(message, send)
        null
    }
}

/**
 * Interface for building mandatory transitions for transitory states.
 * Transitory states must always define the next state to transition to.
 *
 * @param D The type of state data this builder works with
 */
interface MandatoryTransitionBuilder<D> where D : StateData {

    /**
     * Defines the next state transitioning behavior for a transitory state.
     *
     * @param onTransition Function that returns the next state data
     */
    fun next(onTransition: TransitionToNext<D>)
}
