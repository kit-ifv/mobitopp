package usecases.steps

import datastructure.LinkedLeg
import domain.location.ZoneLocation
import modeling.steps.CustomStep
import modeling.steps.ModelExecution
import modeling.steps.RepositoryState
import modeling.steps.repairFinishedState
import modeling.steps.validateState

fun <S, C> S.output() where S : ModelExecution<C>, C : PersonContext {
    addStep(
        CustomStep(
            name = "Write Output",
            validation = {
                validateState(context.personRepository, RepositoryState.FINISHED, this).also {
                    repairFinishedState(context.personRepository, this)
                }
            },
            exec = {
                val result = context.personRepository.elements.map { person ->
                    val legs = person.schedule.pastLegs()
                    val e = legs as List<LinkedLeg>
                    e.joinToString("\n") { leg ->
                        toCSV(
                            person.id,
                            leg.duration,
                            leg.transportType,
                            leg.startTime,
                            leg.endTime,
                            (leg.startLocation as ZoneLocation).zone.id,
                            (leg.endLocation as ZoneLocation).zone.id,
                        )
                    }
                }
                println("SIMULATION OUTPUT\n")
                println(result.joinToString("\n", prefix = "id;duration;mode;start;end;ZoneStart;ZoneEnd\n") { it })
                println("END\n")
            }
        )
    )
}

fun toCSV(vararg elements: Any): String {
    return elements.joinToString(";") { it.toString() }
}
