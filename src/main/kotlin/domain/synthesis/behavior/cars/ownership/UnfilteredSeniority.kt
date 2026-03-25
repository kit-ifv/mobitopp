package domain.synthesis.behavior.cars.ownership

import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.cars.SynthesisCar
import domain.synthesis.data.Car

class UnfilteredSeniority<S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> : AssignMainUser<S, T> {
    override fun assign(
        household: SynthesisHousehold<S, T>,
        cars: List<Car>,
    ): List<SynthesisCar> {
        val members = household.sortedByDescending { it.age }
        val output = members.zip(cars) { member, car ->
            SynthesisCar(car, member)
        }
        return output
    }
}
