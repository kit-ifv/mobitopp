package domain.data

import Decodable
import Encodable


/**
 * currently a placeholder interface, soon there will be additional methods
 */
interface Mode : Encodable

/**
 * The default mode encoding from legacy MobiTopp
 */
enum class StandardMode(private val code: Int): Mode {
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

    companion object: Decodable<StandardMode> {
        override fun decode(i: Int) = StandardMode.values().first {it.code == i}
    }

}
