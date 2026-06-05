package edu.kit.ifv.domain.shared.location.zone
import edu.kit.ifv.Mutable
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.StandardLocationImpl
import edu.kit.ifv.domain.shared.location.road.RoadAccess
import edu.kit.ifv.domain.shared.location.zone.attributes.HasCentroid
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRegionType
import edu.kit.ifv.domain.shared.location.zone.attributes.MaximumZoneAttributes

@Mutable
open class MaximalZone(override val zoneId: ZoneId, override val attributes: MaximumZoneAttributes) :
    Zone<MaximumZoneAttributes> {
    val parkingPlaces get() = attributes.parkingPlaces
    val isDestination get() = attributes.isDestination
    override val centroidLocation: StandardLocation = centroidLocation()
}

private fun <T> Zone<T>.centroidLocation(): StandardLocation where T : HasRegionType, T : HasCentroid =
    StandardLocationImpl(this.attributes.centroid, this, RoadAccess.INVALID)
