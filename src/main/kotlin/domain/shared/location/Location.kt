package domain.shared.location

import domain.shared.location.zone.attributes.HasRoadAccess
import domain.shared.location.zone.attributes.HasZoneId
import domain.shared.location.zone.ZoneId
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.WGS84Coordinate
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel

interface Location<out L> {
    val position: Point
    val attributes: L
    fun distance(other: Location<*>): Distance = JTSDistanceCalculator.distance(position, other.position)

    companion object {
        fun of(point: Point): Location<*> = LocationImpl(point)

        @Suppress("MagicNumber")
        fun utm(x: Double, y: Double): Location<*> =
            of(GeometryFactory(PrecisionModel(), 25832).createPoint(Coordinate(x, y)))

        fun utm(string: String): Location<*> {
            val (x, y) = string.split(",").take(2)
            return utm(x.toDouble(), y.toDouble())
        }

        fun wgs(coord: WGS84Coordinate) = wgs(coord.x, coord.y)
        fun wgs(x: Double, y: Double): Location<*> = of(PointCreator.createWGS(x, y))

        val BIELEFELD by lazy {
            wgs(8.531007, 52.019101)
        }
    }
}
