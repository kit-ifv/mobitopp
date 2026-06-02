package application.steps.results

import application.steps.HasImpedance
import application.steps.HasPersonAgentRepo
import application.steps.ResultsConfig
import core.modelsteps.Check
import core.modelsteps.Context
import core.modelsteps.steps.repositoryDependentStep
import core.modelsteps.validation.validateFileWriteAccess
import domain.shared.datastructure.schedule.Activity
import domain.shared.datastructure.schedule.LinkedLeg
import domain.shared.location.Impedance
import domain.simulation.agent.PersonAgent
import domain.simulation.behavior.euros
import domain.simulation.behavior.kilometers
import domain.synthesis.results.toCSV
import java.nio.file.Path
import kotlin.io.path.bufferedWriter

context(config: ResultsConfig)
fun <C> C.writeTrips(
    file: Path = config.resultDir.resolve("demandsimulation.csv"),
    legWriter: C.() -> WriteLegToCSV = { StandardCSVLegWriter(impedance) },
) where C : HasPersonAgentRepo<*, PersonAgent>, C : HasImpedance = repositoryDependentStep(
    name = "write trips to csv: $file",
    validation = listOf(createWriteValidationCheck(file)),
    dependentRepositories = setOf(personAgentRepository),
) {
    val csvWriter = legWriter()
    var count = 0

    file.bufferedWriter().use { writer ->
        writer.appendLine(csvWriter.header)
        personAgentRepository.elements
            .filter { it.schedule.pastLegs().isNotEmpty() }
            .forEach { person ->
                val legs = person.schedule.pastLegs() as List<LinkedLeg>
                legs.forEachIndexed { index, leg ->
                    writer.appendLine(csvWriter.generateCSVLine(index, leg, person))
                    count++
                }
            }
    }

    logNormal("Wrote $count legs to $file")
}

fun <C : Context> C.createWriteValidationCheck(file: Path): Check<C> = {
    validateFileWriteAccess(file, fileDescription = "result csv for simulated trips")
}

// todo move to domain package: like output or writers
interface WriteLegToCSV {
    val header: String
    fun generateCSVLine(index: Int, leg: LinkedLeg, person: PersonAgent): String
}

class StandardCSVLegWriter(private val impedance: Impedance) : WriteLegToCSV {
    override val header: String =
        "legId;personId;duration_sec;mode;activityType;tripStart_sec;tripEnd_sec;" +
            "ZoneStart;ZoneEnd;previousActivityType;distance_km;cost_euro"

    override fun generateCSVLine(index: Int, leg: LinkedLeg, person: PersonAgent): String {
        val previous = leg.previous
        val next = leg.next
        val purpose = if (next is Activity) next.type.code.toString() else "-"
        val previousPurpose = if (previous is Activity) previous.type.code.toString() else "-"

        val dist = leg.run {
            impedance.distance(startLocation, endLocation, transportType)
        }

        val cost = leg.run {
            impedance.cost(startLocation, endLocation, transportType, startTime)
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

// class WriteTripsToCsvStep(
//    private val path: Path,
//    private val context: WriteTripsCsvContext,
//    private val legWriter: WriteLegToCSV = StandardCSVLegWriter,
// ) : ModelStep, SameValidationBehavior {
//
//    override val name: String = "Write trip output to csv"
//
//
//    override fun execute() {
//        path.bufferedWriter().use { writer ->
//            writer.appendLine(legWriter.header)
//            context.personAgents.elements
//                .filter { it.schedule.pastLegs().isNotEmpty() }
//                .forEach { person ->
//                    val legs = person.schedule.pastLegs() as List<LinkedLeg>
//                    legs.forEachIndexed { index, leg ->
//                        writer.appendLine(legWriter.generateCSVLine(index, leg, person, context))
//                    }
//                }
//        }
//        println("Demand Simulation written to $path")
//    }
//
//    override fun verifyInput(): Warning? =
//        validateFileWriteAccess(path, fileDescription = "result csv for simulated trips")
// }
