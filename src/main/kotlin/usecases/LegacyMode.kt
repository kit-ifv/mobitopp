package usecases

import domain.enums.Mode
import usecases.models.ChoiceModelModes
import utils.CodePlan

/**
 * The default mode encoding from legacy MobiTopp
 *
 * @property code integer code of the mode
 */
enum class LegacyMode(override val code: Int, private val isFixed: Boolean = false) : Mode {
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

    override val description = name

    companion object : CodePlan<LegacyMode> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
        override fun values(): Set<LegacyMode> = LegacyMode.entries.toSet()
    }

    override val requiresVehicleTakeAlong: Boolean = isFixed
}

val legacyChoiceModelModes = ChoiceModelModes(
    car = LegacyMode.CAR,
    passenger = LegacyMode.PASSENGER,
    bike = LegacyMode.BIKE,
    pedestrian = LegacyMode.PEDESTRIAN,
    publicTransport = LegacyMode.PUBLICTRANSPORT,
    bikeSharing = LegacyMode.BIKESHARING,
    ridePooling = LegacyMode.RIDE_POOLING,
    carSharingFree = LegacyMode.CARSHARING_FREE,
    carSharingStation = LegacyMode.CARSHARING_STATION,
    taxi = LegacyMode.TAXI,
    eScooter = LegacyMode.E_SCOOTER,
)
