package domain.simulation.agent

import Mutable
import domain.shared.enums.Mode
import domain.synthesis.data.ISharingProvider
import domain.synthesis.data.ISharingStation
import domain.synthesis.data.SharingProviderId
import domain.synthesis.data.SharingStationId
import utils.ID
import utils.Identifiable
import java.util.*

@Mutable
abstract class SharingProviderAgent(
    final override val id: SharingProviderId,
    final override val name: String,
    final override val mode: Mode,
) : ISharingProvider {

    abstract override val stations: Set<SharingStationAgent>
    abstract val ownedVehicles: Set<SharingVehicleAgent>

    override val numberOfVehicles: Int
        get() = ownedVehicles.size

}

@Mutable
abstract class SharingStationAgent(
    final override val id: SharingStationId,
    final override val owner: SharingProviderAgent
) : ISharingStation {

    // only provide an immutable view of the vehicle set, since adding/removing vehicles requires additional logic
    val vehicles: Set<SharingVehicleAgent>
        get() = _vehicles
    private val _vehicles: MutableSet<SharingVehicleAgent> = Collections.synchronizedSet(mutableSetOf())

    fun addVehicle(vehicle: SharingVehicleAgent) {
        vehicle.returnTo(this)
    }

    fun addVehicles(vehicles: Collection<SharingVehicleAgent>) {
        vehicles.forEach { addVehicle(it) }
    }

    fun take(vehicle: SharingVehicleAgent) {
        require(vehicle in vehicles) {
            "Cannot take sharing vehicle ${vehicle.id} from station ${this.id} as it is not located there."
        }

        _vehicles -= vehicle
        vehicle.take()
    }

    fun takeAny(): SharingVehicleAgent {
        require(hasAvailableVehicles) {
            "Cannot take a sharing vehicle from station '${this.name}' as none are currently available."
        }

        require(_vehicles.isNotEmpty()) { // TODO is this a duplicate/redundant require?
            "Empty $hasAvailableVehicles"
        }
        return _vehicles.first().also { take(it) }
    }

    fun giveBack(vehicle: SharingVehicleAgent) {
        _vehicles += vehicle
        vehicle.returnTo(this)
    }

    override fun toString(): String {
        return "$id ${_vehicles.size}"
    }

    val hasAvailableVehicles: Boolean
        get() = _vehicles.isNotEmpty()


}

typealias SharingVehicleId = ID<SharingVehicleAgent>

class SharingVehicleAgent(
    override val id: SharingVehicleId,
    val mode: Mode,
    val owner: SharingProviderAgent,
) : Identifiable<SharingVehicleId> {
//
//     init {
//         owner.ownedVehicles.add(this)
//     }

    private var currentStation: SharingStationAgent? = null

    fun take() {
        require(currentStation != null) { "How are you taking bike $id when it has no station?" }
        currentStation = null
    }

    fun returnTo(sharingStation: SharingStationAgent) {
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
