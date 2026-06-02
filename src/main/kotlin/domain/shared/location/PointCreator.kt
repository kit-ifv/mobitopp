package domain.shared.location

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel

@Suppress("MagicNumber")
object PointCreator {

    private val wgsFactory = GeometryFactory(PrecisionModel(), 4326)
    private val utmFactory = GeometryFactory(PrecisionModel(), 25832)

    fun createWGS(x: Double, y: Double): Point = wgsFactory.createPoint(Coordinate(x, y))

    fun createUTM(x: Double, y: Double): Point = utmFactory.createPoint(Coordinate(x, y))

    fun createUTM(string: String): Point {
        val (x, y) = string.split(",").take(2)
        return createUTM(x.toDouble(), y.toDouble())
    }
}