package edu.kit.ifv.domain.synthesis.behavior.cars.generation
import edu.kit.ifv.domain.shared.car.Car
import edu.kit.ifv.domain.shared.car.CarSegment
import edu.kit.ifv.domain.shared.car.engine.EngineType
import edu.kit.ifv.domain.synthesis.behavior.MinimalistHousehold
import edu.kit.ifv.domain.synthesis.behavior.cars.CarImpl

/**
 * Each household gets the same amount of cars, and the cars are all the same model
 */
class TrivialCarGeneration(private val targetAmountOfCars: Int = 2) : GenerateCars<Any?, Any?> {
    override fun generate(householdBuilder: MinimalistHousehold<Any?, Any?>): List<Car> = buildCars()

    @Suppress("MagicNumber") // 4 seats is not magic, but default
    private fun buildCars() =
        (0..<targetAmountOfCars).map {
            CarImpl.fromEngineType(
                engineType = EngineType.COMBUSTION,
                segment = CarSegment.SMALL,
                seats = 4,
            )
        }
}
