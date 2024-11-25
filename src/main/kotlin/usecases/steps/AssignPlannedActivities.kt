package usecases.steps

import datastructure.plans.SingularDispatcher
import domain.data.ActivityId
import domain.data.Person
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.data.toSchedule
import modeling.steps.ModelExecution
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.UpdateStep
import modeling.steps.validateNotSealed
import modeling.validation.Warning

fun <S, C> S.assignPlannedActivities() where S : ModelExecution<C>, C : AssignPlannedActivitiesContext {
    this.addStep(AssignPlannedActivities(context))
}

interface AssignPlannedActivitiesContext {
    val personRepository: MutableRepository<Person, PersonId>
    val plannedActivityRepository: MutableRepository<PlannedActivity, ActivityId>
}

class AssignPlannedActivities(
    activityRepositoryContext: AssignPlannedActivitiesContext,
) : UpdateStep<Person, PersonId>() {

    override val name: String = "Assign planned activities to person in bulk."

    override val repository: MutableRepository<Person, PersonId> = activityRepositoryContext.personRepository
    private val activityRepository = activityRepositoryContext.plannedActivityRepository
    override val dependentRepositories: Set<Repository<*, *>> = setOf(activityRepository)

    override fun verifyInput(): Warning? = validateNotSealed(activityRepository, this)

    private val activitiesPerPerson: MutableMap<PersonId, List<PlannedActivity>> by lazy {
        activityRepository.elements.groupBy { it.person.id }.toMutableMap()
    }

    override fun update(element: Person) {
        element.schedule = requireNotNull(activitiesPerPerson[element.id]) {
            "No activities found for Person ${element.id}" // TODO error handling here
        }.toSchedule(SingularDispatcher())
    }

    override fun execute() = super.execute().also {
        activityRepository.clear()
        activitiesPerPerson.clear()
        System.gc()
    }
}
