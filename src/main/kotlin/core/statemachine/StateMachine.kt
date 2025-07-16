package core.statemachine

import utils.units.AbsoluteTime

interface StateMachine {
    val name: String
    fun start(): Events

    fun process(event: Event<*>): Events {
        setTime(event.receiveTime)
        return process(event.content)
    }

    fun setTime(time: AbsoluteTime)
    fun process(message: Message): Events
}

typealias StateTransition = Pair<Events, State?>
val NULL_TRANSITION = (emptyList<Event<*>>() to null)

interface State {
    val name: String

    fun updateTime(time: AbsoluteTime)
    fun enter(): Events
    fun processMessage(message: Message): StateTransition
    fun fallbackTransition(): StateTransition
    fun interrupt(): Events
}

interface StateMachineFactory<A : Agent<out Message>> {
    fun create(startTime: AbsoluteTime, agent: A): StateMachine =
        create(initialState(startTime, agent))

    fun create(initialState: State): StateMachine
    fun initialState(startTime: AbsoluteTime, agent: A): State
}

open class TransitoryStateMachine(
    override val name: String,
    private val initial: State // TODO avoid storing initial state, maybe initialize current state right away?
) : StateMachine {

    protected lateinit var currentState: State

    override fun start(): Events = findNonTransitoryState(initial)

    override fun setTime(time: AbsoluteTime) {
        currentState.updateTime(time)
    }

    override fun process(message: Message): Events {
        val result: MutableList<Event<*>> = mutableListOf()

        val (responses, transitionTo) = currentState.processMessage(message)
        result += responses

        val nextState = transitionTo ?: run {
            val (messages, transitionElse) = currentState.fallbackTransition()
            result += messages
            transitionElse
        }

        nextState?.let { state ->
            result += findNonTransitoryState(state)
        }

        return result
    }

    protected open fun findNonTransitoryState(state: State): Events {
        val result: MutableList<Event<*>> = mutableListOf()

        var nextState: State = state
        var nextTransition: State? = state
        while (nextTransition != null) {
            nextState = nextTransition
            result += nextState.enter()

            val (instantTransitionResponse, transitionElse) = nextState.fallbackTransition()
            result += instantTransitionResponse

            nextTransition = transitionElse
        }

        currentState = nextState

        return result
    }
}

class RecordingStateMachine(
    name: String,
    initial: State
) : TransitoryStateMachine(name, initial) {

    val history: List<State>
        get() = stateHistory
    private val stateHistory = mutableListOf<State>()

    fun clearHistory() {
        stateHistory.clear()
    }

    override fun findNonTransitoryState(state: State): Events {
        val result: MutableList<Event<*>> = mutableListOf()

        var nextState: State = state
        var nextTransition: State? = state
        while (nextTransition != null) {
            nextState = nextTransition
            result += nextState.enter()
            stateHistory += nextState
            val (messages, transitionElse) = nextState.fallbackTransition()
            result += messages
            nextTransition = transitionElse
        }

        currentState = nextState

        return result
    }
}
