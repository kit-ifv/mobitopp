package states_cleaned

fun stateMachine(name: String, scope: StateMachineBuilder.() -> Unit): StateMachineFactoryBuilder {
    val builder = StateMachineBuilderImpl(name)
    builder.scope()
    return builder
}

interface StateResolver {
    fun <D: StateData> resolve(stateData: D): State?
}

class StateMachineBuilderImpl(
    private val name: String
): StateMachineBuilder, StateMachineFactoryBuilder {

    private val builders: MutableList<StateBuilder<out StateData>> = mutableListOf()
    private val stateTypes: MutableSet<AnyStateType> = mutableSetOf()

    override fun <D : StateData> state(
        state: StateType<D>,
        onEnter: OnEnter<D>?
    ): MessageResponseBuilder<D> {
        require(state !in stateTypes) {
            "state machine $name already has a definition of state: $state"
        }
        val builder = StateBuilderImpl<D>(state, onEnter ?: {})
        builders.add(builder)
        stateTypes.add(state)
        return builder
    }

    override fun <D : StateData> transState(
        state: StateType<D>,
        onEnter: OnEnter<D>?
    ): TransitoryStateBuilder<D> {
        require(state !in stateTypes) {
            "state machine $name already has a definition of state: $state"
        }
        val builder = TransitoryStateBuilderImpl<D>(state, onEnter ?: {})
        builders.add(builder)
        stateTypes.add(state)
        return builder
    }

    override fun <D: StateData> finState(
        state: StateType<D>,
        onEnter: OnEnter<D>?
    ) {
        val builder = object: StateBuilder<D> {
            override val type = state
            override fun build(resolver: StateResolver) = FinalStateBehavior<D>(onEnter ?: {})
        }
        builders.add(builder)
        stateTypes.add(state)
    }

    override fun <A> initialState(initializer: (A) -> StateData): StateMachineFactory<A> where A: Agent<out Message> =
        StateMachineFactoryImpl(builders, name, initializer)

}

private data class StateImpl<D: StateData>(
    private val data: D,
    private val behavior: StateBehavior<D>,
): State {
    override val name = data::class.simpleName!!

    override fun enter() = behavior.enter(data)
    override fun processMessage(message: Message) = behavior.processMessage(data, message)
    override fun checkMessageTransition(message: Message) = behavior.checkMessageTransition(data, message)
    override fun checkConditionTransition() = behavior.checkConditionTransition(data)
    override fun interrupt() = behavior.interrupt(data)

}

private data class StateMachineImpl(
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

class StateMachineFactoryImpl<A: Agent<out Message>>(
    builders: List<StateBuilder<out StateData>>,
    private val name: String,
    private val initializer: (A) -> StateData
): StateResolver, StateMachineFactory<A> {

    private val states: Map<AnyStateType, StateBehavior<out StateData>> = builders.associate {
        it.type to it.build(this)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <D> resolve(stateData: D): State where D : StateData {
        val stateType = stateData.type
        return requireNotNull(states[stateType]) {
            "No state definition found for state data: $stateData"
        }.let {
            it as? StateBehavior<D>
        }?.let {
            StateImpl<D>(stateData, it)
        }!!
    }


    override fun create(agent: A): StateMachine = StateMachineImpl(
        name = name,
        initial = resolve(initializer(agent))
    )

}

