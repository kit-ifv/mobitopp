package usecases.steps

import domain.data.Household
import domain.data.HouseholdId
import domain.data.Person
import domain.data.PrivateCarBuilder
import modeling.steps.ModelExecution
import modeling.steps.UpdateStep

private typealias Persons = MutableSet<Person>
private typealias UnAssignedPersons = Pair<Persons, Persons>

@Suppress("LongParameterList")
fun <S, C> S.assignCarUsers() where S : ModelExecution<C>, C : BasePrivateCarContext {
    val hhMembers: MutableMap<HouseholdId, UnAssignedPersons> = mutableMapOf()

    this.addStep(
        UpdateStep(
            name = "assign private car main user",
            repository = context.carRepository,
            transformation = { assign(it, hhMembers) }
        )
    )
}

private fun assign(car: PrivateCarBuilder, hhMembers: MutableMap<HouseholdId, UnAssignedPersons>): PrivateCarBuilder {
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
