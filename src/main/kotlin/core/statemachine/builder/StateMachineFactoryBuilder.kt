package core.statemachine.builder

import core.statemachine.Agent
import core.statemachine.Message
import core.statemachine.State
import core.statemachine.StateMachine
import core.statemachine.StateMachineFactory
import core.statemachine.TransitoryStateMachine

interface StateMachineFactoryBuilder {
    fun <A> initialState(initializer: (A) -> StateData): StateMachineFactory<A> where A : Agent<out Message>
}

internal class StateMachineFactoryImpl<A : Agent<out Message>>(
    builders: List<StateBuilder<out StateData>>,
    private val name: String,
    private val initializer: (A) -> StateData
) : StateResolver, StateMachineFactory<A> {

    private val states: Map<AnyStateType, StateBehavior<out StateData>> = builders.associate {
        it.type to it.build(this)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <D> resolve(stateData: D): State where D : StateData {
        val stateType = stateData::class
        return requireNotNull(states[stateType]) {
            "No state definition found for state data: $stateData"
        }.let {
            it as? StateBehavior<D>
        }?.let {
            StateImpl<D>(stateData, it)
        }!!
    }

    override fun create(agent: A): StateMachine = TransitoryStateMachine(
        name = name,
        initial = resolve(initializer(agent))
    )
}

private data class StateImpl<D : StateData>(
    private val data: D,
    private val behavior: StateBehavior<D>,
) : State {
    override val name = data::class.simpleName!!

    override fun enter() = behavior.enter(data)
    override fun processMessage(message: Message) = behavior.processMessage(data, message)

    override fun fallbackTransition() = behavior.fallbackTransition(data)
    override fun interrupt() = behavior.interrupt(data)
}
