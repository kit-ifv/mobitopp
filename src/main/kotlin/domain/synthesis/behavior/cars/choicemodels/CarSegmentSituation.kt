package domain.synthesis.behavior.cars.choicemodels

import domain.simulation.data.car.CarSegment
import domain.synthesis.attributes.household.HasIncome
import domain.synthesis.attributes.household.HasNumberOfCars
import domain.synthesis.attributes.person.HasBiologicalSex
import domain.synthesis.attributes.person.HasCommuteDistance
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.MinimalistPerson
import kotlin.random.Random

data class CarSegmentSituation<S, T>(
    val person: MinimalistPerson<T>,
    val household: MinimalistHousehold<S, T>,
) where S : HasNumberOfCars, S : HasIncome, T : HasBiologicalSex, T : HasCommuteDistance {
    // TODO delegate to household once merged with default household dataclass
    val random: Random = Random(System.currentTimeMillis())
    fun with(choice: CarSegment) = choice.toAlternative(person, household)
}
