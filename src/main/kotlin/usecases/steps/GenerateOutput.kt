package usecases.steps

import datastructure.Activity
import datastructure.LinkedLeg
import domain.location.ZoneLocation
import modeling.steps.ModelExecution
import modeling.steps.ModelStep
import modeling.steps.RepositoryState
import modeling.steps.repairFinishedState
import modeling.steps.subValidateState
import modeling.validation.Warning
import modeling.validation.validateFileWriteAccess
import modeling.validation.validateScope
import java.io.File
import kotlin.io.path.Path
fun <S, C> S.output(
    file: File = Path(
        "results/demandsimulation.csv"
    ).toFile()
) where S : ModelExecution<C>, C : PersonContext {
    addStep(
        WriteTripsToCsvStep(file, context)
//        CustomStep( //TODO inherit model step instead of implementing custom step -> this can be referenced in validate
//            name = "Write Output",
//            validation = {
//                validateScope(
//                    "Validate $name: write simulated trips to csv format:"
//                ) {
//                    validateFileWriteAccess(file, fileDescription = "result csv for simulated trips")
//
//                    subValidateState(context.personRepository, RepositoryState.FINISHED, this@output) //TODO
//                    repairFinishedState(context.personRepository, this@output)
//                }
//            },
//            exec = {
//                val result =
//                    context.personRepository.elements.filter { it.schedule.pastLegs().isNotEmpty() }.map { person ->
//                        val legs = person.schedule.pastLegs()
//                        val e = legs as List<LinkedLeg>
//                        e.joinToString("\n") { leg ->
//                            toCSV(
//                                person.id,
//                                leg.duration,
//                                leg.transportType,
//                                leg.startTime,
//                                leg.endTime,
//                                (leg.startLocation as ZoneLocation).zone.id,
//                                (leg.endLocation as ZoneLocation).zone.id,
//                            )
//                        }
//                    }
//                val text =
//                    result.joinToString("\n", prefix = "id;duration;mode;start;end;ZoneStart;ZoneEnd\n") { it }
//
//                file.writeText(text)
//                println("Demand Simulation written to $file")
//            }
//        )
    )
}

private class WriteTripsToCsvStep(
    private val file: File,
    private val context: PersonContext,
) : ModelStep {
    override val name: String
        get() = "Write trip output to csv"

    override fun execute() {
        val result =
            context.personRepository.elements.filter { it.schedule.pastLegs().isNotEmpty() }.map { person ->
                val legs = person.schedule.pastLegs()
                val e = legs as List<LinkedLeg>

                e.joinToString("\n") { leg ->
                    val previous = leg.previous
                    val next = leg.next
                    val output = if (next is Activity) { next.type.toString() } else { "-" }
                    val previousOutput = if (previous is Activity) { previous.type.toString() } else { "-" }
                    toCSV(
                        person.id,
                        leg.duration,
                        leg.transportType,
                        output,
                        leg.startTime,
                        leg.endTime,
                        (leg.startLocation as ZoneLocation).zone.id,
                        (leg.endLocation as ZoneLocation).zone.id,
                        previousOutput,
                    )
                }
            }
        val text =
            result.joinToString(
                "\n",
                prefix = "id;duration;mode;activityType;tripStart;tripEnd;ZoneStart;ZoneEnd;previousActivityType\n"
            ) {
                it
            }

        file.writeText(text)
        println("Demand Simulation written to $file")
    }

    override fun validate(): Warning? = validateScope(
        "Validate $name: write simulated trips to csv format:"
    ) {
        validateFileWriteAccess(file, fileDescription = "result csv for simulated trips")

        subValidateState(context.personRepository, RepositoryState.FINISHED, this@WriteTripsToCsvStep) // TODO
        repairFinishedState(context.personRepository, this@WriteTripsToCsvStep)
    }
}

fun toCSV(vararg elements: Any): String {
    return elements.joinToString(";") { it.toString() }
}
