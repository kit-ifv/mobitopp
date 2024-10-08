package domain.enums

import utils.CodePlan
import utils.Encodable

/**
 * A mode describes the type of transportation a person uses to travel.
 * Each project can provide a custom definition of which mode of transportation are available.
 */
interface Mode : Encodable {

    val requiresVehicleTakeAlong: Boolean
}

object MODEUNKOWN : Mode {
    override val requiresVehicleTakeAlong: Boolean = false

    override fun encode(): Int {
        throw UnsupportedOperationException("MODE UNKNOWN should never be encoded!")
    }
}

/**
 * The default mode encoding from legacy MobiTopp
 *
 * @property code integer code of the mode
 */
enum class StandardMode(private val code: Int, private val isFixed: Boolean = false) : Mode {
    UNDEFINED(-2),
    UNKNOWN(-1),
    BIKE(0, true),
    CAR(1, true),
    PASSENGER(2),
    PEDESTRIAN(3),
    PUBLICTRANSPORT(4),
    TRUCK(5, true),
    PARK_AND_RIDE(6),
    TAXI(7),
    CARSHARING_STATION(11, true),
    CARSHARING_FREE(12),
    E_SCOOTER(15),
    PEDELEC(16, true),
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

    override val requiresVehicleTakeAlong: Boolean = isFixed
}
