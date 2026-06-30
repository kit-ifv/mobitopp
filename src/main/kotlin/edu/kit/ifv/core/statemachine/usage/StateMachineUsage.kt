package edu.kit.ifv.core.statemachine.usage
import kotlin.time.Duration

/**
 * GlobalStateMachineUsage records [StateMachineUsage] for all observed state machines.
 *
 * @property usageByStateMachine map of [StateMachineUsage] by StateMachine name
 */
interface GlobalStateMachineUsage {
    val usageByStateMachine: Map<String, StateMachineUsage>
}

/**
 * StateMachineUsage records usage statistics including
 *  - number of state machine instances
 *  - initialState
 *  - [State] usage
 *  - state transition usage
 */
interface StateMachineUsage {
    val name: String
    val instanceCount: Int
    val initialState: String
    val stateUsages: Map<String, StateUsage>
    val transitionUsages: Map<TransitionKey, TransitionUsage>

    /**
     * Check if the recorded state machine has any self-transition in the given state.
     * A self-transition occurs when state logic is executed, but it remains in the current state.
     * In some cases, a new state object of the same StateType is created, which does not count as a self-transition.
     *
     * @param state the
     */
    fun hasSelfTransition(state: String) = transitionUsages.keys.any {
        it.from == state && it.to == state && it.stayInState
    }
}

/**
 * StateUsage records the Usage of different messages distinguished by trigger.
 * A trigger is either a "state Enter" or a "received message" event.
 */
interface StateUsage {
    val name: String
    val messagesByTrigger: Map<String, SendMessageUsage>
}

/**
 * SendMessageUsage counts the messages sent in response to a specific [trigger].
 * A trigger is either a "state Enter" or a "received message" event.
 */
interface SendMessageUsage {
    val trigger: String
    val messageCount: Map<String, Int>
}

/**
 * A TransitionKey contains properties to distinguish transitions for counting.
 *
 * @property from the state before the transition
 * @property to the state after the transition
 * @property message the (optional) message triggering the transition
 * @property stayInState whether the state object is not changed
 */
data class TransitionKey(val from: String, val to: String, val message: String?, val stayInState: Boolean)

/**
 * TransitionUsage counts the use of a certain transition
 * and records the average duration spent in state before the transition.
 */
interface TransitionUsage {
    val key: TransitionKey
    val count: Int
    val avgTimeSinceEnter: Duration
}

val TransitionUsage.from get() = key.from
val TransitionUsage.to get() = key.to
val TransitionUsage.message get() = key.message
val TransitionUsage.stayInState get() = key.stayInState
