package domain.synthesis.behavior

import domain.synthesis.attributes.household.HasNumberOfCars
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.HasLicence
import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
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

fun interface GenerateCars<in S, in T> {
    fun generate(householdBuilder: MinimalistHousehold<S, T>): List<Car>
}
fun interface AssignMainUser<S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> {
    fun assign(household: SynthesisHousehold<S, T>, cars: List<Car>): List<SynthesisCar>
}

class BySeniority<S: MinimumHouseholdAttributes, T: MinimumPersonAttributes>: AssignMainUser<S, T> {
    override fun assign(
        household: SynthesisHousehold<S, T>,
        cars: List<Car>,
    ): List<SynthesisCar> {
        val members = household.sortedByDescending { it.age }
        val output = members.zip(cars) { member, car ->
            SynthesisCar(car, member)

        }
        return output

    }
}

data class CarImpl(
    override val engine: CarEngine,
    override val segment: CarSegment,
    override val seats: Int = segment.toSeats(),

): Car {

    override val id: CarId = nextId



    companion object {
        @Suppress("MagicNumber") // Seat size is a number
        private fun CarSegment.toSeats(): Int {
            return when (this) {
                CarSegment.SMALL -> 4
                CarSegment.MIDSIZE -> 5
                CarSegment.LARGE -> 5
            }
        }

        fun fromEngineType(
            engineType: EngineType,
            segment: CarSegment,
            seats: Int? = null,
        ): Car {
            val actualSeats = seats ?: segment.toSeats()
            return CarImpl(
                engine = CarEngineStatistics().buildEngine(segment, engineType),
                segment = segment,
                seats = actualSeats,
            )
        }
        @OptIn(ExperimentalAtomicApi::class)
        private var idCounter: AtomicLong = AtomicLong(0L)

        @OptIn(ExperimentalAtomicApi::class)
        private val nextId: CarId get() = CarId(idCounter.incrementAndFetch())
    }
}

class SynthesisCar constructor(
    private val car: Car,
    val mainUser: SynthesisPerson<*, *>? = null
) : Car by car




/**
 * Each household gets the same amount of cars, and the cars are all the same model
 */
class TrivialCarGeneration(private val targetAmountOfCars : Int = 2) : GenerateCars<Any?, Any?> {
    override fun generate(householdBuilder: MinimalistHousehold<Any?, Any?>): List<Car> {
        return buildCars(householdBuilder)
    }

    @Suppress("MagicNumber") // 4 seats is not magic, but default
    private fun buildCars(householdBuilder: MinimalistHousehold<Any?, Any?>) =
        (0..<targetAmountOfCars).map {
            CarImpl.fromEngineType(
                engineType = EngineType.COMBUSTION,
                segment = CarSegment.SMALL,
                seats = 4,
            )
        }

    fun <T : MinimumPersonAttributes> generateCars(householdBuilder: SynthesisHousehold<MinimumHouseholdAttributes, T>): List<Car> {
        return buildCars(householdBuilder)
    }
}

object InfoBasedCarGeneration: GenerateCars<HasNumberOfCars, Any?> {
    override fun generate(householdBuilder: MinimalistHousehold<HasNumberOfCars, *>): List<Car> {
        return (0..<householdBuilder.attributes.amountOfCars).map {
            CarImpl.fromEngineType(
                engineType = EngineType.COMBUSTION,
                segment = CarSegment.SMALL,
                seats = 4
            )
        }
    }
}


/**
 * Sampling car generation pulls a sample of potential drivers from the household based on the number of licences.
 */

class SamplingCarGeneration<S> : GenerateCars<S, MaximumPersonAttributes> where S: MinimumHouseholdAttributes, S: HasNumberOfCars {
    private val segmentModel = carSegmentChoiceModel.build(CarSegmentParameters())

    // TODO make parameters customizable!
    private val engineModel = carEngineChoiceModel.build(EngineParameters())

    override fun generate(householdBuilder: MinimalistHousehold<S, MaximumPersonAttributes>): List<Car> {
        // If no licence is found all adults are considered as potential owners for the generation purposes
        val potentialCarUsers = householdBuilder.run {
            if (numberOfDrivingLicences == 0) adults else licenceHolders
        }
        val generationTargets = potentialCarUsers.selectExact(householdBuilder.attributes.amountOfCars)
        return generationTargets.map { person ->

            val random = Random(person.attributes.personNumber)
            val segment = context(CarSegmentChoice.create(person, householdBuilder), random) {
                segmentModel.select()
            }
            val engineType = context(EngineAlternative.fromHousehold(person.attributes, householdBuilder), random) {
                engineModel.select()
            }
            CarImpl.fromEngineType(
                engineType = engineType,
                segment = segment,
            )
        }
    }


}

val  <T: HasLicence> MinimalistHousehold<*, T>.licenceHolders
    get(): List<MinimalistPerson<T>> {
        return members.filter { it.hasLicence }
    }

@Suppress("MagicNumber")
val <T: MinimumPersonAttributes > MinimalistHousehold<*,T>.adults
    get(): List<MinimalistPerson<T>> {
        return members.filter { it.attributes.age >= 18 }
    }

val <T: HasLicence> MinimalistHousehold<*,T>.numberOfDrivingLicences
    get(): Int {
        return members.count { it.hasLicence }
    }
