package usecases.choicemodels

import domain.data.Person
import domain.data.locationBySchedule
import domain.enums.Mode
import domain.enums.StandardMode
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

val speedupMap: Map<String, Mode> = mapOf(
    TAXI_KEY to StandardMode.TAXI,
    RIDE_POOLING_KEY to StandardMode.RIDE_POOLING,
    BIKESHARING_KEY to StandardMode.BIKESHARING,
    CARSHARING_FREE_KEY to StandardMode.CARSHARING_FREE,
    CARSHARING_STATION_KEY to StandardMode.CARSHARING_STATION,
    E_SCOOTER_KEY to StandardMode.E_SCOOTER,
    CAR_KEY to StandardMode.CAR,
    PASSENGER_KEY to StandardMode.PASSENGER,
    BIKE_KEY to StandardMode.BIKE,
    PUBLICTRANSPORT_KEY to StandardMode.PUBLICTRANSPORT,
    PEDESTRIAN_KEY to StandardMode.PEDESTRIAN,
)

object FakePlan : CodePlan<Mode> {
    override fun decode(i: Int): Mode {
        return StandardMode.decode(i)
    }

    override fun decode(s: String): Mode {
        return StandardMode.decode(s)
    }

    override fun values(): Set<Mode> {
        return speedupMap.values.toSet()
    }
}
// TODO Jelle: What is the purpose of this interface?

interface BasicModesModel {

    val modes: CodePlan<Mode>

    val modeMap: Map<String, Mode> get() = speedupMap
}

fun interface ModeFilter<M, P> {
    fun filter(modes: Collection<M>, params: P): Collection<M>
}

object NoFilter : ModeFilter<Mode, Person> {
    override fun filter(modes: Collection<Mode>, params: Person): Collection<Mode> {
        return modes
    }
}

class ModeAvailabilityFilter(override val modes: CodePlan<Mode>) : ModeFilter<Mode, Person>, BasicModesModel {

    private val cache = PerpetualCache<List<Byte>, Set<Mode>>()
    override fun filter(modes: Collection<Mode>, params: Person): Collection<Mode> {
        val modeList = modes.toList()
        val set = BitSet(modeList.size)
        set.flip(0, modeList.size)
        modeList.withIndex().forEach {
            set[it.index] = determineAvailability(it.value, params)
        }
        return cache.getOrPut(set.toByteArray().toList()) {
            modeList.filterBy(set).toSet()
        }
    }

    private fun determineAvailability(mode: Mode, params: Person): Boolean {
        return when (mode) {
            StandardMode.CAR -> checkCar(params)
            StandardMode.BIKE -> checkBike(params)

            StandardMode.PUBLICTRANSPORT -> checkPut(params)
            StandardMode.RIDE_POOLING -> checkRidepooling(params)
            StandardMode.PEDESTRIAN, StandardMode.PASSENGER -> true

            else -> false
        }
    }

    private fun checkBike(person: Person): Boolean {
        return person.hasBike
    }

    private fun checkPut(person: Person): Boolean {
        return person.hasCommuterTicket
    }

    private fun checkCar(person: Person): Boolean {
        return person.hasLicense && person.household.cars.any { it.location == person.locationBySchedule() }
    }

    private fun checkRidepooling(person: Person): Boolean {
        return person.memberships["Carsharing"] == true
    }
}

/**
 * A wrapper class containing the parameters needed to determine the mode availability of the [ModeAvailabilityFilter]
 */
data class StandardSet(
    val person: Person,
)
