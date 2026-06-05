package edu.kit.ifv.domain.synthesis.behavior.cars.generation
import edu.kit.ifv.domain.shared.car.Car
import edu.kit.ifv.domain.synthesis.attributes.household.HasNumberOfCars
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.household.adults
import edu.kit.ifv.domain.synthesis.attributes.household.licenceHolders
import edu.kit.ifv.domain.synthesis.attributes.household.numberOfDrivingLicences
import edu.kit.ifv.domain.synthesis.attributes.person.MaximumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.MinimalistHousehold
import edu.kit.ifv.domain.synthesis.behavior.MinimalistPerson
import edu.kit.ifv.domain.synthesis.behavior.cars.CarImpl
import edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels.CarSegmentChoice
import edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels.EngineAlternative
import edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels.carEngineChoiceModel
import edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels.carSegmentChoiceModel
import edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels.parameters.CarSegmentParameters
import edu.kit.ifv.domain.synthesis.behavior.cars.choicemodels.parameters.EngineParameters
import edu.kit.ifv.utils.collections.selectExact
import kotlin.random.Random

/**
 * Sampling car generation pulls a sample of potential drivers from the household based on the number of licences.
 */

class SamplingCarGeneration<S>(
    segmentParameters: CarSegmentParameters = CarSegmentParameters(),
    engineParameters: EngineParameters = EngineParameters(),
    private val randomGenerator: (MinimalistPerson<MaximumPersonAttributes>) -> Random = {
        Random(it.attributes.hashCode())
    },
) : GenerateCars<S, MaximumPersonAttributes>
    where S : MinimumHouseholdAttributes, S : HasNumberOfCars {

    private val segmentModel = carSegmentChoiceModel.build(segmentParameters)
    private val engineModel = carEngineChoiceModel.build(engineParameters)

    override fun generate(householdBuilder: MinimalistHousehold<S, MaximumPersonAttributes>): List<Car> {
        // If no licence is found all adults are considered as potential owners for the generation purposes
        val potentialCarUsers = householdBuilder.run {
            if (numberOfDrivingLicences == 0) adults else licenceHolders
        }
        val generationTargets = potentialCarUsers.selectExact(householdBuilder.attributes.amountOfCars)
        return generationTargets.map { person ->

            val random = randomGenerator(person)
            // random comes from.
            val segment = context(CarSegmentChoice.create(person, householdBuilder), random) {
                segmentModel.select()
            }
            val engineType = context(
                EngineAlternative.fromHousehold(person.attributes, householdBuilder),
                random,
            ) {
                engineModel.select()
            }
            CarImpl.fromEngineType(
                engineType = engineType,
                segment = segment,
            )
        }
    }
}
