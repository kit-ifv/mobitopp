package core.statemachine.builder

import core.statemachine.Agent
import core.statemachine.StateMachineFactory
import core.statemachine.Time
import core.statemachine.builder.states.FinalStateBuilder
import core.statemachine.builder.states.ReactiveStateBuilder
import core.statemachine.builder.states.TransitoryStateBuilder

fun <A> stateMachine(
    name: String,
    scope: StateMachineBuilder<A>.() -> Unit
): StateMachineFactory<A> where A : Agent<*> {
    val builder = StateMachineBuilderImpl<A>(name)
    builder.scope()
    return builder.build()
}

private class StateMachineBuilderImpl<A>(
    private val name: String
) : StateMachineBuilder<A> where A : Agent<*> {

    private val builders: MutableList<StateBuilder<out StateData>> = mutableListOf()
    private val stateTypes: MutableSet<AnyStateType> = mutableSetOf()
    private lateinit var initialize: (Time, A) -> StateData

//    override fun start(onEnter: OnEnter<StartState<A>>?): MessageResponseBuilder<StartState<A>> {
//        val state = StartState::class
//        require(state !in stateTypes) { stateExistsError(state) }
//        val builder = ReactiveStateBuilder<StartState<A>>(state, onEnter ?: {})
//        builders.add(builder)
//        stateTypes.add(state)
//        return builder
//    }

    override fun <D : StateData> start(
        state: StateType<D>,
        initialize: (Time, A) -> D,
        onEnter: OnEnter<D>?
    ): MessageResponseBuilder<D> {
        require(!this::initialize.isInitialized) {
            "Start state was already defined for state machine: $name"
        }

        this.initialize = initialize
        return this.state(state, onEnter)
    }

//    override fun <S> subStart(onEnter: OnEnter<SubStartState<A, S>>?): MessageResponseBuilder<SubStartState<A, S>> {
//        val state = SubStartState::class
//        require(state !in stateTypes) { stateExistsError(state) }
//        val builder = ReactiveStateBuilder<SubStartState<A, S>>(state, onEnter ?: {})
//        builders.add(builder)
//        stateTypes.add(state)
//        return builder
//    }

    override fun <D : StateData> state(
        state: StateType<D>,
        onEnter: OnEnter<D>?
    ): MessageResponseBuilder<D> {
        require(state !in stateTypes) { stateExistsError(state) }
        val builder = ReactiveStateBuilder<D>(state, onEnter ?: {})
        builders.add(builder)
        stateTypes.add(state)
        return builder
    }

    override fun <D : StateData> transState(
        state: StateType<D>,
        onEnter: OnEnter<D>?
    ): MandatoryTransitionBuilder<D> {
        require(state !in stateTypes) { stateExistsError(state) }
        val builder = TransitoryStateBuilder<D>(state, onEnter ?: {})
        builders.add(builder)
        stateTypes.add(state)
        return builder
    }

    override fun <D : StateData> finState(
        state: StateType<D>,
        onEnter: OnEnter<D>?
    ) {
        require(state !in stateTypes) { stateExistsError(state) }
        val builder = FinalStateBuilder<D>(state, onEnter ?: {})
        builders.add(builder)
        stateTypes.add(state)
    }

    private fun <D : StateData> stateExistsError(state: StateType<D>): String =
        "state machine $name already has a definition of state: $state"

    fun build(): StateMachineFactory<A> = CachedBehaviorStateMachineFactory(builders, initialize, name)
}
