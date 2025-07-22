package core.statemachine.usage

import core.statemachine.Agent
import core.statemachine.Event
import core.statemachine.Events
import core.statemachine.Message
import core.statemachine.State
import core.statemachine.StateMachine
import core.statemachine.StateMachineFactory
import core.statemachine.TransitoryStateMachine
import utils.units.AbsoluteTime

/**
 * A [core.statemachine.StateMachineFactory] for creating a [RecordingStateMachine].
 * It wraps another [factory] used to delegate [initialState] creation.
 *
 * @param A generic type of agent to create state machines for
 * @property factory delegate [core.statemachine.StateMachineFactory] used to delegate [initialState] creation
 * @constructor Create empty Recording state machine factory
 */
class RecordingStateMachineFactory<A>(
    private val factory: StateMachineFactory<A>,
) : StateMachineFactory<A> where A : Agent<out Message> {
    private val registry = mutableMapOf<A, RecordingStateMachine>()

    override val name: String get() = factory.name

    override fun create(startTime: AbsoluteTime, agent: A): StateMachine =
        create(initialState(startTime, agent)).also {
            registry[agent] = it
        }

    override fun create(initialState: State): RecordingStateMachine {
        return RecordingStateMachine(name, initialState)
    }

    override fun initialState(startTime: AbsoluteTime, agent: A): State =
        factory.initialState(startTime, agent)

    /**
     * Returns the stored state machine of the given agent.
     *
     * @param agent agent for with the state machine should be obtained from lookup
     * @return stored state machine of the given agent or null if not yet registered
     */
    fun of(agent: A) = registry[agent]
}

/**
 * Wrap the receiver [StateMachineFactory] with a [RecordingStateMachineFactory]
 *
 * @param A the generic type of agent for which state machines can be built
 * @return a [RecordingStateMachineFactory] wrapping this factory
 */
fun <A> StateMachineFactory<A>.withRecording(): RecordingStateMachineFactory<A> where A : Agent<out Message> =
    RecordingStateMachineFactory(this)

/**
 * Extension of [core.statemachine.TransitoryStateMachine] that records the history and usage of visited states.
 * Useful for debugging and testing state machines.
 * StateMachine Usage is recorded and stored globally in the companion object:
 * [RecordingStateMachine.stateMachineUsage].
 *
 * @property name The name of this state machine
 * @property initial The initial state of this state machine
 */
class RecordingStateMachine(
    name: String,
    initial: State
) : TransitoryStateMachine(name, initial) {

    init {
        globalUsage.registerStateMachine(this, initial)
    }

    /**
     * The history of states visited by this state machine since the last [clearHistory].
     */
    val history: List<State>
        get() = stateHistory
    private val stateHistory = mutableListOf<State>()

    /**
     * Clears the history of visited states.
     */
    fun clearHistory() {
        stateHistory.clear()
    }

    private var enterTimeOfCurrentState: AbsoluteTime = initial.time
    private val timeSinceEnter get() = currentState.time - enterTimeOfCurrentState

    override fun process(message: Message): Events {
        val result: MutableList<Event<*>> = mutableListOf()

        val (onMessageResponses, transitionTo) = currentState.processMessage(message)
        result += onMessageResponses

        globalUsage.registerTransitionOnMessage(
            this,
            currentState,
            message,
            transitionTo,
            timeSinceEnter,
            onMessageResponses
        )

        val nextState = transitionTo ?: run {
            val (onFallbackResponses, transitionElse) = currentState.fallbackTransition()
            result += onFallbackResponses

            globalUsage.registerFallbackTransition(
                this,
                currentState,
                transitionElse,
                timeSinceEnter,
                onFallbackResponses
            )

            transitionElse
        }

        nextState?.let { state ->
            result += findNonTransitoryState(state)
        }

        return result
    }

    override fun findNonTransitoryState(state: State): Events {
        val result: MutableList<Event<*>> = mutableListOf()

        var next: State? = state
        while (next != null) {
            val onEnterMessages = next.enter()
            result += onEnterMessages

            currentState = next
            enterTimeOfCurrentState = currentState.time

            val (onFallbackMessages, transitionElse) = currentState.fallbackTransition()
            result += onFallbackMessages
            next = transitionElse

            stateHistory += currentState
            globalUsage.registerEnter(this, currentState, onEnterMessages)
            globalUsage.registerFallbackTransition(
                this,
                currentState,
                transitionElse,
                timeSinceEnter,
                onFallbackMessages
            )
        }

        return result
    }

    companion object {
        val stateMachineUsage: GlobalStateMachineUsage get() = globalUsage
        private val globalUsage = GlobalStateMachineUsageRecorder()
    }
}
