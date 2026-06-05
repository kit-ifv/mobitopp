package edu.kit.ifv.domain.simulation.data.sharing
import Mutable

@Mutable
abstract class SharingProvider(
    final override val id: SharingProviderId,
    // todo should name and mode also be immutable?
) : ISharingProvider {
    abstract override val stations: Set<SharingStation>

    override val numberOfVehicles: Int
        get() = stations.sumOf { it.initialVehicleCount }
}
