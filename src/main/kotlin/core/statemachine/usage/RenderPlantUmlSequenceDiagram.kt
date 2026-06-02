package core.statemachine.usage

import core.statemachine.Agent
import utils.units.AbsoluteTime
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

fun AgentInteractions.renderAsPumlSequenceDiagram(
    agent: Agent<*>,
    file: Path = Path(
        "results/sequence/${agent::class.simpleName ?: agent.instanceName}.puml",
    ),
    maxDepth: Int = 1,
) {
    file.parent.createDirectories()
    file.toFile().writeText(
        getTransitiveRelatedActionsOf(agent, maxDepth).toPlantUml(),
    )
}

private fun List<Action>.toPlantUml(): String {
    val agents = this.getAgents()
    val activationState = agents.associateWith { false }.toMutableMap()
    return """
        |@startuml
        |
        |${agents.joinToString("\n") { it.agentToPlantUml() }}
        |
        |${this.toSeqEvents().sortedWith(
        compareBy(SeqEvent::time, SeqEvent::index),
    ).joinToString("\n") { it.toPlantUml(activationState) }}
        |
        |@enduml
    """.trimMargin()
}

private fun String.agentToPlantUml() = "participant \"$this\" as ${this.agentAlias()}"

private sealed interface SeqEvent {
//    fun toPlantUml(): String
    val time: AbsoluteTime
    val index: Int

    data class ChangeState(
        override val time: AbsoluteTime,
        override val index: Int,
        val instance: String,
        val newState: String,
    ) : SeqEvent

    data class SendNow(
        override val time: AbsoluteTime,
        override val index: Int,
        val from: String,
        val to: String,
        val message: String,
    ) : SeqEvent

    data class SendLater(
        override val time: AbsoluteTime,
        override val index: Int,
        val from: String,
        val to: String,
        val message: String,
        val reference: Int,
    ) : SeqEvent

    data class ReceiveLater(
        override val time: AbsoluteTime,
        override val index: Int,
        val from: String,
        val to: String,
        val message: String,
        val reference: Int,
    ) : SeqEvent
}

private fun List<Action>.toSeqEvents(counter: Counter = Counter()) = flatMap {
    when (it) {
        is Action.ChangeState -> listOf(SeqEvent.ChangeState(it.time, it.index, it.instance, it.newState))
        is Action.SendMessage -> it.toArrows(counter)
    }
}

private fun Action.SendMessage.toArrows(counter: Counter) = if (sendTime == receiveTime) {
    listOf(SeqEvent.SendNow(sendTime, index, from, to, message))
} else {
    val reference = counter.value
    listOf(
        SeqEvent.SendLater(sendTime, index, from, to, message, reference),
        SeqEvent.ReceiveLater(receiveTime, index, from, to, message, reference),
    )
}

private fun SeqEvent.toPlantUml(activationState: MutableMap<String, Boolean>) = when (this) {
    is SeqEvent.ChangeState -> toStateChangeCode(activationState)
    is SeqEvent.SendNow -> "${from.agentAlias()} -> ${to.agentAlias()}: send $message"
    is SeqEvent.SendLater -> "${from.agentAlias()} --> ${to.agentAlias()}: later [$reference]\\n$message"
    is SeqEvent.ReceiveLater -> "${to.agentAlias()} --> ${to.agentAlias()}: recv [$reference]\\n$message"
}

private fun SeqEvent.ChangeState.toStateChangeCode(activationState: MutableMap<String, Boolean>) = (
    takeIf { activationState.getOrPut(instance) { false } }?.let {
        "deactivate ${instance.agentAlias()}\n"
    } ?: ""
    ) + "${instance.agentAlias()} -> ${instance.agentAlias()} ++: **$newState**".also {
    activationState[instance] = true
}

private fun String.agentAlias() = this.lowercase()
    .replace(Regex("[^a-z0-9]+"), "_") // Replace non-alphanumeric with _
    .trim('_') // Trim leading/trailing underscores

private class Counter {
    private var i = 0
    val value get() = i++
}
