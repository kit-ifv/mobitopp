package application.steps.results

import core.modelsteps.ModelStep
import core.modelsteps.Repository
import core.modelsteps.SameValidationBehavior
import core.modelsteps.Warning
import core.modelsteps.validateFileWriteAccess
import domain.shared.datastructure.schedule.Activity
import domain.shared.datastructure.schedule.LinkedLeg
import domain.simulation.agent.PersonAgent
import domain.simulation.config.DemandSimContext
import domain.synthesis.data.PersonId
import domain.synthesis.results.toCSV
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.writeText

fun WriteTripsCsvContext.writeTripsToCsv(
    file: Path = Path("results/demandsimulation.csv")
) = runStep {
    WriteTripsToCsvStep(file, this)
}

interface WriteTripsCsvContext : DemandSimContext {
    val personAgents: Repository<PersonAgent, PersonId>
}

class WriteTripsToCsvStep(
    private val path: Path,
    private val context: WriteTripsCsvContext,
) : ModelStep, SameValidationBehavior {

    override val name: String = "Write trip output to csv"

    val header = "id;duration;mode;activityType;tripStart;tripEnd;ZoneStart;ZoneEnd;previousActivityType\n"

    override fun execute() {
        path.bufferedWriter().use { writer ->
            writer.appendLine(header)
            context.personAgents.elements
                .filter { it.schedule.pastLegs().isNotEmpty() }
                .forEach { person ->
                    val legs = person.schedule.pastLegs() as List<LinkedLeg>
                    legs.forEach { leg ->
                        writer.appendLine(stringifyLeg(leg, person))
                    }
                }
        }
        println("Demand Simulation written to $path")
//        val result =
//            context.personAgents.elements.filter { it.schedule.pastLegs().isNotEmpty() }.map { person ->
//                val legs = person.schedule.pastLegs()
//                val e = legs as List<LinkedLeg>
//                stringifyLegs(e, person)
//            }
//
//        val text = result.joinToString("\n", prefix = header)
//
//        path.writeText(text)
//        println("Demand Simulation written to $path")
    }
    private fun stringifyLeg(leg: LinkedLeg, person: PersonAgent): String {
        val previous = leg.previous
        val next = leg.next
        val output = if (next is Activity) next.type.toString() else "-"
        val previousOutput = if (previous is Activity) previous.type.toString() else "-"
        return toCSV(
            person.id,
            leg.duration,
            leg.transportType,
            output,
            leg.startTime,
            leg.endTime,
            leg.startLocation.requireZone().id,
            leg.endLocation.requireZone().id,
            previousOutput,
        )
    }
    private fun stringifyLegs(e: List<LinkedLeg>, person: PersonAgent) =
        e.joinToString("\n") { leg ->
            val previous = leg.previous
            val next = leg.next
            val output = if (next is Activity) {
                next.type.toString()
            } else {
                "-"
            }
            val previousOutput = if (previous is Activity) {
                previous.type.toString()
            } else {
                "-"
            }
            toCSV(
                person.id,
                leg.duration,
                leg.transportType,
                output,
                leg.startTime,
                leg.endTime,
                (leg.startLocation).requireZone().id,
                (leg.endLocation).requireZone().id,
                previousOutput,
            )
        }

    override fun verifyInput(): Warning? =
        validateFileWriteAccess(path, fileDescription = "result csv for simulated trips")
}
