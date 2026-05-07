package domain.shared.location.zone

import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.location.BetterLocation
import domain.shared.location.Location
import domain.shared.location.RoadAccess
import domain.shared.location.ZoneId
import domain.shared.location.attributes.HasRegionType
import domain.shared.location.attributes.HasZoneId
import domain.shared.location.toZoneId
import geopackage.GeoPackageAdapter
import org.jetbrains.annotations.TestOnly
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.Point
import org.locationtech.jts.shape.random.RandomPointsBuilder
import utils.Identifiable
import kotlin.io.path.Path

interface Zone<out T>: Identifiable<ZoneId> {
    override val id: ZoneId
    val attributes: T

    operator fun contains(location: HasZoneId): Boolean = id == location.zoneId
}

data class StandardZone(
    override val id: ZoneId,
    override val geometry: Geometry,
    override val attributes: ZoneAttributes
): HasGeometricEmbedding, Zone<HasRegionType> {

    val centroidLocation = BetterLocation(geometry.centroid, this, RoadAccess.INVALID)
    constructor(zoneId: Number, geometry: Geometry, zoneAttributes: ZoneAttributes) : this(zoneId.toZoneId(), geometry, zoneAttributes)
}

interface GeometricZone<T> : Zone<T>, HasGeometricEmbedding
interface HasNumberParkingPlaces {
    val parkingPlaces: Int
}
data class ZoneAttributes(override val regionType: RegionType): HasRegionType {
    companion object {
        @TestOnly
        val STANDARD = ZoneAttributes(regionType = RegioStaR17.URBAN_AREA_METRO)
    }
}


data class GeometricZoneImpl<T>(
    override val id: ZoneId,
    override val geometry: Geometry,
    override val attributes: T,
): GeometricZone<T> {
    constructor(number: Number, geometry: Geometry, attributes: T): this(
        id = ZoneId(number.toLong()),
        geometry = geometry,
        attributes = attributes
    )
}

typealias NakedZone = GeometricZoneImpl<Unit>

interface HasCentroid {
    val centroid: Point
}
interface HasVisumId {
    val visumId: Int
}

interface HasGeometricEmbedding: HasCentroid {
    val geometry: Geometry
    override val centroid: Point get() {
        val point = geometry.centroid
        point.srid = geometry.srid
        return point
    }

    operator fun contains(location: Point): Boolean = location in geometry
    operator fun contains(location: Location) = contains(location.position)

    fun randomPoint(): Point {
        val pointBuilder = RandomPointsBuilder(geometry.factory)
        pointBuilder.setExtent(geometry)
        pointBuilder.setNumPoints(1)
        val points = pointBuilder.geometry as MultiPoint
        return points.getGeometryN(0) as Point

    }
}

fun main() {

    val parser = GeoPackageAdapter(Path("\\\\ifv-fs.ifv.kit.edu\\Forschung\\Projekte\\RegionStuttgart_ErhebungModellMakroMikro\\Work\\Modelle\\Bevölkerungssynthese\\zones.gpkg"))
    val schemas = parser.tableSchemas

    val zones = parser.read(0) {geometry, table ->
        val any = table["NO"].toString()
        NakedZone(any.toInt(), geometry, Unit)
    }
    val random = zones.first().randomPoint()
    println(zones)
}
