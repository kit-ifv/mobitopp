package usecases.steps

import datastructure.plans.SingularDispatcher
import domain.data.toSchedule
import modeling.steps.Context
import modeling.steps.CustomStep
import modeling.steps.ModelExecution
import modeling.steps.RepositoryState
import modeling.steps.repairFinishedState
import modeling.steps.subValidateState
import modeling.validation.validateScope

fun <S, C> S.assignPlannedActivities()
    where S : ModelExecution<C>, C : Context, C : ActivityContext, C : PersonContext {
    this.addStep(
        CustomStep(
            "assign planned activities to persons",
            validation = {
                validateScope(
                    "Validate assign HOME location to household and update schedules produced warnings:"
                ) {
                    subValidateState(
                        context.personRepository,
                        RepositoryState.FINISHED,
                        this@assignPlannedActivities
                    )
                    subValidateState(
                        context.plannedActivityRepository,
                        RepositoryState.FINISHED,
                        this@assignPlannedActivities
                    )
                    repairFinishedState(context.personRepository, this@assignPlannedActivities)
                }
            },
            exec = {
                context.plannedActivityRepository.elements.groupBy { it.person.id }.map {
                    context.personRepository.getById(it.key)!!.schedule = it.value.toSchedule(SingularDispatcher())
                }

                context.plannedActivityRepository.clear()
                System.gc()
            }
        )
    )
}
