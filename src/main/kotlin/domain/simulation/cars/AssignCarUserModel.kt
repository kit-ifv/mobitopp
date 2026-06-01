package domain.simulation.cars

import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutablePrivateCar
import domain.synthesis.data.Person
import domain.synthesis.data.isAdult

private typealias Persons = MutableSet<Person>
private typealias UnAssignedPersons = Pair<Persons, Persons>

class AssignCarUserModel {

    private val hhMembers: MutableMap<HouseholdId, UnAssignedPersons> = mutableMapOf()

    fun clearCache() {
        hhMembers.clear()
    }

    @Suppress("UnusedPrivateMember")
    fun assign(car: MutablePrivateCar): MutablePrivateCar {
        val owner: Household = requireNotNull(
            car.owner
        ) { "Cannot assign main user of cars, since owner has not been defined yet!" }

        if (owner.id !in hhMembers.keys) {
            // assume cars of households appear as sequence of rows in csv (TODO assert via sorted?)
            hhMembers.clear()
        }

        val (unassigned, assigned) = hhMembers[owner.id] ?: initDrivers(owner)

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
    ): UnAssignedPersons {
        val res = owner.getDrivers() to mutableSetOf<Person>()
        hhMembers[owner.id] = res
        return res
    }

    private fun Household.getDrivers(): MutableSet<Person> {
        require(
            members.isNotEmpty()
        ) { "Cannot assign main user of cars if household members have not been defined!" }

        return members.filter { it.hasLicense }.toMutableSet().ifEmpty {
            members.filter { it.isAdult }.toMutableSet()
        }
    }
}
