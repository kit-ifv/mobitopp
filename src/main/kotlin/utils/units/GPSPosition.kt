package utils.units

import kotlin.math.*

// Certain numbers such as the radius of the earth or the fact that the maximum latitude is 90 does not feel like magic
@Suppress("MagicNumber")
class GPSPosition(private val latitudeRadians: Double, private val longitudeRadians: Double) {

    fun distance(other: GPSPosition): Distance {
        val deltaLat = this.latitudeRadians - other.latitudeRadians
        val deltaLong = this.longitudeRadians - other.longitudeRadians

        val haversine =
            sin(deltaLat / 2).pow(2) + cos(this.latitudeRadians) *
                    cos(other.latitudeRadians) * sin(deltaLong / 2).pow(2)

        val normalized = 2* asin(sqrt(haversine))
        val earthRadius = 6371.kilometers
        return earthRadius * normalized
    }

    companion object {
        fun degrees(lat: Double, long: Double): GPSPosition {
            assert(lat in -90.0..90.0)
            assert(long in 0.0..180.0)
            return GPSPosition(PI / 180 * lat, PI / 180 * long)
        }
    }
}
fun Pair<Number, Number>.toCoordinate(): GPSPosition {
    return GPSPosition(this.first.toDouble(), this.second.toDouble())
}
