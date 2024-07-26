package domain.data

import Buildable
import domain.enums.Mode
import domain.location.ZoneLocation
import utils.Builder
import utils.ID
import utils.Identifiable

typealias SharingStationId = ID<SharingStation>

private var idCounter: Long = 0L

class SharingProvider(
    val name: String,
) {
    val stations: Set<SharingStation>
        get() = _stations

    private val _stations: MutableSet<SharingStation> = mutableSetOf()

    fun register(station: SharingStation) {
        _stations += station
    }

    val ownedVehicles: Set<SharingVehicle>
        get() = _ownedVehicles

    private val _ownedVehicles: MutableSet<SharingVehicle> = mutableSetOf()

    fun register(vehicle: SharingVehicle) {
        _ownedVehicles += vehicle
    }
}

@Buildable
class SharingStation(
    val uid: String,
    val name: String,
    val location: ZoneLocation,
    val zonesByFoot: Set<Zone>,
    val owner: SharingProvider,
    initialVehicles: Set<SharingVehicle>,
) : Identifiable<SharingStationId> {
    override val id = SharingStationId(idCounter++)
    val vehicles: Set<SharingVehicle>
        get() = _vehicles

    private val _vehicles: MutableSet<SharingVehicle> = mutableSetOf()

    init {
        _vehicles.addAll(initialVehicles)
        owner.register(this)
    }

    fun take(vehicle: SharingVehicle) {
        this._vehicles -= vehicle
        vehicle.take()
    }

    fun giveBack(vehicle: SharingVehicle) {
        this._vehicles += vehicle
        vehicle.returnTo(this)
    }
}
fun SharingStation.weakerBuilder(): Builder<SharingStation> {
    val build = SharingStationBuilder()
    build.uid = uid
    build.name = name
    build.location = location
    build.zonesByFoot = zonesByFoot.toMutableSet()
    build.owner = owner
    build.initialVehicles = vehicles.toMutableSet()
    return build
}

typealias SharingVehicleId = ID<SharingVehicle>
class SharingVehicle(
    override val id: SharingVehicleId,
    val mode: Mode,
    val owner: SharingProvider,
) : Identifiable<SharingVehicleId> {

    private var currentStation: SharingStation? = null

    init {
        owner.register(this)
    }

    fun take() {
        currentStation?.also {
            currentStation = null
            it.take(this)
        }
    }

    fun returnTo(sharingStation: SharingStation) {
        if (currentStation == null) {
            currentStation = sharingStation
            sharingStation.giveBack(this)
        }

        require(currentStation == sharingStation) {
            "Cannot return SharingVehicle[$id] to SharingStation[${sharingStation.id} " +
                "as it is currently located at SharingStation[${currentStation?.id}]!"
        }
    }
}
