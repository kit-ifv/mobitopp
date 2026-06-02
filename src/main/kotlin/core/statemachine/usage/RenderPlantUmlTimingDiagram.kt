@file:Suppress("TooManyFunctions")

package core.statemachine.usage

import core.statemachine.Agent
import utils.units.AbsoluteTime
import utils.units.MINUTES_PER_HOUR
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

fun AgentInteractions.renderAsPumlTimingDiagram(
    agent: Agent<*>,
    file: Path = Path(
        "docs/timing/${agent::class.simpleName ?: agent.instanceName}.puml",
    ),
    maxDepth: Int = 1,
) {
    file.parent.createDirectories()
    file.toFile().writeText(
        getTransitiveRelatedActionsOf(agent, maxDepth).toPlantUml(),
    )
}

fun List<Action>.getAgents() = map {
    when (it) {
        is Action.SendMessage -> listOf(it.from, it.to)
        is Action.ChangeState -> listOf(it.instance)
    }
}.flatten().distinct()

private const val BLUE = "blue"

private fun List<Action>.toPlantUml(): String {
    val agents = this.getAgents()
    val colors = assignColors(agents)
    return """
        |@startuml
        |skinparam dpi 400
        |
        |${agents.joinToString("\n") { it.agentToPlantUml() }}
        |
        |${this.mapNotNull { it as? Action.ChangeState }.groupBy { it.time.minutesSinceStart }.values.joinToString(
        "\n",
    ) { it.toTimeBlock(colors) }}
        |
        |${this.mapNotNull { it as? Action.SendMessage }.joinToString(
        "\n",
    ) { it.toPlantUml(colors[it.instance] ?: BLUE) } }
        |
        |@enduml
    """.trimMargin()
}

private val colors = listOf(
    "#00876C", // green
    "#4664AA", // blue
    "#ffe600", // yellow
    "#dc0d15", // red
    "#77A200", // may green
    "#A3107C", // purple
    "#f59c00", // orange
    "#079EDE", // cyan
    "#A7822E", // brown
)

fun assignColors(agents: List<String>) = agents.mapIndexed { index, instance ->
    instance to colors[index % colors.size]
}.toMap()

private fun String.agentToPlantUml() = "concise \"$this\" as ${this.agentAlias()}"

private fun List<Action.ChangeState>.toTimeBlock(colors: Map<String, String>) = """
    |${getTime().takeIf { it != AbsoluteTime.START }?.asTimeString() ?: ""}
    |${this.groupBy { it.instance }.entries.joinToString("\n") { it.toPair().toPlantUml(colors[it.key] ?: BLUE) }}
    |
""".trimMargin()

private fun Pair<String, List<Action.ChangeState>>.toPlantUml(color: String) =
    "${first.agentAlias()} is \"${second.joinToString(
        "\\n",
    ) { it.newState }}\" $color"

private fun List<Action>.getTime() = this.getOrNull(0)?.time ?: error("No time for empty action list")

private fun Action.SendMessage.toPlantUml(color: String) = if (from == to) {
    "${from.agentAlias()}${sendTime.asTimeString()} <-[$color]-> ${receiveTime.asTimeString()}"
} else {
    "${from.agentAlias()}${sendTime.asTimeString()} -[$color]> ${to.agentAlias()}${receiveTime.asTimeString()}"
} + (takeIf { message.isNotEmpty() }?.let { ": <color:$color> $message" } ?: "")

private fun String.agentAlias() = this.lowercase()
    .replace(Regex("[^a-z0-9]+"), "_") // Replace non-alphanumeric with _
    .trim('_') // Trim leading/trailing underscores

// private fun AbsoluteTime.asTimeHeader() = "@${asTimeString()} as ${asPlantUmlAlias()}"
//
// private fun AbsoluteTime.asPlantUmlAlias() = sinceStart.let {
//    ":%d_%02d_%02d".format(it.inWholeHours, it.inWholeMinutes % MINUTES_PER_HOUR, it.inWholeSeconds % SECONDS_PER_MINUTE)
// }

private fun AbsoluteTime.asTimeString() = sinceStart.let {
    "@%d:%02d:00".format(
        it.inWholeHours,
        it.inWholeMinutes % MINUTES_PER_HOUR,
    ) // , it.inWholeSeconds % SECONDS_PER_MINUTE)
}
