package domain.synthesis.behavior.cars.generation

import domain.simulation.data.car.Car
import domain.simulation.data.car.CarSegment
import domain.simulation.data.car.engine.EngineType
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
