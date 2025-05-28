package synthesis

import domain.data.Car
import domain.data.CarEngine
import domain.data.CarEngineStatistics
import domain.data.CarId
import domain.data.CarSegment
import domain.data.EngineType
import domain.data.buildEngine
import modeling.models.fixed
import synthesis.discreteChoice.CarSegmentParameters
import synthesis.discreteChoice.CarSegmentSituation
import synthesis.discreteChoice.EngineChoiceSituation
import synthesis.discreteChoice.EngineParameters
import synthesis.discreteChoice.carEngineChoiceModel
import synthesis.discreteChoice.carSegmentChoiceModel
import synthesis.domain.SynthesisHousehold
import synthesis.domain.SynthesisPerson

fun interface GenerateCars<T> {
    fun generate(householdBuilder: SynthesisHousehold<out T>): List<SynthesisCar>
}

class SynthesisCar(
    householdBuilder: SynthesisHousehold<*>,
    override val segment: CarSegment,
    engineType: EngineType,
    override val seats: Int,
    val mainUser: SynthesisPerson<*>? = null
) : Car {

    override val engine: CarEngine = CarEngineStatistics().buildEngine(segment, engineType)
    override val id: CarId = CarId(1L)
}

object TrivialCarGeneration : GenerateCars<Any> {
    override fun generate(householdBuilder: SynthesisHousehold<out Any>): List<SynthesisCar> {
        return buildCars(householdBuilder)
    }

    @Suppress("MagicNumber") // 4 seats is not magic, but default
    private fun buildCars(householdBuilder: SynthesisHousehold<out Any>) =
        (0..<householdBuilder.amountOfCars).map {
            SynthesisCar(
                householdBuilder,
                CarSegment.SMALL,
                EngineType.COMBUSTION,
                4
            )
        }

    fun <T : SurveyInfo> generateCars(householdBuilder: SynthesisHousehold<out T>): List<SynthesisCar> {
        return buildCars(householdBuilder)
    }
}

/**
 * Sampling car generation pulls a sample of potential drivers from the household based on the number of licences.
 */

object SamplingCarGeneration : GenerateCars<SurveyWithCommute> {
    private val segmentModel = carSegmentChoiceModel.build(CarSegmentParameters()).fixed(CarSegment.entries.toSet())

    // TODO make parameters customizable!
    private val engineModel = carEngineChoiceModel.build(EngineParameters()).fixed(EngineType.entries.toSet())

    override fun generate(householdBuilder: SynthesisHousehold<out SurveyWithCommute>): List<SynthesisCar> {
        // If no licence is found all adults are considered as potential owners for the generation purposes
        val potentialCarUsers = householdBuilder.run {
            if (numberOfDrivingLicences == 0) adults else licenceHolders
        }
        val generationTargets = potentialCarUsers.selectExact(householdBuilder.amountOfCars)
        return generationTargets.map { person ->
            val segment = segmentModel.filterAndSelect(CarSegmentSituation(person, householdBuilder))
            val engineType = engineModel.filterAndSelect(EngineChoiceSituation(person.info, householdBuilder))

            SynthesisCar(householdBuilder, segment, engineType, segment.toSeats(), person)
            // TODO there already is a model to determine main user, does selectExact match that definition?
        }
    }

    @Suppress("MagicNumber") // Seat size is a number
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

@Suppress("MagicNumber")
val <T : SurveyInfo> SynthesisHousehold<T>.adults
    get(): List<SynthesisPerson<out T>> {
        return members.filter { it.age >= 18 }
    }

val <T : SurveyInfo> SynthesisHousehold<T>.numberOfDrivingLicences
    get(): Int {
        return members.count { it.hasLicence }
    }
