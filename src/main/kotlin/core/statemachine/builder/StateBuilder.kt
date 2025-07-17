package core.statemachine.builder

import core.statemachine.State

/**
 * Interface for building state behaviors from state data.
 * State builders are used in the state machine building process to define how states behave.
 *
 * @param D The type of state data this builder works with
 */
interface StateBuilder<D> where D : StateData {

    val type: AnyStateType

    /**
     * Builds a state behavior using the provided state resolver.
     *
     * @param resolver The resolver used to resolve state references
     * @return A state behavior for the specified state data type
     */
    fun build(resolver: StateResolver): StateBehavior<D>
}

/**
 * Interface for resolving state data to actual state instances.
 * This is used during the state machine building process to connect state references.
 * This way, the state type of the agent-dependent state data can be used
 * to look up the shared state behavior implementation.
 */
interface StateResolver {
    /**
     * Resolves state data to an actual state instance.
     *
     * @param D The type of state data to resolve
     * @param stateData The state data to resolve
     * @return The resolved state, or null if the state cannot be resolved
     */
    fun <D : StateData> resolve(stateData: D): State?
}
