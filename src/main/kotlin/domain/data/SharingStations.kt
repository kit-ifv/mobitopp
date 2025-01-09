package domain.data

import Mutable
import domain.enums.Mode
import domain.location.Location
import domain.resources.Resource
import domain.resources.Subscribable
import utils.ID
import utils.Identifiable
import java.util.*

typealias SharingStationId = ID<SharingStation>

private var idCounter: Long = 0L

@Mutable
abstract class SharingProvider : Subscribable<Person> {

    abstract val mode: Mode // TODO assign proper mode
    abstract val stations: Set<SharingStation>
    abstract val ownedVehicles: Set<SharingVehicle>

    val numberOfVehicles: Int
        get() = ownedVehicles.size

    override val resources: Set<Resource<Person>>
        get() {
            return stations
        }
}

// class MutableSharingProvider(scope: SharingProviderBuilder.() -> Unit) : SharingProviderBuilder(scope)

// class MySharingProvider(
//    override val name: String,
//    val mode: Mode // TODO assign proper mode
// ) : Subscribable<Person> {
//
//    val stations: Set<SharingStation>
//        get() = _stations
//
//    val numberOfVehicles get() = _ownedVehicles.size
//    private val _stations: MutableSet<SharingStation> = mutableSetOf()
//
//    fun register(station: SharingStation) {
//        _stations += station
//    }
//
//    val ownedVehicles: Set<SharingVehicle>
//        get() = _ownedVehicles
//
//    private val _ownedVehicles: MutableSet<SharingVehicle> = mutableSetOf()
//
//    fun register(vehicle: SharingVehicle) {
//        _ownedVehicles += vehicle
//    }
//
//    override val resources: Set<Resource<Person>>
//        get() {
//            return _stations
//        }
// }

// @Buildable
@Mutable
abstract class SharingStation(
    override val id: SharingStationId = SharingStationId(idCounter++),
    val owner: MutableSharingProvider,
) : Identifiable<SharingStationId>, Resource<Person> {

    abstract val uid: String
    abstract val name: String
    abstract val location: Location
    abstract val zonesByFoot: Set<Zone>

    // only provide immutable view of vehicle set, since adding/removing vehicles requires additional logic
    val vehicles: Set<SharingVehicle>
        get() = _vehicles
    private val _vehicles: MutableSet<SharingVehicle> = Collections.synchronizedSet(mutableSetOf())

    init {
        registerOwner()
    }

    private fun registerOwner() {
        owner.stations.add(this)
    }

    fun addVehicle(vehicle: SharingVehicle) {
        vehicle.returnTo(this)
    }

    fun addVehicles(vehicles: Collection<SharingVehicle>) {
        vehicles.forEach { addVehicle(it) }
    }

    fun take(vehicle: SharingVehicle) {
        require(vehicle in vehicles) {
            "Cannot take sharing vehicle ${vehicle.id} from station ${this.id} as it is not located there."
        }

        _vehicles -= vehicle
        vehicle.take()
    }

    fun takeAny(): SharingVehicle {
        require(hasAvailableVehicles) {
            "Cannot take a sharing vehicle from station '${this.name}' as none are currently available."
        }

        require(_vehicles.isNotEmpty()) {
            "Empty $hasAvailableVehicles"
        }
        return _vehicles.first().also { take(it) }
    }

    fun giveBack(vehicle: SharingVehicle) {
        _vehicles += vehicle
        vehicle.returnTo(this)
    }

    override fun toString(): String {
        return "$id ${_vehicles.size}"
    }

    val hasAvailableVehicles: Boolean
        get() = _vehicles.isNotEmpty()

    override fun isAvailableFor(agent: Person): Boolean {
        return agent.memberships.containsKey(owner) &&
            !agent.inTransit &&
//                hasAvailableVehicles && // Available vehicles is not relevant for the resource allocation
            zonesByFoot.any { agent.location.inSameZone(it.centroid) }
    }
}

// fun SharingStation.weakerBuilder(): Builder<SharingStation> {
//    val build = SharingStationBuilder()
//    build.uid = uid
//    build.name = name
//    build.location = location
//    build.zonesByFoot = zonesByFoot.toMutableSet()
//    build.owner = owner
//    build.initialVehicles = vehicles.toMutableSet()
//    return build
// }

typealias SharingVehicleId = ID<SharingVehicle>

class SharingVehicle(
    override val id: SharingVehicleId,
    val mode: Mode,
    val owner: MutableSharingProvider,
) : Identifiable<SharingVehicleId> {

    private var currentStation: SharingStation? = null

    init {
        owner.ownedVehicles.add(this)
    }

    fun take() {
        require(currentStation != null) { "How are you taking bike $id when it has no station?" }
        currentStation = null
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

    override fun toString(): String {
        return "$id -> ${currentStation?.name}"
    }
}
