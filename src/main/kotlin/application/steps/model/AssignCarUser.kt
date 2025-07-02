package application.steps.model

import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.TransformEachStep
import core.modelsteps.Warning
import domain.simulation.config.DemandSimContext
import domain.synthesis.behavior.AssignCarUserModel
import domain.synthesis.data.CarId
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutablePrivateCar
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId

@Suppress("LongParameterList")
fun AssignCarsContext.assignCarUsers() = runStep {
    AssignCarUserStep(this)
}

interface AssignCarsContext : DemandSimContext {
    val personRepository: Repository<Person, PersonId>
    val householdRepository: Repository<Household, HouseholdId>
    val carRepository: MutableRepository<MutablePrivateCar, CarId>
}

class AssignCarUserStep(
    context: AssignCarsContext,
) : TransformEachStep<MutablePrivateCar, CarId>() {

    override val name: String = "Assign cars to household members as main users."
    override val repository: MutableRepository<MutablePrivateCar, CarId> = context.carRepository
    override val dependentRepositories: Set<Repository<*, *>> =
        setOf(context.personRepository, context.householdRepository)

    private val model = AssignCarUserModel()

    override fun transform(element: MutablePrivateCar): MutablePrivateCar {
        return model.assign(element)
    }

    override fun verifyInput(): Warning? = null
}
