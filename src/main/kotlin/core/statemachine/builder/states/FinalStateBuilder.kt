package core.statemachine.builder.states

import core.statemachine.Events
import core.statemachine.Message
import core.statemachine.NULL_TRANSITION
import core.statemachine.StateTransition
import core.statemachine.builder.AnyStateType
import core.statemachine.builder.OnEnter
import core.statemachine.builder.StateBehavior
import core.statemachine.builder.StateBuilder
import core.statemachine.builder.StateData
import core.statemachine.builder.StateResolver
import core.statemachine.sendScope

internal class FinalStateBuilder<D>(
    override val type: AnyStateType,
    private val onEnterScope: OnEnter<D>,
) : StateBuilder<D> where D : StateData {

    override fun build(resolver: StateResolver): StateBehavior<D> = FinalStateBehavior(onEnterScope)
}

private class FinalStateBehavior<D>(
    private val onEnterScope: OnEnter<D>,
) : StateBehavior<D> where D : StateData {

    override fun enter(data: D): Events = sendScope(data) { send ->
        data.onEnterScope(send)
    }.first

    override fun processMessage(data: D, message: Message): StateTransition {
        throw UnsupportedOperationException("processMessage should not be called on FinalStates")
    }

    override fun fallbackTransition(data: D): StateTransition = NULL_TRANSITION

    override fun interrupt(data: D): Events {
        error("Not implemented")
    }
}
