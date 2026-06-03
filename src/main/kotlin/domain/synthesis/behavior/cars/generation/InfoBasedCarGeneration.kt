package domain.synthesis.behavior.cars.generation

import domain.shared.car.Car
import domain.shared.car.CarSegment
import domain.shared.car.engine.EngineType
import domain.synthesis.attributes.household.HasNumberOfCars
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.cars.CarImpl

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
