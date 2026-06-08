package edu.kit.ifv.domain.shared.location.jts
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel

@Suppress("MagicNumber")
object PointCreator {

    private val wgsFactory = GeometryFactory(PrecisionModel(), 4326)
    private val utmFactory = GeometryFactory(PrecisionModel(), 25832)

    fun createWGS(x: Number, y: Number): Point = wgsFactory.createPoint(Coordinate(x.toDouble(), y.toDouble()))

    fun createUTM(x: Number, y: Number): Point = utmFactory.createPoint(Coordinate(x.toDouble(), y.toDouble()))

    fun createUTM(string: String): Point {
        val (x, y) = string.split(",").take(2)
        return createUTM(x.toDouble(), y.toDouble())
    }
}
