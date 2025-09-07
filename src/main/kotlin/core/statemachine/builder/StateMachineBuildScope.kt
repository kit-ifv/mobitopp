package core.statemachine.builder

import core.statemachine.Agent
import core.statemachine.StateMachineFactory
import core.statemachine.builder.states.FinalStateBuilder
import core.statemachine.builder.states.ReactiveStateBuilder
import core.statemachine.builder.states.TransitoryStateBuilder
import utils.units.AbsoluteTime

/**
 * Creates a state machine factory for a specific agent type using a DSL-style builder.
 * This function provides a scope for building the states of a state machine.
 *
 * @param A The type of agent this state machine is for
 * @param name The name of the state machine
 * @param scope A lambda with receiver that defines the states of the state machine
 * @return A factory that can create instances of the defined state machine for a given agent
 */
fun <A> stateMachine(
    name: String,
    scope: StateMachineBuilder<A>.() -> Unit
): StateMachineFactory<A> where A : Agent<*> {
    val builder = StateMachineBuilderImpl<A>(name)
    builder.scope()
    return builder.build()
}

/**
 * Implementation of [StateMachineBuilder] that collects state definitions and builds a state machine factory.
 * This class is responsible for detecting duplicate state definitions and creating the appropriate state builders.
 *
 * @param A The type of agent this state machine builder is for
 * @property name The name of the state machine being built
 */
private class StateMachineBuilderImpl<A>(
    private val name: String
) : StateMachineBuilder<A> where A : Agent<*> {

    private val builders: MutableList<StateBuilder<out StateData>> = mutableListOf()

    /**
     * The set of state types that have been defined in this state machine.
     * Used to prevent duplicate state definitions.
     */
    private val stateTypes: MutableSet<AnyStateType> = mutableSetOf()

    /**
     * Function to create the initial state data for an agent at a specific time.
     */
    private lateinit var initialize: (AbsoluteTime, A) -> StateData

    /**
     * Defines the start state for this state machine.
     * The start state is the first state an agent enters when the state machine starts.
     *
     * @param D The type of state data for this state
     * @param state The type of state to define
     * @param initialize Function to initialize the state data
     * @param onEnter Optional function to execute when entering this state
     * @return A builder for defining message responses for this state
     * @throws IllegalArgumentException if a start state has already been defined
     */
    override fun <D : StateData> start(
        state: StateType<D>,
        initialize: (AbsoluteTime, A) -> D,
        onEnter: OnEnter<D>?
    ): MessageResponseBuilder<D> {
        require(!this::initialize.isInitialized) {
            "Start state was already defined for state machine: $name"
        }

        this.initialize = initialize
        return this.state(state, onEnter)
    }

    /**
     * Defines a 'reactive' state for this state machine.
     * Reactive states respond to messages but do not necessarily immediately transition to other states.
     *
     * @param D The type of state data for this state
     * @param state The type of state to define
     * @param onEnter Optional function to execute when entering this state
     * @return A builder for defining message responses for this state
     * @throws IllegalArgumentException if a state with this type has already been defined
     */
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

    /**
     * Defines a transitory state for this state machine.
     * Transitory states immediately transition to another state without receiving a message
     * or passing time within the state.
     *
     * @param D The type of state data for this state
     * @param state The type of state to define
     * @param onEnter Optional function to execute when entering this state
     * @return A builder for defining the mandatory transition for this state
     * @throws IllegalArgumentException if a state with this type has already been defined
     */
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

    /**
     * Defines a final state for this state machine.
     * Final states do not transition to other states and represent the end of a state machine.
     *
     * @param D The type of state data for this state
     * @param state The type of state to define
     * @param onEnter Optional function to execute when entering this state
     * @throws IllegalArgumentException if a state with this type has already been defined
     */
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

    /**
     * Builds a state machine factory from the collected state definitions.
     *
     * @return A factory that can create instances of the defined state machine
     */
    fun build(): StateMachineFactory<A> = CachedBehaviorStateMachineFactory(builders, initialize, name)
}
