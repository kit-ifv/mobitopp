package domain.synthesis.behavior.cars.generation

import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.cars.CarImpl
import domain.synthesis.data.Car
import domain.synthesis.data.CarSegment
import domain.synthesis.data.EngineType

/**
 * Each household gets the same amount of cars, and the cars are all the same model
 */
class TrivialCarGeneration(private val targetAmountOfCars : Int = 2) : GenerateCars<Any?, Any?> {
    override fun generate(householdBuilder: MinimalistHousehold<Any?, Any?>): List<Car> {
        return buildCars()
    }

    @Suppress("MagicNumber") // 4 seats is not magic, but default
    private fun buildCars() =
        (0..<targetAmountOfCars).map {
            CarImpl.Companion.fromEngineType(
                engineType = EngineType.COMBUSTION,
                segment = CarSegment.SMALL,
                seats = 4,
            )
        }

}