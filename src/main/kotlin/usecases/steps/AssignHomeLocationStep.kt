package usecases.steps

import domain.data.Person
import domain.data.PersonId
import domain.enums.LegacyActivityType
import modeling.steps.ModelExecution
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.UpdateStep
import modeling.validation.Warning

fun <S, C> S.assignHomeLocations() where S : ModelExecution<C>, C : LoadPersonsContext {
    this.addStep(
        AssignHomeLocationStep(context)
    )
}

class AssignHomeLocationStep(
    context: LoadPersonsContext,
) : UpdateStep<Person, PersonId>() {
    override val name: String = "Assign home location in schedule"
    override val repository: MutableRepository<Person, PersonId> = context.personRepository

    override val dependentRepositories: Set<Repository<*, *>> = setOf(
        context.householdRepository,
    )

    override fun update(element: Person) {
        element.schedule.activities().filter {
                act ->
            act.type == LegacyActivityType.HOME
        }.forEach {
                home ->
            home.location = element.household.location
        }
    }

    override fun verifyInput(): Warning? = null // TODO what can be verified here?
}
