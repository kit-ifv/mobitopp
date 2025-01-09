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
import synthesis.discreteChoice.CarSegmentChoice
import synthesis.discreteChoice.CarSegmentParameters
import synthesis.discreteChoice.FatParameters
import synthesis.discreteChoice.carEngineChoiceModel
import synthesis.discreteChoice.carSegmentChoiceModel
import synthesis.discreteChoice.toChoice

fun interface GenerateCars<T : SurveyInfo> {
    fun generate(householdBuilder: SynthesisHouseholdBuilder<T>): List<Car>
}

class SynthesisCar(
    householdBuilder: SynthesisHouseholdBuilder<*>,
    override val segment: CarSegment, engineType: EngineType, override val seats: Int,
    val mainUser: SynthesisPerson<*>? = null
) : Car {

    override val engine: CarEngine =
        CarEngineBuilder(CarEngineStatistics(), segment, engineType).buildEngine()

    override var location: Location = householdBuilder.location
    override var driver: Person? = null
    override var passengers: MutableSet<Person> = mutableSetOf()
    override var keyHolder: Person? = null
    override val id: CarId = CarId(1L)

}

object TrivialCarGeneration : GenerateCars<SurveyInfo> {
    override fun generate(householdBuilder: SynthesisHouseholdBuilder<SurveyInfo>): List<Car> {

        return buildCars(householdBuilder)
    }

    private fun buildCars(householdBuilder: SynthesisHouseholdBuilder<*>) =
        (0..<householdBuilder.amountOfCars).map {
            SynthesisCar(
                householdBuilder,
                CarSegment.SMALL,
                EngineType.COMBUSTION,
                4
            )
        }

    fun <T: SurveyInfo> generateCars(householdBuilder: SynthesisHouseholdBuilder<T>): List<SynthesisCar> {
        return buildCars(householdBuilder)
    }


}

object SamplingCarGeneration : GenerateCars<RawSurveyInfo> {
    private val segmentModel = carSegmentChoiceModel
    override fun generate(householdBuilder: SynthesisHouseholdBuilder<RawSurveyInfo>): List<Car> {
        // If no licence is found all adults are considered as potential owners for the generation purposes
        val potentialCarUsers =
            if (householdBuilder.numberOfDrivingLicences == 0) householdBuilder.adults else householdBuilder.licenceHolders
        val generationTargets = potentialCarUsers.selectExact(householdBuilder.amountOfCars)
        return generationTargets.map { person ->
            val segment = segmentModel.select({ it.toChoice(person, householdBuilder) }, CarSegmentParameters())
            val engineType = carEngineChoiceModel.select({ it.toChoice(person, householdBuilder) }, FatParameters)
            SynthesisCar(householdBuilder, segment, engineType, segment.toSeats(), person)
        }
    }

    private fun CarSegment.toSeats(): Int {
        return when (this) {
            CarSegment.SMALL -> 4
            CarSegment.MIDSIZE -> 5
            CarSegment.LARGE -> 5
        }
    }
}


val <T : SurveyInfo> SynthesisHouseholdBuilder<T>.licenceHolders
    get(): List<SynthesisPerson<T>> {
        return members.filter { it.hasLicence }
    }

val <T : SurveyInfo> SynthesisHouseholdBuilder<T>.adults
    get(): List<SynthesisPerson<T>> {
        return members.filter { it.age >= 18 }
    }

val <T : SurveyInfo> SynthesisHouseholdBuilder<T>.numberOfDrivingLicences
    get(): Int {
        return members.count { it.hasLicence }
    }