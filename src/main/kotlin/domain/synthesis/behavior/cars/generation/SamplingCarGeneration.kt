package domain.synthesis.behavior.cars.generation

import domain.shared.car.Car
import domain.synthesis.attributes.household.HasNumberOfCars
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.household.adults
import domain.synthesis.attributes.household.licenceHolders
import domain.synthesis.attributes.household.numberOfDrivingLicences
import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.MinimalistPerson
import domain.synthesis.behavior.cars.CarImpl
import domain.synthesis.behavior.cars.choicemodels.CarSegmentChoice
import domain.synthesis.behavior.cars.choicemodels.EngineAlternative
import domain.synthesis.behavior.cars.choicemodels.carEngineChoiceModel
import domain.synthesis.behavior.cars.choicemodels.carSegmentChoiceModel
import domain.synthesis.behavior.cars.choicemodels.parameters.CarSegmentParameters
import domain.synthesis.behavior.cars.choicemodels.parameters.EngineParameters
import utils.collections.selectExact
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
