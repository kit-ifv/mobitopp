package edu.kit.ifv.core.statemachine.usage
import edu.kit.ifv.core.statemachine.Agent
import edu.kit.ifv.core.statemachine.Events
import edu.kit.ifv.core.statemachine.Message
import edu.kit.ifv.core.statemachine.State
import edu.kit.ifv.utils.units.AbsoluteTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * Interface for recording and looking up agent state changes and messages during execution of state machines.
 * Can register ChangeState actions when agents enter or transition into a certain state,
 * and SendMessage actions for events generated in response to enter or transitions.
 *
 * Allows looking up actions for a given agent, resolving actions transitively up to a certain interaction depth.
 */
interface AgentInteractions {

    fun getTransitiveRelatedActionsOf(agent: Agent<*>, maxDepth: Int = 1): List<Action>
    fun registerEnter(enteredState: State, response: Events)
    fun registerTransition(currentState: State, nextState: State?, response: Events)
}

object NullInteractionRecorder : AgentInteractions {
    override fun registerEnter(enteredState: State, response: Events) = Unit
    override fun registerTransition(currentState: State, nextState: State?, response: Events) = Unit
    override fun getTransitiveRelatedActionsOf(agent: Agent<*>, maxDepth: Int) = emptyList<Action>()
}

class SimpleInteractionRecorder : AgentInteractions {

    private val actionsByInstance = ConcurrentHashMap<String, ConcurrentLinkedDeque<Action>>()

    @Suppress("CyclomaticComplexMethod")
    override fun getTransitiveRelatedActionsOf(agent: Agent<*>, maxDepth: Int): List<Action> {
        val visited = mutableListOf<String>()
        val instance = agent.instanceName

        val actions = synchronized(this) {
            getTransitiveRelatedActionsOf(instance, level = 0, maxDepth, visited)
        }

        val instanceActions = actionsByInstance[instance]?.sortedBy { it.time } ?: emptyList()
        val start = instanceActions.firstOrNull()?.time ?: AbsoluteTime.START
        val end = instanceActions.lastOrNull()?.time

        // filter messages within the visited scope
        return actions.filter {
            when (it) {
                is Action.ChangeState ->
                    it.instance in visited &&
                        (start <= it.time && end?.let { e -> it.time <= e } ?: true)

                is Action.SendMessage ->
                    it.from in visited &&
                        it.to in visited &&
                        (start <= it.receiveTime && end?.let { e -> it.receiveTime <= e } ?: true)
            }
        }.distinct().sortedBy { it.time }
    }

    private fun getTransitiveRelatedActionsOf(
        instance: String,
        level: Int,
        maxDepth: Int,
        visited: MutableList<String>,
    ): List<Action> = if (level >
        maxDepth
    ) {
        emptyList()
    } else if (level == maxDepth) {
        visited.add(instance)
        actionsByInstance[instance].orEmpty().toList()
    } else {
        visited.add(instance)
        val (relations, actions) = getActionsAndRelationsOf(instance)
        val relatedActions = relations.filter {
            it !in visited
        }.flatMap {
            getTransitiveRelatedActionsOf(it, level + 1, maxDepth, visited)
        }

        actions + relatedActions
    }

    private fun getActionsAndRelationsOf(instance: String): Pair<List<String>, List<Action>> {
        val relations = mutableListOf<String>()
        val actions = (actionsByInstance[instance] ?: emptyList<Action>()).onEach {
            when (it) {
                is Action.SendMessage -> {
                    relations.add(it.from)
                    relations.add(it.to)
                }

                else -> Unit
            }
        }
        relations.remove(instance)
        return relations to actions.toList()
    }

    override fun registerEnter(enteredState: State, response: Events) {
        val instance = enteredState.agent.instanceName

        val action = Action.ChangeState(enteredState.time, instance, enteredState.label)
        synchronized(instance) {
            getActionListOf(instance).add(action)
        }

        registerResponseActions(response, instance)
    }

    override fun registerTransition(currentState: State, nextState: State?, response: Events) {
        val instance = currentState.agent.instanceName
        registerResponseActions(response, instance)

        nextState?.let {
            val action = Action.ChangeState(currentState.time, instance, it.label)
            requireNotNull(action)

            synchronized(instance) {
                getActionListOf(instance).add(action)
            }
        }
    }

    private fun SimpleInteractionRecorder.registerResponseActions(response: Events, instance: String) =
        response.forEach { event ->
            val sendAction = Action.SendMessage(
                instance,
                event.sendTime,
                event.receiver.instanceName,
                event.receiveTime,
                event.content.label,
            )

            requireNotNull(sendAction)

            synchronized(instance) {
                synchronized(sendAction.to) {
                    getActionListOf(instance).add(sendAction)
                    getActionListOf(sendAction.to).add(sendAction)
                }
            }
        }

    private fun getActionListOf(instance: String): ConcurrentLinkedDeque<Action> = synchronized(this) {
        synchronized(instance) {
            actionsByInstance.getOrPut(instance) {
                ConcurrentLinkedDeque<Action>()
            }
        }
    }

    private val State.label get() = this.name.replace("state", "", ignoreCase = true)

    private val Message.label get() = this::class.simpleName!!.replace("message", "", ignoreCase = true)
}

val Agent<*>.instanceName get() =
    this::class.simpleName!!.replace("agent", "", ignoreCase = true) + "_" + this.hashCode()

sealed class Action {
    val index: Int = indexCounter++
    abstract val time: AbsoluteTime
    abstract val instance: String

    data class ChangeState(override val time: AbsoluteTime, override val instance: String, val newState: String) :
        Action()

    data class SendMessage(
        val from: String,
        val sendTime: AbsoluteTime,
        val to: String,
        val receiveTime: AbsoluteTime,
        val message: String,
    ) : Action() {
        override val instance: String get() = from
        override val time: AbsoluteTime get() = sendTime
    }

    companion object {
        private var indexCounter = 0
    }
}
