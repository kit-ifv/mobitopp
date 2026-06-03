package domain.simulation.data

import domain.shared.location.StandardLocation
import domain.shared.location.zone.Zone
import domain.shared.location.zone.attributes.HasRegionType
import utils.Identifiable

interface ISharingStation : Identifiable<SharingStationId> {
    val uid: String
    val name: String
    val location: StandardLocation
    val zonesByFoot: Set<Zone<HasRegionType>>
    val owner: ISharingProvider

    fun isReachableFrom(origin: StandardLocation) = zonesByFoot.any { origin in it }
    fun isReachableFrom(zone: Zone<*>) = zone in zonesByFoot
}