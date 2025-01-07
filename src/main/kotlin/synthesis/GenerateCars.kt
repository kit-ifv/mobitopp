package synthesis

import domain.data.Car
import domain.data.CarEngine
import domain.data.CarEngineBuilder
import domain.data.CarEngineStatistics
import domain.data.CarId
import domain.data.CarSegment
import domain.data.CombustionEngine
import domain.data.EngineType
import domain.data.Person
import domain.data.PrivateCarBuilder
import domain.location.Location

fun interface GenerateCars {
    fun generate(householdBuilder: SynthesisHouseholdBuilder): List<Car>
}
class SynthesisCar(householdBuilder: SynthesisHouseholdBuilder): Car {
    override val segment: CarSegment
        get() = CarSegment.MIDSIZE
    override val engine: CarEngine = CarEngineBuilder(CarEngineStatistics(), segment, EngineType.COMBUSTION).buildEngine()
    override val seats: Int
        get() = 4
    override var location: Location = householdBuilder.location
    override var driver: Person? = null
    override var passengers: MutableSet<Person> = mutableSetOf()
    override var keyHolder: Person? = null
    override val id: CarId = CarId(1L)

}
object TrivialCarGeneration : GenerateCars {
    override fun generate(householdBuilder: SynthesisHouseholdBuilder): List<Car> {

        return (0..<householdBuilder.amountOfCars).map { SynthesisCar(householdBuilder) }
    }

}