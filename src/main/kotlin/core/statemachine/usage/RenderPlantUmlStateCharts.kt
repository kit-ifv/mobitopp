@file:Suppress("TooManyFunctions")

package core.statemachine.usage

import utils.units.round
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.time.Duration

/**
 * Render the receiver [GlobalStateMachineUsage] as plant uml files encoding a state chart.
 * Stores the charts in the given directory [dir].
 *
 * @param dir directory where the .puml files are saved. Defaults to "/docs" inside the working directory.
 */
fun GlobalStateMachineUsage.renderAsPlantUmlFiles(dir: Path = Path("docs/state_machines")) =
    usageByStateMachine.values.forEach {
        it.apply {
            dir.createDirectories()
            val file = dir.resolve("${name.asPlantumlAlias()}.puml")

            file.toFile().writeText(
                toPlantUml()
            )
        }
    }

private fun StateMachineUsage.toPlantUml() = """
    |@startuml
    |
    |state "$name" as ${name.asPlantumlAlias()} <<frame>> {
    |${name.asPlantumlAlias()}: instances = $instanceCount
    |
    |'states
    |${stateUsages.values.joinToString("\n") {
    it.toPlantUml(instanceCount, hasSelfTransition(it.name))
}
}
    |
    |'transitions
    |[*] --> ${initialState.asPlantumlAlias()}
    |${transitionUsages.values.joinToString("\n") { it.toPlantUml(instanceCount) }}
    |}
    |
    |@enduml
""".trimMargin()

private fun StateUsage.toPlantUml(agentCount: Int, hasSelfTransition: Boolean) =
    toPlantUml(agentCount, hasSelfTransition, name.asPlantumlAlias())

private fun StateUsage.toPlantUml(agents: Int, hasSelfTransition: Boolean, stateAlias: String) = """
    |state "${name.asStateName()}" as $stateAlias
    |${messagesByTrigger.values.joinToString("\n") { it.toPlantUml(agents, stateAlias)} }
    |${if (hasSelfTransition) "${name.asPlantumlAlias()}: self transitions:" else ""}
    |
""".trimMargin()

private fun SendMessageUsage.toPlantUml(agents: Int, stateAlias: String) =
    if (messageCount.isEmpty()) {
        ""
    } else {
        "$stateAlias: msg sent on $trigger:\n" +
            messageCount.entries.joinToString("\n") { (message, count) ->
                "$stateAlias: - ${message.asMessageName()} ${count.countString(agents)}"
            }
    }

private fun TransitionUsage.toPlantUml(agents: Int) = if (stayInState) {
    "${from.asPlantumlAlias()}: - ${key.message.asMessageName()} "
} else {
    "${key.toPlantUmlArrow()}\\n${avgTimeSinceEnter.durationString()} "
} + count.countString(agents)

private fun TransitionKey.toPlantUmlArrow() =
    "${from.asPlantumlAlias()} --> ${to.asPlantumlAlias()}: ${message.asMessageName()}"

private fun String?.asMessageName() = this?.replace("Message", "", ignoreCase = true) ?: "-"

private fun String.asStateName() = this.replace("State", "", ignoreCase = true)

private fun String.asPlantumlAlias() = this.lowercase()
    .replace(Regex("[^a-z0-9]+"), "_") // Replace non-alphanumeric with _
    .trim('_') // Trim leading/trailing underscores

private fun Int.countString(agents: Int) = "#$this | ${(this.toDouble() / agents).round(2)}"

private fun Duration.durationString() = takeIf {
    this.isPositive()
}?.let {
    " (${this.toString().truncateDecimalDigits(2)})"
} ?: ""

private fun String.truncateDecimalDigits(digits: Int = 2): String {
    val regex = Regex("""\.(\d{$digits})\d*""")
    return this.replace(regex) { ".${it.groupValues[1]}" }
}
