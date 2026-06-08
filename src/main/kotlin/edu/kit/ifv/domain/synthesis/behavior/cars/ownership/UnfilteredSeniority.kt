package edu.kit.ifv.domain.synthesis.behavior.cars.ownership
import edu.kit.ifv.domain.shared.car.Car
import edu.kit.ifv.domain.synthesis.SynthesisHousehold
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.cars.SynthesisCar

class UnfilteredSeniority<S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> : AssignMainUser<S, T> {
    override fun assign(household: SynthesisHousehold<S, T>, cars: List<Car>): List<SynthesisCar> {
        val members = household.sortedByDescending { it.age }
        val output = members.zip(cars) { member, car ->
            SynthesisCar(car, member)
        }
        return output
    }
}
