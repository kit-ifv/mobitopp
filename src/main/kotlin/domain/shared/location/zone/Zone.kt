package domain.shared.location.zone

import Mutable
import domain.shared.enums.ZoneClassification
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.location.BetterLocation
import domain.shared.location.Location
import domain.shared.location.RoadAccess
import domain.shared.location.StandardLocation
import domain.shared.location.ZoneId
import domain.shared.location.attributes.HasRegionType
import domain.shared.location.attributes.HasZoneId
import domain.shared.location.toZoneId
import edu.kit.ifv.units.Distance
import org.jetbrains.annotations.TestOnly
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.Point
import org.locationtech.jts.shape.random.RandomPointsBuilder

interface Zone<out T> : HasZoneId {
    val attributes: T
    val centroidLocation: StandardLocation
    operator fun contains(location: HasZoneId): Boolean = zoneId == location.zoneId
}
private fun <T> Zone<T>.centroidLocation(): StandardLocation where T : HasRegionType, T : HasCentroid =
    BetterLocation(this.attributes.centroid, this, RoadAccess.INVALID)

data class MinimalZone<T>(override val zoneId: ZoneId, override val attributes: T) : Zone<T> {
    override val centroidLocation: StandardLocation
        get() = TODO("Not yet implemented")
}

@Mutable
data class StandardZone(
    override val zoneId: ZoneId,
    override val geometry: Geometry,
    override val attributes: ZoneAttributes,
) : HasGeometricEmbedding,
    Zone<HasRegionType> {

    override val centroidLocation = BetterLocation(geometry.centroid, this, RoadAccess.INVALID)
    constructor(zoneId: Number, geometry: Geometry, zoneAttributes: ZoneAttributes) : this(
        zoneId.toZoneId(),
        geometry,
        zoneAttributes,
    )
}

interface GeometricZone<out T> :
    Zone<T>,
    HasGeometricEmbedding

interface HasNumberParkingPlaces {
    val parkingPlaces: Int
}
data class ZoneAttributes(override val regionType: RegionType) : HasRegionType {
    companion object {
        @TestOnly
        val STANDARD = ZoneAttributes(regionType = RegioStaR17.URBAN_AREA_METRO)
    }
}

data class GeometricZoneImpl<T>(
    override val zoneId: ZoneId,
    override val geometry: Geometry,
    override val attributes: T,
) : GeometricZone<T> {
    constructor(number: Number, geometry: Geometry, attributes: T) : this(
        zoneId = ZoneId(number.toLong()),
        geometry = geometry,
        attributes = attributes,
    )

    override val centroidLocation: StandardLocation
        get() = TODO()
}

typealias NakedZone = GeometricZoneImpl<Unit>

interface HasVisumId {
    val visumId: Int
}

interface HasGeometricEmbedding : HasCentroid {
    val geometry: Geometry
    override val centroid: Point get() {
        val point = geometry.centroid
        point.srid = geometry.srid
        return point
    }

    operator fun contains(location: Point): Boolean = location in geometry
    fun contains(location: Location) = contains(location.position)

    fun randomPoint(): Point {
        val pointBuilder = RandomPointsBuilder(geometry.factory)
        pointBuilder.setExtent(geometry)
        pointBuilder.setNumPoints(1)
        val points = pointBuilder.geometry as MultiPoint
        return points.getGeometryN(0) as Point
    }
}

@Mutable
open class MaximalZone(override val zoneId: ZoneId, override val attributes: MaximumZoneAttributes) :
    Zone<MaximumZoneAttributes> {
    val parkingPlaces get() = attributes.parkingPlaces
    val isDestination get() = attributes.isDestination
    override val centroidLocation: StandardLocation = centroidLocation()
}

/**
 * The maximum information a zone can hold in the simulation framework, even with the most asinine information available.
 */
interface MaximumZoneAttributes :
    HasRegionType,
    HasNumberParkingPlaces,
    HasCentroid {
    val visumId: Long
    val name: String
    val classification: ZoneClassification
    val isDestination: Boolean
    val relief: Distance
}

data class MaximumZoneAttributesImpl(
    override val visumId: Long,
    override val name: String,
    override val classification: ZoneClassification,
    override val isDestination: Boolean,
    override val relief: Distance,
    override val regionType: RegionType,
    override val parkingPlaces: Int,
    override val centroid: Point,
) : MaximumZoneAttributes
