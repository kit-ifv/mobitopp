package usecases.steps

import domain.data.CarId
import domain.data.Household
import domain.data.HouseholdId
import domain.data.MutablePrivateCar
import domain.data.Person
import domain.data.PersonId
import modeling.steps.ModelExecution
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.TransformStep
import modeling.validation.Warning

private typealias Persons = MutableSet<Person>
private typealias UnAssignedPersons = Pair<Persons, Persons>

@Suppress("LongParameterList")
fun <S, C> S.assignCarUsers() where S : ModelExecution<C>, C : AssignCarsContext {
    this.addStep(
        AssignCarUserStep(context)
    )
}

interface AssignCarsContext {

    val personRepository: Repository<Person, PersonId>
    val householdRepository: Repository<Household, HouseholdId>
    val carRepository: MutableRepository<MutablePrivateCar, CarId>
}

class AssignCarUserStep(
    context: AssignCarsContext,
) : TransformStep<MutablePrivateCar, CarId>() {

    override val name: String = "Assign cars to household members as main users."
    override val repository: MutableRepository<MutablePrivateCar, CarId> = context.carRepository
    override val dependentRepositories: Set<Repository<*, *>> =
        setOf(context.personRepository, context.householdRepository)

    private val hhMembers: MutableMap<HouseholdId, UnAssignedPersons> = mutableMapOf()

    override fun transform(element: MutablePrivateCar): MutablePrivateCar {
        return assign(element, hhMembers)
    }

    override fun verifyInput(): Warning? = null
}

@Suppress("UnusedPrivateMember")
private fun assign(car: MutablePrivateCar, hhMembers: MutableMap<HouseholdId, UnAssignedPersons>): MutablePrivateCar {
    val owner: Household = requireNotNull(
        car.owner
    ) { "Cannot assign main user of cars, since owner has not been defined yet!" }

    if (owner.id !in hhMembers.keys) {
        // assume cars of households appear as sequence of rows in csv
        hhMembers.clear()
    }

    val (unassigned, assigned) = hhMembers[owner.id] ?: initDrivers(owner, hhMembers)

    if (unassigned.isEmpty()) {
        unassigned.addAll(assigned)
        assigned.clear()
    }

    require(unassigned.isNotEmpty()) { "Cannot assign main user of car if household has no members!" }

    val user = unassigned.random(owner.random)
    car.mainUser = user

    unassigned.remove(user)
    assigned.add(user)

    return car
}

private fun initDrivers(
    owner: Household,
    hhMembers: MutableMap<HouseholdId, UnAssignedPersons>
): UnAssignedPersons {
    val res = owner.getDrivers() to mutableSetOf<Person>()
    hhMembers[owner.id] = res
    return res
}

private fun Household.getDrivers(): MutableSet<Person> {
    require(members.isNotEmpty()) { "Cannot assign main user of cars if household members have not been defined!" }

    return members.filter { it.hasLicense }.toMutableSet().ifEmpty {
        members.filter { it.isAdult }.toMutableSet()
    }
}
