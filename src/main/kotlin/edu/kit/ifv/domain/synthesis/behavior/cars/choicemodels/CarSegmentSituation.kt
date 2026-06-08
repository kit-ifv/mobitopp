package edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels
import edu.kit.ifv.domain.shared.car.CarSegment
import edu.kit.ifv.domain.synthesis.attributes.household.HasIncome
import edu.kit.ifv.domain.synthesis.attributes.household.HasNumberOfCars
import edu.kit.ifv.domain.synthesis.attributes.person.HasBiologicalSex
import edu.kit.ifv.domain.synthesis.attributes.person.HasCommuteDistance
import edu.kit.ifv.domain.synthesis.behavior.MinimalistHousehold
import edu.kit.ifv.domain.synthesis.behavior.MinimalistPerson
import kotlin.random.Random

data class CarSegmentSituation<S, T>(
    val person: MinimalistPerson<T>,
    val household: MinimalistHousehold<S, T>,
) where S : HasNumberOfCars, S : HasIncome, T : HasBiologicalSex, T : HasCommuteDistance {
    // TODO delegate to household once merged with default household dataclass
    val random: Random = Random(System.currentTimeMillis())
    fun with(choice: CarSegment) = choice.toAlternative(person, household)
}
