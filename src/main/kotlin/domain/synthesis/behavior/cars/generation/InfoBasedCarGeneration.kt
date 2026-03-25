package domain.synthesis.behavior.cars.generation

import domain.synthesis.attributes.household.HasNumberOfCars
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.cars.CarImpl
import domain.synthesis.data.Car
import domain.synthesis.data.CarSegment
import domain.synthesis.data.EngineType

object InfoBasedCarGeneration : GenerateCars<HasNumberOfCars, Any?> {
    override fun generate(householdBuilder: MinimalistHousehold<HasNumberOfCars, *>): List<Car> {
        return (0..<householdBuilder.attributes.amountOfCars).map {
            CarImpl.Companion.fromEngineType(
                engineType = EngineType.COMBUSTION,
                segment = CarSegment.SMALL,
                seats = 4
            )
        }
    }
}
