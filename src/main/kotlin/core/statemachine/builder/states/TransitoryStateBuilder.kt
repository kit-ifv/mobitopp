package core.statemachine.builder.states

import core.statemachine.Event
import core.statemachine.Events
import core.statemachine.Message
import core.statemachine.ReusableSender
import core.statemachine.StateTransition
import core.statemachine.builder.MandatoryTransitionBuilder
import core.statemachine.builder.OnEnter
import core.statemachine.builder.StateBehavior
import core.statemachine.builder.StateBuilder
import core.statemachine.builder.StateData
import core.statemachine.builder.StateResolver
import core.statemachine.builder.StateType
import core.statemachine.builder.TransitionOnNext

internal class TransitoryStateBuilder<D>(
    override val type: StateType<D>,
    private val onEnter: OnEnter<D>
) : StateBuilder<D>, MandatoryTransitionBuilder<D> where D : StateData {

    private lateinit var nextTransition: TransitionOnNext<D>

    override fun next(onTransition: TransitionOnNext<D>) {
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
    private val nextStateTransition: TransitionOnNext<D>,
    private val stateResolver: StateResolver,
) : StateBehavior<D> where D : StateData {
    private val sendScope = ReusableSender()

    override fun enter(data: D): Events = sendScope(data) { send ->
        data.onEnterScope(send)
    }.first

    override fun processMessage(data: D, message: Message): StateTransition {
        throw UnsupportedOperationException("processMessage should not be called on TransitoryState")
    }
//
//    override fun checkMessageTransition(data: D, message: Message): StateTransition {
//        throw UnsupportedOperationException("checkMessageTransition should not be called on TransitoryState")
//    }

    override fun fallbackTransition(data: D): StateTransition {
        val nextState = data.nextStateTransition()
        return emptyList<Event<*>>() to stateResolver.resolve(nextState)
    }

    override fun interrupt(data: D): Events {
        TODO("Not yet implemented")
    }
}
