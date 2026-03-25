package domain.synthesis.behavior.cars.ownership

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.cars.SynthesisCar
import domain.synthesis.SynthesisHousehold
import domain.synthesis.data.Car

fun interface AssignMainUser<S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> {
    fun assign(household: SynthesisHousehold<S, T>, cars: List<Car>): List<SynthesisCar>
}