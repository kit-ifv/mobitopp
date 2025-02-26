package usecases.steps

import datastructure.plans.SingularDispatcher
import domain.data.ActivityId
import domain.data.MutablePerson
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.data.toSchedule
import modeling.steps.Context
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.UpdateStep
import modeling.steps.validateNotSealed
import modeling.validation.Warning
import utils.random.getGaussian
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes

fun AssignPlannedActivitiesContext.assignPlannedActivities() = runStep {
    AssignPlannedActivities(this)
}

interface AssignPlannedActivitiesContext : Context {
    val personRepository: MutableRepository<MutablePerson, PersonId>
    val plannedActivityRepository: MutableRepository<PlannedActivity, ActivityId>
}

class AssignPlannedActivities(
    activityRepositoryContext: AssignPlannedActivitiesContext,
) : UpdateStep<MutablePerson, PersonId>() {

    override val name: String = "Assign planned activities to person in bulk."

    override val repository: MutableRepository<MutablePerson, PersonId> = activityRepositoryContext.personRepository
    private val activityRepository = activityRepositoryContext.plannedActivityRepository
    override val dependentRepositories: Set<Repository<*, *>> = setOf(activityRepository)

    override fun verifyInput(): Warning? = validateNotSealed(activityRepository, this)

    private val activitiesPerPerson: MutableMap<PersonId, List<PlannedActivity>> by lazy {
        activityRepository.elements.groupBy { it.person.id }.toMutableMap()
    }

    override fun update(element: MutablePerson) {
        element.schedule = requireNotNull(activitiesPerPerson[element.id]) {
            "No activities found for Person ${element.id}" // TODO error handling here
        }.toSchedule(SingularDispatcher())

        val schedule = element.schedule
        val rand = element.random
        for (i in 0 until schedule.activities().size) {
            val act = schedule.activities().toList()[i]
            val durMin = act.duration.inWholeMinutes
            val deviation = (rand.getGaussian(0.0, 1.0) * durMin / 20.0).roundToInt()
            act.duration = min(max(1.0, (durMin + deviation).toDouble()), 10080.0).minutes //TODO legacy shift algorithm is fixed to one week?!
        }
    }

    override fun execute() = super.execute().also {
        activityRepository.clear()
        activitiesPerPerson.clear()
        System.gc()
    }
}
