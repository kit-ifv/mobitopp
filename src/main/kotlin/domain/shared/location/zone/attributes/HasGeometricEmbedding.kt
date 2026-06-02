package domain.shared.location.zone.attributes

import domain.shared.location.Location
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.Point
import org.locationtech.jts.shape.random.RandomPointsBuilder

interface HasGeometricEmbedding : HasCentroid {
    val geometry: Geometry
    override val centroid: Point
        get() {
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
