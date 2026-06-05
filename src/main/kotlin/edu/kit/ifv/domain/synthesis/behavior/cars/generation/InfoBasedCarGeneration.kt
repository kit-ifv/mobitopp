package edu.kit.ifv.domain.synthesis.behavior.cars.generation
import edu.kit.ifv.domain.shared.car.Car
import edu.kit.ifv.domain.shared.car.CarSegment
import edu.kit.ifv.domain.shared.car.engine.EngineType
import edu.kit.ifv.domain.synthesis.attributes.household.HasNumberOfCars
import edu.kit.ifv.domain.synthesis.behavior.MinimalistHousehold
import edu.kit.ifv.domain.synthesis.behavior.cars.CarImpl

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
