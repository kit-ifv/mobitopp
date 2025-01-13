package synthesis

import domain.data.Car
import domain.data.CarEngine
import domain.data.CarEngineBuilder
import domain.data.CarEngineStatistics
import domain.data.CarId
import domain.data.CarSegment
import domain.data.EngineType
import domain.data.Person
import domain.location.Location
import synthesis.discreteChoice.CarSegmentParameters
import synthesis.discreteChoice.FatParameters
import synthesis.discreteChoice.carEngineChoiceModel
import synthesis.discreteChoice.carSegmentChoiceModel
import synthesis.discreteChoice.toChoice
import synthesis.domain.SynthesisPerson

fun interface GenerateCars<T> {
    fun generate(householdBuilder: SynthesisHousehold<T>): List<SynthesisCar>
}

class SynthesisCar(
    householdBuilder: SynthesisHousehold<*>,
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

object TrivialCarGeneration : GenerateCars<Any> {
    override fun generate(householdBuilder: SynthesisHousehold<Any>): List<SynthesisCar> {

        return buildCars(householdBuilder)
    }

    private fun buildCars(householdBuilder: SynthesisHousehold<*>) =
        (0..<householdBuilder.amountOfCars).map {
            SynthesisCar(
                householdBuilder,
                CarSegment.SMALL,
                EngineType.COMBUSTION,
                4
            )
        }

    fun <T: SurveyInfo> generateCars(householdBuilder: SynthesisHousehold<T>): List<SynthesisCar> {
        return buildCars(householdBuilder)
    }


}


/**
 * Sampling car generation pulls a sample of potential drivers from the household based on the number of licences.
 */
object SamplingCarGeneration : GenerateCars<RawSurveyInfo> {
    private val segmentModel = carSegmentChoiceModel
    override fun generate(householdBuilder: SynthesisHousehold<RawSurveyInfo>): List<SynthesisCar> {
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


val <T : SurveyInfo> SynthesisHousehold<T>.licenceHolders
    get(): List<SynthesisPerson<out T>> {
        return members.filter { it.hasLicence }
    }

val <T : SurveyInfo> SynthesisHousehold<T>.adults
    get(): List<SynthesisPerson<out T>> {
        return members.filter { it.age >= 18 }
    }

val <T : SurveyInfo> SynthesisHousehold<T>.numberOfDrivingLicences
    get(): Int {
        return members.count { it.hasLicence }
    }