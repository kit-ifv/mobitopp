package domain.simulation.data.sharing

import Mutable

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