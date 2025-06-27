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
    fun create(startTime: AbsoluteTime, agent: A): StateMachine
}

data class TransitoryStateMachine(
    override val name: String,
    private val initial: State // TODO avoid storing initial state, maybe initialize current state right away?
) : StateMachine {

    private lateinit var currentState: State

    override fun start(): Events {
        return enter(initial)
    }

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
            result += enter(state)
        }

        return result
    }

    private fun enter(state: State): Events {
        val result: MutableList<Event<*>> = mutableListOf()

        var nextState: State = state
        var nextTransition: State? = state
        while (nextTransition != null) {
            nextState = nextTransition
            result += nextState.enter()
            val (messages, transitionElse) = nextState.fallbackTransition()
            result += messages
            nextTransition = transitionElse
        }

        currentState = nextState

        return result
    }
}
