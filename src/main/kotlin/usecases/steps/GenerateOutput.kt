package usecases.steps

import datastructure.Activity
import datastructure.LinkedLeg
import domain.data.Person
import domain.data.PersonId
import modeling.steps.ModelExecution
import modeling.steps.ModelStep
import modeling.steps.Repository
import modeling.steps.SameValidationBehavior
import modeling.validation.Warning
import modeling.validation.validateFileWriteAccess
import java.io.File
import kotlin.io.path.Path

fun <S, C> S.output(
    file: File = Path(
        "results/demandsimulation.csv"
    ).toFile()
) where S : ModelExecution<C>, C : WriteTripsCsvContext {
    addStep(
        WriteTripsToCsvStep(file, context)
    )
}

interface WriteTripsCsvContext {
    val personRepository: Repository<Person, PersonId>
}

private class WriteTripsToCsvStep(
    private val file: File,
    private val context: WriteTripsCsvContext,
) : ModelStep, SameValidationBehavior {

    override val name: String = "Write trip output to csv"

    val header = "id;duration;mode;activityType;tripStart;tripEnd;ZoneStart;ZoneEnd;previousActivityType\n"

    override fun execute() {
        val result =
            context.personRepository.elements.filter { it.schedule.pastLegs().isNotEmpty() }.map { person ->
                val legs = person.schedule.pastLegs()
                val e = legs as List<LinkedLeg>
                stringifyLegs(e, person)
            }

        val text = result.joinToString("\n", prefix = header)

        file.writeText(text)
        println("Demand Simulation written to $file")
    }

    private fun stringifyLegs(e: List<LinkedLeg>, person: Person) =
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
        validateFileWriteAccess(file, fileDescription = "result csv for simulated trips")
}

fun toCSV(vararg elements: Any): String {
    return elements.joinToString(";") { it.toString() }
}
