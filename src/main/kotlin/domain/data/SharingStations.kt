package domain.data

import Mutable
import domain.enums.Mode
import domain.location.Location
import utils.ID
import utils.Identifiable

typealias SharingProviderId = ID<SharingProvider>

interface ISharingProvider : Identifiable<SharingProviderId> {
    val name: String
    val mode: Mode // TODO assign proper mode
    val stations: Set<ISharingStation>

//    val ownedVehicles: Set<ISharingVehicle>
    val numberOfVehicles: Int
}

@Mutable
abstract class SharingProvider(
    final override val id: SharingProviderId
    // todo should name and mode also be immutable?
) : ISharingProvider {
    abstract override val stations: Set<SharingStation>
}

typealias SharingStationId = ID<SharingStation>

interface ISharingStation : Identifiable<SharingStationId> {
    val uid: String
    val name: String
    val location: Location
    val zonesByFoot: Set<Zone>
    val owner: ISharingProvider
}

@Mutable
abstract class SharingStation(
    final override val id: SharingStationId,
    final override val owner: MutableSharingProvider,
    val initialVehicleCount: Int,
) : ISharingStation { // , Resource<Person>

    init {
        registerOwner()
    }

    private fun registerOwner() {
        owner.stations.add(this)
        owner.numberOfVehicles += initialVehicleCount
    }
}
