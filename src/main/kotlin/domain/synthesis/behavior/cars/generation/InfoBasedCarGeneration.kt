package domain.synthesis.behavior.cars.generation

import domain.synthesis.attributes.household.HasNumberOfCars
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.cars.CarImpl
import domain.synthesis.data.car.Car
import domain.synthesis.data.car.CarSegment
import domain.synthesis.data.car.engine.EngineType

object InfoBasedCarGeneration : GenerateCars<HasNumberOfCars, Any?> {
    override fun generate(householdBuilder: MinimalistHousehold<HasNumberOfCars, *>): List<Car> =
        (0..<householdBuilder.attributes.amountOfCars).map {
            CarImpl.fromEngineType(
                engineType = EngineType.COMBUSTION,
                segment = CarSegment.SMALL,
                seats = 4,
            )
        }
}
