package core.statemachine.builder

import core.statemachine.Agent
import core.statemachine.Message
import core.statemachine.StateMachineFactory
import core.statemachine.builder.states.FinalStateBuilder
import core.statemachine.builder.states.TemporalStateBuilder
import core.statemachine.builder.states.TransitoryStateBuilder

fun stateMachine(name: String, scope: StateMachineBuilder.() -> Unit): StateMachineFactoryBuilder {
    val builder = StateMachineBuilderImpl(name)
    builder.scope()
    return builder
}

private class StateMachineBuilderImpl(
    private val name: String
) : StateMachineBuilder, StateMachineFactoryBuilder {

    private val builders: MutableList<StateBuilder<out StateData>> = mutableListOf()
    private val stateTypes: MutableSet<AnyStateType> = mutableSetOf()

    override fun <D : StateData> state(
        state: StateType<D>,
        onEnter: OnEnter<D>?
    ): MessageResponseBuilder<D> {
        require(state !in stateTypes) {
            "state machine $name already has a definition of state: $state"
        }
        val builder = TemporalStateBuilder<D>(state, onEnter ?: {})
        builders.add(builder)
        stateTypes.add(state)
        return builder
    }

    override fun <D : StateData> transState(
        state: StateType<D>,
        onEnter: OnEnter<D>?
    ): MandatoryTransitionBuilder<D> {
        require(state !in stateTypes) {
            "state machine $name already has a definition of state: $state"
        }
        val builder = TransitoryStateBuilder<D>(state, onEnter ?: {})
        builders.add(builder)
        stateTypes.add(state)
        return builder
    }

    override fun <D : StateData> finState(
        state: StateType<D>,
        onEnter: OnEnter<D>?
    ) {
        val builder = FinalStateBuilder<D>(state, onEnter ?: {})
        builders.add(builder)
        stateTypes.add(state)
    }

    override fun <A> initialState(initializer: (A) -> StateData): StateMachineFactory<A> where A : Agent<out Message> =
        StateMachineFactoryImpl(builders, name, initializer)
}
