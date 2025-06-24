package core.statemachine.builder

import core.statemachine.State

interface StateBuilder<D> where D: StateData {
    val type: StateType<D>
    fun build(resolver: StateResolver): StateBehavior<D>
}

interface StateResolver {
    fun <D: StateData> resolve(stateData: D): State?
}