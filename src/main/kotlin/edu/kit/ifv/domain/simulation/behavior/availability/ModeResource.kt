package edu.kit.ifv.domain.simulation.behavior.availability

import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.simulation.agent.DrtOffer
import edu.kit.ifv.domain.simulation.agent.DrtRide
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.agent.PrivateCarAgent
import edu.kit.ifv.domain.simulation.agent.SharingStationAgent
import edu.kit.ifv.domain.simulation.agent.SharingVehicleAgent
import edu.kit.ifv.domain.simulation.agent.isHome

interface ModeResource {
    val mode: Mode
    fun startTrip(person: PersonAgent)
    fun endTrip(person: PersonAgent): ModeResource?
}

class NoResourceMode(override val mode: Mode) : ModeResource {
    override fun startTrip(person: PersonAgent) = Unit

    override fun endTrip(person: PersonAgent): ModeResource? = if (mode.requiresVehicleTakeAlong && !person.isHome()) {
        this
    } else {
        null
    }
}

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

typealias SharingFreeFloatingZoneAgent = SharingStationAgent
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

class PoolingResource(override val mode: Mode, val drtOffer: DrtOffer) : ModeResource {
    var drtRide: DrtRide? = null

    override fun startTrip(person: PersonAgent) {
        drtRide = drtRide ?: drtOffer.providerAgent.algorithm.bookRide(drtOffer)
    }

    override fun endTrip(person: PersonAgent): ModeResource? = null
}
