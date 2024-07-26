package utils.units

import units.Distance
import units.GPSCoordinate
import units.kilometers
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
interface LegcayCoordinate {
    val latitudeRadians: Double
    val longitudeRadians: Double

    fun distance(other: LegcayCoordinate): Distance
}

// Certain numbers such as the radius of the earth or the fact that the maximum latitude is 90 does not feel like magic
@Suppress("MagicNumber")
class GPSLegcayCoordinate(
    override val latitudeRadians: Double,
    override val longitudeRadians: Double
) : LegcayCoordinate {

    override fun distance(other: LegcayCoordinate): Distance {
        val deltaLat = this.latitudeRadians - other.latitudeRadians
        val deltaLong = this.longitudeRadians - other.longitudeRadians

        val haversine =
            sin(deltaLat / 2).pow(2) + cos(this.latitudeRadians) *
                cos(other.latitudeRadians) * sin(deltaLong / 2).pow(2)

        val normalized = 2 * asin(sqrt(haversine))
        val earthRadius = 6371.kilometers
        return earthRadius * normalized
    }

    companion object {
        fun degrees(lat: Double, long: Double): GPSLegcayCoordinate {
            assert(lat in -90.0..90.0)
            assert(long in 0.0..180.0)
            return GPSLegcayCoordinate(PI / 180 * lat, PI / 180 * long)
        }
    }
}

@Suppress("MagicNumber")
fun Pair<Number, Number>.toCoordinate(): GPSCoordinate {
    return GPSCoordinate.decimalDegree(first.toDouble(), second.toDouble())
}
