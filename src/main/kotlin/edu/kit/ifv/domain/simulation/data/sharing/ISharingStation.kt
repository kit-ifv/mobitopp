package edu.kit.ifv.domain.simulation.data.sharing
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRegionType
import edu.kit.ifv.utils.Identifiable

interface ISharingStation : Identifiable<SharingStationId> {
    val uid: String
    val name: String
    val location: StandardLocation
    val zonesByFoot: Set<Zone<HasRegionType>>
    val owner: ISharingProvider

    fun isReachableFrom(origin: StandardLocation) = zonesByFoot.any { origin in it }
    fun isReachableFrom(zone: Zone<*>) = zone in zonesByFoot
}
