package domain.shared.location.zone

import Mutable
import domain.shared.location.BetterLocation
import domain.shared.location.RoadAccess
import domain.shared.location.StandardLocation
import domain.shared.location.zone.attributes.HasCentroid
import domain.shared.location.zone.attributes.HasRegionType
import domain.shared.location.zone.attributes.MaximumZoneAttributes

@Mutable
open class MaximalZone(override val zoneId: ZoneId, override val attributes: MaximumZoneAttributes) :
    Zone<MaximumZoneAttributes> {
    val parkingPlaces get() = attributes.parkingPlaces
    val isDestination get() = attributes.isDestination
    override val centroidLocation: StandardLocation = centroidLocation()
}

private fun <T> Zone<T>.centroidLocation(): StandardLocation where T : HasRegionType, T : HasCentroid = BetterLocation(
    this.attributes.centroid,
    this,
    RoadAccess.INVALID,
)
