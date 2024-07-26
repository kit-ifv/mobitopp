package domain.enums

import domain.location.Location
import units.Currency
import units.euros
import utils.CodePlan
import utils.Encodable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

/**
 * A mode describes the type of transportation a person uses to travel.
 * Each project can provide a custom definition of which mode of transportation are available.
 */
interface Mode : Encodable {
    fun travelTime(from: Location, to: Location): Duration = 42.days

    fun travelCost(from: Location, to: Location): Currency = 0.euros

    fun getAccessTime(from: Location, to: Location): Duration = 0.minutes
    fun getEgressTime(from: Location, to: Location): Duration = 0.minutes
}

object MODEUNKOWN : Mode {
    override fun encode(): Int {
        throw UnsupportedOperationException("MODE UNKNOWN should never be encoded!")
    }
}

/**
 * The default mode encoding from legacy MobiTopp
 *
 * @property code integer code of the mode
 */
enum class StandardMode(private val code: Int) : Mode {
    UNDEFINED(-2),
    UNKNOWN(-1),
    BIKE(0),
    CAR(1),
    PASSENGER(2),
    PEDESTRIAN(3),
    PUBLICTRANSPORT(4),
    TRUCK(5),
    PARK_AND_RIDE(6),
    TAXI(7),
    CARSHARING_STATION(11),
    CARSHARING_FREE(12),
    E_SCOOTER(15),
    PEDELEC(16),
    BIKESHARING(17),
    RIDE_POOLING(21),
    RIDE_HAILING(22),
    PREMIUM_RIDE_HAILING(23),
    ;

    override fun encode(): Int {
        return this.code
    }

    companion object : CodePlan<StandardMode> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
        override fun values(): Set<StandardMode> = StandardMode.entries.toSet()
    }
}
