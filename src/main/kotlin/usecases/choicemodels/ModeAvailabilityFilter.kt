package usecases.choicemodels

import domain.data.Person
import domain.enums.Mode
import utils.CodePlan
import utils.PerpetualCache
import utils.collections.filterBy
import java.util.*

const val TAXI_KEY = "TAXI"
const val RIDE_POOLING_KEY = "RIDE_POOLING"
const val BIKESHARING_KEY = "BIKESHARING"
const val CARSHARING_FREE_KEY = "CARSHARING_FREE"
const val CARSHARING_STATION_KEY = "CARSHARING_STATION"
const val E_SCOOTER_KEY = "E_SCOOTER"
const val CAR_KEY = "CAR"
const val PASSENGER_KEY = "PASSENGER"
const val BIKE_KEY = "BIKE"
const val PUBLICTRANSPORT_KEY = "PUBLICTRANSPORT"
const val PEDESTRIAN_KEY = "PEDESTRIAN"

interface BasicModesModel {

    val modes: CodePlan<Mode>

    val modeMap: Map<String, Mode>
        get() = listOf(
            TAXI_KEY,
            RIDE_POOLING_KEY,
            BIKESHARING_KEY,
            CARSHARING_FREE_KEY,
            CARSHARING_STATION_KEY,
            E_SCOOTER_KEY,
            CAR_KEY,
            PASSENGER_KEY,
            BIKE_KEY,
            PUBLICTRANSPORT_KEY,
            PEDESTRIAN_KEY,
        ).associateWith {
            try {
                modes.decode(it)
            } catch (e: IllegalArgumentException) {
                error(
                    "Provided mode CodePlan ${modes::class.simpleName} does not provide a mode called $it: " +
                        "\n${e.message}"
                )
            }
        }
}

fun interface ModeFilter<M, P> {
    fun filter(modes: List<M>, params: P): Collection<M>
}

class ModeAvailabilityFilter(override val modes: CodePlan<Mode>) : ModeFilter<Mode, StandardSet>, BasicModesModel {

    private val cache = PerpetualCache<List<Byte>, Set<Mode>>()
    override fun filter(modes: List<Mode>, params: StandardSet): Collection<Mode> {
        val set = BitSet(modes.size)
        set.flip(0, modes.size)
        modes.withIndex().forEach {
            set[it.index] = determineAvailability(it.value, params)
        }
        return cache.getOrPut(set.toByteArray().toList()) {
            modes.filterBy(set).toSet()
        }
    }
    private fun determineAvailability(mode: Mode, params: StandardSet): Boolean {
        return when (mode) {
            modeMap[CAR_KEY]!! -> checkCar(params)
            modeMap[BIKE_KEY]!! -> checkBike(params)
            modeMap[PEDESTRIAN_KEY]!! -> true
            modeMap[PUBLICTRANSPORT_KEY]!! -> checkPut(params)

            else -> false
        }
    }

    private fun checkBike(params: StandardSet): Boolean {
        return params.person.hasBike
    }

    private fun checkPut(params: StandardSet): Boolean {
        return params.person.hasCommuterTicket
    }

    private fun checkCar(params: StandardSet): Boolean {
        return params.person.let { person ->
            person.hasLicense && person.isAdult && person.household.cars.any { car -> car.mainUser == person }
        }
    }
}

/**
 * A wrapper class containing the parameters needed to determine the mode availability of the [ModeAvailabilityFilter]
 */
data class StandardSet(
    val person: Person,
)
