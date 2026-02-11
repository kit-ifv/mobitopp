package domain.synthesis.behavior

import domain.synthesis.behavior.discreteChoice.CarSegmentChoice
import domain.synthesis.behavior.discreteChoice.CarSegmentParameters
import domain.synthesis.behavior.discreteChoice.EngineAlternative
import domain.synthesis.behavior.discreteChoice.EngineParameters
import domain.synthesis.behavior.discreteChoice.carEngineChoiceModel
import domain.synthesis.behavior.discreteChoice.carSegmentChoiceModel
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.data.Car
import domain.synthesis.data.CarEngine
import domain.synthesis.data.CarEngineStatistics
import domain.synthesis.data.CarId
import domain.synthesis.data.CarSegment
import domain.synthesis.data.EngineType
import domain.synthesis.data.buildEngine
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.random.Random

fun interface GenerateCars<T> {
    fun generate(householdBuilder: SynthesisHousehold<out T>): List<SynthesisCar>
}

class SynthesisCar(
    override val segment: CarSegment,
    engineType: EngineType,
    override val seats: Int,
    val mainUser: SynthesisPerson<*>? = null
) : Car {

    override val engine: CarEngine = CarEngineStatistics().buildEngine(segment, engineType)
    override val id: CarId = CarId(nextId)

    companion object {
        @OptIn(ExperimentalAtomicApi::class)
        private var idCounter: AtomicLong = AtomicLong(0L)

        @OptIn(ExperimentalAtomicApi::class)
        private val nextId: Long get() = idCounter.incrementAndFetch()
    }
}

object TrivialCarGeneration : GenerateCars<Any> {
    override fun generate(householdBuilder: SynthesisHousehold<out Any>): List<SynthesisCar> {
        return buildCars(householdBuilder)
    }

    @Suppress("MagicNumber") // 4 seats is not magic, but default
    private fun buildCars(householdBuilder: SynthesisHousehold<out Any>) =
        (0..<householdBuilder.amountOfCars).map {
            SynthesisCar(
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
    private val segmentModel = carSegmentChoiceModel.build(CarSegmentParameters())

    // TODO make parameters customizable!
    private val engineModel = carEngineChoiceModel.build(EngineParameters())

    override fun generate(householdBuilder: SynthesisHousehold<out SurveyWithCommute>): List<SynthesisCar> {
        // If no licence is found all adults are considered as potential owners for the generation purposes
        val potentialCarUsers = householdBuilder.run {
            if (numberOfDrivingLicences == 0) adults else licenceHolders
        }

        if (potentialCarUsers.isEmpty() && householdBuilder.amountOfCars > 0) {
            println(
                "WARN: household ${householdBuilder.surveyHouseholdId} has ${householdBuilder.amountOfCars}" +
                    " but no pot. car users:\n" +
                    "  - numOfLicenses: ${householdBuilder.numberOfDrivingLicences}\n" +
                    "  - adults: ${householdBuilder.adults}\n" +
                    "  - licenseHolders: ${householdBuilder.licenceHolders}\n" +
                    "  - all members (id, age, license): ${
                        householdBuilder.members.joinToString { "${it.personId}, ${it.age}, ${it.hasLicence}" }
                    }"
            )
        }

        val generationTargets = potentialCarUsers.selectExact(householdBuilder.amountOfCars)
        return generationTargets.map { person ->

            val random = Random(person.personId)
            val segment = context(CarSegmentChoice(person, householdBuilder), random) {
                segmentModel.select()
            }
            val engineType = context(EngineAlternative(person.information, householdBuilder), random) {
                engineModel.select()
            }

            SynthesisCar(segment, engineType, segment.toSeats(), person)
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
