package core.statemachine.usage

import core.statemachine.Events
import core.statemachine.Message
import core.statemachine.State
import core.statemachine.StateMachine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * An implementation of [GlobalStateMachineUsage] for recording the usage.
 * It provides the following functions to register usage:
 *  - register new state machine instance: [registerStateMachine]
 *  - register an "enter state" event: [registerEnter]
 *  - register a transition on message: [registerTransitionOnMessage]
 *  - register a fallback transition: [registerFallbackTransition]
 *
 *  Also allows recording agent interactions: [recordInteractions], [stopRecordingInteractions]
 */
class GlobalStateMachineUsageRecorder : GlobalStateMachineUsage {
    override val usageByStateMachine: Map<String, StateMachineUsageRecorder> get() = usage
    private val usage = mutableMapOf<String, StateMachineUsageRecorder>()

    val interactions: AgentInteractions get() = interactionRecorder
    private var interactionRecorder: AgentInteractions = NullInteractionRecorder

    /** Creates new interaction recorder and returns previous interactions. */
    fun recordInteractions(): AgentInteractions = interactionRecorder.also {
        interactionRecorder = SimpleInteractionRecorder()
    }

    /** Removes current interaction recorder and returns previous interactions. */
    fun stopRecordingInteractions(): AgentInteractions = interactionRecorder.also {
        interactionRecorder = NullInteractionRecorder
    }

    fun registerStateMachine(stateMachine: StateMachine, initialState: State): Unit = synchronized(this) {
        usage.getOrPut(stateMachine.name) {
            StateMachineUsageRecorder(stateMachine.name, initialState.name)
        }.registerNewInstance()
    }

    fun registerEnter(stateMachine: StateMachine, enteredState: State, response: Events) {
        getStateMachineUsage(stateMachine).registerStateEnter(enteredState.name)
        register(stateMachine, enteredState, null, null, response, null)

        interactionRecorder.registerEnter(enteredState, response)
    }

    @Suppress("LongParameterList")
    fun registerTransitionOnMessage(
        stateMachine: StateMachine,
        currentState: State,
        message: Message,
        nextState: State?,
        timeSinceEnter: Duration,
        response: Events,
    ) {
        val transitionTarget = (nextState ?: currentState).name
        val stayInState = nextState == null
        val transitionKey = TransitionKey(currentState.name, transitionTarget, message::class.simpleName!!, stayInState)

        val transitionTimeSinceEnter = nextState?.let { timeSinceEnter }
        register(
            stateMachine,
            currentState,
            transitionKey,
            transitionTimeSinceEnter,
            response,
            message,
        )

        interactionRecorder.registerTransition(currentState, nextState, response)
    }

    fun registerFallbackTransition(
        stateMachine: StateMachine,
        currentState: State,
        nextState: State?,
        timeSinceEnter: Duration,
        response: Events,
    ) {
        if (response.isEmpty() && nextState == null) {
            return
        }

        val transitionTarget = (nextState ?: currentState).name
        val stayInState = nextState == null
        val transitionKey = TransitionKey(currentState.name, transitionTarget, null, stayInState)

        val transitionTimeSinceEnter = nextState?.let { timeSinceEnter }
        register(
            stateMachine,
            currentState,
            transitionKey,
            transitionTimeSinceEnter,
            response,
            null,
        )

        interactionRecorder.registerTransition(currentState, nextState, response)
    }

    @Suppress("LongParameterList")
    private fun register(
        stateMachine: StateMachine,
        currentState: State,
        transitionKey: TransitionKey?,
        timeSinceEnter: Duration?,
        response: Events,
        trigger: Message?,
    ) {
        val triggerName = trigger?.let { it::class.simpleName } ?: "Enter"

        getStateMachineUsage(stateMachine).apply {
            val responseMessages = response.map { it.content::class.simpleName!! }
            requireStateUsage(currentState.name).registerResponses(triggerName, responseMessages)

            transitionKey?.let {
                getOrCreateTransitionUsage(it)
            }?.register(timeSinceEnter ?: 0.seconds)
        }
    }

    private fun getStateMachineUsage(stateMachine: StateMachine): StateMachineUsageRecorder = requireNotNull(
        usage[stateMachine.name],
    ) {
        "State machine ${stateMachine.name} should already be registered! \n" +
            "only found: ${usage.keys}"
    }
}

/**
 * An implementation of [StateMachineUsage] for recording the usage of the state given by [name].
 */
class StateMachineUsageRecorder(override val name: String, override val initialState: String) : StateMachineUsage {
    override val instanceCount get() = count
    private var count: Int = 0

    override val stateUsages: Map<String, StateUsageRecorder> get() = states
    private val states = mutableMapOf<String, StateUsageRecorder>()

    override val transitionUsages: Map<TransitionKey, TransitionUsageRecorder> get() = transitions
    private val transitions = mutableMapOf<TransitionKey, TransitionUsageRecorder>()

    fun registerNewInstance() = synchronized(this) {
        count++
    }

    fun getOrCreateTransitionUsage(transitionKey: TransitionKey) = synchronized(this) {
        transitions.getOrPut(transitionKey) {
            TransitionUsageRecorder(transitionKey)
        }
    }

    fun registerStateEnter(name: String) = synchronized(this) {
        states.putIfAbsent(
            name,
            StateUsageRecorder(name),
        )
    }

    fun requireStateUsage(name: String) = requireNotNull(stateUsages[name]) {
        "State $name should already be registered!"
    }

    init {
        states[initialState] = StateUsageRecorder(initialState)
    }
}

class StateUsageRecorder( // TODO add triggerer information later for sequence diagram plots
    override val name: String,
) : StateUsage {
    override val messagesByTrigger: Map<String, SendMessageUsageRecorder> get() = messages
    private val messages = mutableMapOf<String, SendMessageUsageRecorder>()

    fun registerResponse(trigger: String, message: String) {
        getOrCreateMessageRecorder(trigger).register(message)
    }

    fun registerResponses(trigger: String, responses: Collection<String>) {
        getOrCreateMessageRecorder(trigger).apply {
            responses.forEach { register(it) }
        }
    }

    private fun getOrCreateMessageRecorder(trigger: String): SendMessageUsageRecorder = synchronized(this) {
        messages.getOrPut(trigger) {
            SendMessageUsageRecorder(trigger)
        }
    }
}

class SendMessageUsageRecorder(override val trigger: String) : SendMessageUsage {
    override val messageCount: Map<String, Int> get() = count
    private val count = mutableMapOf<String, Int>()

    fun register(message: String) = synchronized(this) {
        count[message] = count[message]?.let { it + 1 } ?: 1
    }
}

class TransitionUsageRecorder(override val key: TransitionKey) : TransitionUsage {
    override val count get() = occurrences
    private var occurrences = 0

    override val avgTimeSinceEnter get() = avgTime
    private var avgTime: Duration = 0.seconds

    fun register(timeSinceEnter: Duration) = synchronized(this) {
        avgTime = ((avgTimeSinceEnter * occurrences) + timeSinceEnter) / (occurrences + 1)
        occurrences++
    }

    override fun toString() = "$key: $count | $avgTimeSinceEnter"
}
