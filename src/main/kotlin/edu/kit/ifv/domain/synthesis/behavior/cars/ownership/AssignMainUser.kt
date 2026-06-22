package edu.kit.ifv.domain.synthesis.behavior.cars.ownership
import edu.kit.ifv.domain.shared.car.Car
import edu.kit.ifv.domain.synthesis.SynthesisHousehold
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.cars.SynthesisCar

/**
 * Assigns the main user, operates on the synthesis household rather than the survey household because the car requires
 * a synthesis person rather than a survey person, and this requirement is only present because the car writer requires
 * to write a personId and a household Id. It would be prudent to refactor the output of Cars from the synthesis to
 * skip the household id, which is the entire reason this interface restriction is placed. Maybe the Household Id could
 * be extracted from the context instead.
 */
fun interface AssignMainUser<S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> {
    fun assign(household: SynthesisHousehold<S, T>, cars: List<Car>): List<SynthesisCar>
}
