package domain.shared.location

import edu.kit.ifv.units.WGS84Coordinate
import org.geotools.api.referencing.cs.CoordinateSystem
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import kotlin.text.removeSurrounding

@Suppress("MagicNumber")
fun WGS84Coordinate.toPoint(): Point = PointCreator.createWGS(x, y)

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

fun CoordinateSystem.axisUnits(): Set<String> = (0 until dimension).map { this.getAxis(it).unit.name }.toSet()

/**
 * Parse a point from the legacy point definition.
 */
fun String.parsePoint(factory: GeometryFactory = GeometryFactory(PrecisionModel(), 4326)): Point {
    val res = this.removeSurrounding(prefix = "(", suffix = ")").split(":", ",").map { it.trim() }
    return factory.createPoint(Coordinate(res[0].toDouble(), res[1].toDouble()))
}

