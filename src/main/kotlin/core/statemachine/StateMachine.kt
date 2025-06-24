package core.statemachine

interface StateMachine {
    val name: String
    fun start(): Events
    fun process(message: Message): Events
}


interface State {
    val name: String

    fun enter(): Events
    fun processMessage(message: Message): Events
    fun checkMessageTransition(message: Message): State?
    fun checkConditionTransition(): State?
    fun interrupt(): Events
}


interface StateMachineFactory<A: Agent<out Message>> {
    fun create(agent: A) : StateMachine
}


data class TransitoryStateMachine(
    override val name: String,
    private val initial: State
): StateMachine {

    private lateinit var currentState: State

    override fun start(): Events {
        return enter(initial)
    }

    override fun process(message: Message): Events {
        val result: MutableList<Event<*>> = mutableListOf()

        result += currentState.processMessage(message)

        val nextState = currentState.checkMessageTransition(message) ?: currentState.checkConditionTransition()

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
            nextTransition = nextState.checkConditionTransition()
        }

        currentState = nextState

        return result
    }

}

