package edu.kit.ifv.domain.synthesis.behavior.cars.ownership
import edu.kit.ifv.domain.shared.car.Car
import edu.kit.ifv.domain.synthesis.SynthesisHousehold
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.HasLicence
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.hasLicence
import edu.kit.ifv.domain.synthesis.behavior.cars.SynthesisCar

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
