package edu.kit.ifv.domain.simulation.behavior.availability

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.simulation.agent.DrtOffer
import edu.kit.ifv.domain.simulation.agent.DrtRide
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.agent.PrivateCarAgent
import edu.kit.ifv.domain.simulation.agent.SharingStationAgent
import edu.kit.ifv.domain.simulation.agent.SharingVehicleAgent
import edu.kit.ifv.domain.simulation.agent.isHome

/**
 * Represents a resource (e.g., a vehicle) required to use a specific transport [mode].
 */
interface ModeResource {
    /** The transport mode associated with this resource. */
    val mode: Mode

    /**
     * Actions to be performed when the trip using this resource starts.
     * @param person the person starting the trip
     */
    fun startTrip(person: PersonAgent)

    /**
     * Actions to be performed when the trip using this resource ends.
     * @param person the person ending the trip
     * @return the remaining [ModeResource] if the resource needs to be taken along, `null` otherwise
     */
    fun endTrip(person: PersonAgent): ModeResource?
}

/**
 * A [ModeResource] for modes that do not require a specific individual resource (e.g., pedestrian, public transport).
 */
class NoResourceMode(override val mode: Mode) : ModeResource {
    override fun startTrip(person: PersonAgent) = Unit

    override fun endTrip(person: PersonAgent): ModeResource? = if (mode.requiresVehicleTakeAlong && !person.isHome()) {
        this
    } else {
        null
    }
}

/**
 * A [ModeResource] representing a private car.
 * @property car the private car agent
 */
class CarResource(override val mode: Mode, val car: PrivateCarAgent) : ModeResource {
    override fun startTrip(person: PersonAgent) {
        car.state = PrivateCarAgent.CarState.IN_USE
        car.addDriver(person)
        car.keyHolder = person
    }

    override fun endTrip(person: PersonAgent): CarResource? {
        car.location = person.location
        car.removeDriver()
        car.state = PrivateCarAgent.CarState.PARKED

        if (person.household.location == person.location) {
            car.keyHolder = null
            car.state = PrivateCarAgent.CarState.PARKED
            return null
        }

        return this
    }
}

/**
 * A [ModeResource] representing a bike.
 */
class BikeResource(override val mode: Mode) : ModeResource {
    override fun startTrip(person: PersonAgent) {}
    override fun endTrip(person: PersonAgent): ModeResource? =
        if (person.household.location == person.location) {
            null
        } else this
}

/**
 * Alias for [SharingStationAgent] when used for free-floating zones.
 */
typealias SharingFreeFloatingZoneAgent = SharingStationAgent

/**
 * A [ModeResource] for free-floating sharing services.
 * @property start the starting zone/station
 * @property end the ending zone/station
 */
class SharingFreeResource(
    override val mode: Mode,
    val start: SharingFreeFloatingZoneAgent,
    val end: SharingFreeFloatingZoneAgent,
) : ModeResource {
    private lateinit var vehicle: SharingVehicleAgent

    override fun startTrip(person: PersonAgent) {
        if (!this::vehicle.isInitialized) {
            vehicle = start.takeAny()
        }
    }

    override fun endTrip(person: PersonAgent): ModeResource? {
        vehicle.returnTo(end)
        return null
    }
}

/**
 * A [ModeResource] for station-based sharing services.
 * @property station the sharing station
 */
class SharingStationResource(override val mode: Mode, val station: SharingStationAgent) : ModeResource {
    private lateinit var vehicle: SharingVehicleAgent

    override fun startTrip(person: PersonAgent) {
        if (!this::vehicle.isInitialized) {
            vehicle = station.takeAny()
        }
    }

    override fun endTrip(person: PersonAgent): SharingStationResource? {
        if (person.location.zoneId == station.location.zoneId || person.isHome()) {
            vehicle.returnTo(station)
            return null
        }

        return this
    }
}

/**
 * A [ModeResource] for ride pooling (DRT) services.
 * @property drtOffer the booking offer from the DRT provider
 */
class PoolingResource(override val mode: Mode, val drtOffer: DrtOffer) : ModeResource {
    /** The booked ride, initialized upon [startTrip]. */
    var drtRide: DrtRide? = null

    override fun startTrip(person: PersonAgent) {
        drtRide = drtRide ?: drtOffer.providerAgent.algorithm.bookRide(drtOffer)
    }

    override fun endTrip(person: PersonAgent): ModeResource? = null
}
