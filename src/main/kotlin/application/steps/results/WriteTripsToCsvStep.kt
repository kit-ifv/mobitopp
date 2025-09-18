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

fun WriteTripsCsvContext.writeTripsToCsv(
    file: Path = Path("results/demandsimulation.csv"),
    legCSVWriter: WriteLegToCSV = StandardCSVLegWriter,
) = runStep {
    WriteTripsToCsvStep(file, this, legWriter = legCSVWriter)
}

interface WriteTripsCsvContext : DemandSimContext {
    val personAgents: Repository<PersonAgent, PersonId>
}

interface WriteLegToCSV {
    val header: String
    fun generateCSVLine(leg: LinkedLeg, person: PersonAgent) : String
}

object StandardCSVLegWriter: WriteLegToCSV {
    override val header: String = "id;duration;mode;activityType;tripStart;tripEnd;ZoneStart;ZoneEnd;previousActivityType"

    override fun generateCSVLine(
        leg: LinkedLeg,
        person: PersonAgent,
    ) : String{
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
}

class WriteTripsToCsvStep(
    private val path: Path,
    private val context: WriteTripsCsvContext,
    private val legWriter: WriteLegToCSV = StandardCSVLegWriter,
) : ModelStep, SameValidationBehavior {

    override val name: String = "Write trip output to csv"


    override fun execute() {
        path.bufferedWriter().use { writer ->
            writer.appendLine(legWriter.header)
            context.personAgents.elements
                .filter { it.schedule.pastLegs().isNotEmpty() }
                .forEach { person ->
                    val legs = person.schedule.pastLegs() as List<LinkedLeg>
                    legs.forEach { leg ->
                        writer.appendLine(legWriter.generateCSVLine(leg, person))
                    }
                }
        }
        println("Demand Simulation written to $path")
    }

    override fun verifyInput(): Warning? =
        validateFileWriteAccess(path, fileDescription = "result csv for simulated trips")
}
