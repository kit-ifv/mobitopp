package domain.synthesis.data

import Mutable
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.shared.location.Zone
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

    override val numberOfVehicles: Int
        get() = stations.sumOf { it.initialVehicleCount }
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
) : ISharingStation {

    abstract val initialVehicleCount: Int

    init {
        registerOwner()
    }

    private fun registerOwner() {
        owner.stations.add(this)
    }
}
