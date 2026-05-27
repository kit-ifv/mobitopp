package application.steps.results

import core.modelsteps.ModelStep
import core.modelsteps.Repository
import core.modelsteps.SameValidationBehavior
import core.modelsteps.Warning
import core.modelsteps.validateFileWriteAccess
import domain.shared.datastructure.schedule.Activity
import domain.shared.datastructure.schedule.LinkedLeg
import domain.simulation.agent.PersonAgent
import domain.simulation.behavior.euros
import domain.simulation.behavior.kilometers
import domain.simulation.config.DemandSimContext
import domain.synthesis.data.PersonId
import domain.synthesis.results.toCSV
import java.nio.file.Path
import kotlin.io.path.bufferedWriter

fun WriteTripsCsvContext.writeTripsToCsv(
    file: Path = resultDir.resolve("demandsimulation.csv"),
    legCSVWriter: WriteLegToCSV = StandardCSVLegWriter,
) = runStep {
    WriteTripsToCsvStep(file, this, legWriter = legCSVWriter)
}

interface WriteTripsCsvContext : DemandSimContext {
    val personAgents: Repository<PersonAgent, PersonId>
}

interface WriteLegToCSV {
    val header: String
    fun generateCSVLine(index: Int, leg: LinkedLeg, person: PersonAgent, context: WriteTripsCsvContext): String
}

object StandardCSVLegWriter : WriteLegToCSV {
    override val header: String = "legId;personId;duration_sec;mode;activityType;" +
        "tripStart_sec;tripEnd_sec;ZoneStart;ZoneEnd;previousActivityType;distance_km;cost_euro"

    override fun generateCSVLine(
        index: Int,
        leg: LinkedLeg,
        person: PersonAgent,
        context: WriteTripsCsvContext,
    ): String {
        val previous = leg.previous
        val next = leg.next
        val purpose = if (next is Activity) next.type.code.toString() else "-"
        val previousPurpose = if (previous is Activity) previous.type.code.toString() else "-"

        val dist = leg.run {
            context.impedance.value.distance(startLocation, endLocation, transportType)
        }

        val cost = leg.run {
            context.impedance.value.cost(startLocation, endLocation, transportType, startTime)
        }

        return toCSV(
            index,
            person.id.value,
            leg.duration.inWholeSeconds,
            leg.transportType.code,
            purpose,
            leg.startTime.secondsSinceStart,
            leg.endTime.secondsSinceStart,
            leg.startLocation.zoneId.value,
            leg.endLocation.zoneId.value,
            previousPurpose,
            dist.kilometers,
            cost.euros,
        )
    }
}

class WriteTripsToCsvStep(
    private val path: Path,
    private val context: WriteTripsCsvContext,
    private val legWriter: WriteLegToCSV = StandardCSVLegWriter,
) : ModelStep,
    SameValidationBehavior {

    override val name: String = "Write trip output to csv"

    override fun execute() {
        path.bufferedWriter().use { writer ->
            writer.appendLine(legWriter.header)
            context.personAgents.elements
                .filter { it.schedule.pastLegs().isNotEmpty() }
                .forEach { person ->
                    val legs = person.schedule.pastLegs() as List<LinkedLeg>
                    legs.forEachIndexed { index, leg ->
                        writer.appendLine(legWriter.generateCSVLine(index, leg, person, context))
                    }
                }
        }
        println("Demand Simulation written to $path")
    }

    override fun verifyInput(): Warning? =
        validateFileWriteAccess(path, fileDescription = "result csv for simulated trips")
}
