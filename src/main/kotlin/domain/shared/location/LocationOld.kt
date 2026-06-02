package domain.shared.location

import domain.shared.location.zone.attributes.HasRoadAccess
import edu.kit.ifv.units.KCoordinate
import edu.kit.ifv.units.UTMPosition
import edu.kit.ifv.units.WGS84Coordinate
import org.geotools.api.referencing.cs.CoordinateSystem
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import kotlin.text.removeSurrounding

@Suppress("MagicNumber")
fun WGS84Coordinate.toPoint(): Point = GeometryFactory(PrecisionModel(), 4326).createPoint(Coordinate(x, y))

@Suppress("MagicNumber")
object PointCreator {

    private val wgsFactory = GeometryFactory(PrecisionModel(), 4326)
    private val utmFactory = GeometryFactory(PrecisionModel(), 28532)
    fun createWGS(coord: KCoordinate) = createWGS(coord.x, coord.y)

    fun createWGS(string: String): Point {
        val (x, y) = string.split(",").take(2)
        return createWGS(x.toDouble(), y.toDouble())
    }

    fun createWGS(x: Double, y: Double): Point = wgsFactory.createPoint(Coordinate(x, y))

    fun createUTM(x: Double, y: Double): Point = utmFactory.createPoint(Coordinate(x, y))

    fun createUTM(string: String): Point {
        val (x, y) = string.split(",").take(2)
        return createUTM(x.toDouble(), y.toDouble())
    }
}

data class RoadAccessLocationImpl(
    override val position: Point,
    override val attributes: HasRoadAccess,
) : Location<HasRoadAccess> {
    constructor(position: Point, roadAccess: RoadAccess) : this(position, object : HasRoadAccess)
}

fun CoordinateSystem.axisUnits(): Set<String> = (0 until dimension).map { this.getAxis(it).unit.name }.toSet()


/**
 * Parse a point from the legacy point definition.
 */
fun String.parsePoint(factory: GeometryFactory = GeometryFactory(PrecisionModel(), 4326)): Point {
    val res = this.removeSurrounding(prefix = "(", suffix = ")").split(":", ",").map { it.trim() }
    return factory.createPoint(Coordinate(res[0].toDouble(), res[1].toDouble()))
}

fun KCoordinate.toUTM(): UTMPosition = WGS84Coordinate.decimalDegree(y, x).toUTM()
