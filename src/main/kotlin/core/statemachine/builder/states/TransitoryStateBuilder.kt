package core.statemachine.builder.states

import core.statemachine.Events
import core.statemachine.Message
import core.statemachine.ReusableSender
import core.statemachine.StateTransition
import core.statemachine.builder.AnyStateType
import core.statemachine.builder.MandatoryTransitionBuilder
import core.statemachine.builder.OnEnter
import core.statemachine.builder.StateBehavior
import core.statemachine.builder.StateBuilder
import core.statemachine.builder.StateData
import core.statemachine.builder.StateResolver
import core.statemachine.builder.TransitionToNext

internal class TransitoryStateBuilder<D>(
    override val type: AnyStateType,
    private val onEnter: OnEnter<D>
) : StateBuilder<D>, MandatoryTransitionBuilder<D> where D : StateData {

    private lateinit var nextTransition: TransitionToNext<D>

    override fun next(onTransition: TransitionToNext<D>) {
        nextTransition = onTransition
    }

    override fun build(resolver: StateResolver): StateBehavior<D> = TransitoryStateBehavior(
        onEnterScope = onEnter,
        nextStateTransition = nextTransition,
        stateResolver = resolver,
    )
}

private class TransitoryStateBehavior<D>(
    private val onEnterScope: OnEnter<D>,
    private val nextStateTransition: TransitionToNext<D>,
    private val stateResolver: StateResolver,
) : StateBehavior<D> where D : StateData {
    private val sendScope = ReusableSender()

    override fun enter(data: D): Events = sendScope(data) { send ->
        data.onEnterScope(send)
    }.first

    override fun processMessage(data: D, message: Message): StateTransition {
        throw UnsupportedOperationException("processMessage should not be called on TransitoryState")
    }

    override fun fallbackTransition(data: D): StateTransition = sendScope(data) { send ->
        val nextState = data.nextStateTransition(send)
        stateResolver.resolve(nextState)
    }

    override fun interrupt(data: D): Events {
        error("Not implemented")
    }
}
