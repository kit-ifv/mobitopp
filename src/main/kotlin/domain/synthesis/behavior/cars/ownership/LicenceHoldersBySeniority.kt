package domain.synthesis.behavior.cars.ownership

import domain.shared.car.Car
import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.HasLicence
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.attributes.person.hasLicence
import domain.synthesis.behavior.cars.SynthesisCar

class LicenceHoldersBySeniority<S : MinimumHouseholdAttributes, T> :
    AssignMainUser<S, T>
    where T : MinimumPersonAttributes, T : HasLicence {
    override fun assign(household: SynthesisHousehold<S, T>, cars: List<Car>): List<SynthesisCar> {
        val members = household.filter { it.hasLicence }.sortedByDescending { it.age }
        val output = members.zip(cars) { member, car ->
            SynthesisCar(car, member)
        }
        return output
    }
}
