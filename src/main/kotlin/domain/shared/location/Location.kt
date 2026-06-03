package domain.shared.location

import domain.shared.location.jts.JTSDistanceCalculator
import domain.shared.location.jts.PointCreator
import edu.kit.ifv.units.Distance
import org.locationtech.jts.geom.Point

interface Location<out L> {
    val position: Point
    val attributes: L
    fun distance(other: Location<*>): Distance = JTSDistanceCalculator.distance(position, other.position)

    companion object {
        fun of(point: Point): Location<*> = LocationImpl(point)

        @Suppress("MagicNumber")
        fun utm(x: Double, y: Double): Location<*> = of(PointCreator.createUTM(x, y))

        fun utm(string: String): Location<*> {
            val (x, y) = string.split(",").take(2)
            return utm(x.toDouble(), y.toDouble())
        }

        fun wgs(x: Double, y: Double): Location<*> = of(PointCreator.createWGS(x, y))
    }
}
