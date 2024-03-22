package data

import units.Distance
import units.GPSCoordinate

/**
 * A location is the most generic representation for points of interest of the Simulation and should be representative
 * for any modelling of physical locations, be it known Points or abstract concepts such as Zones or Counties or maybe
 * even Countries
 * @property location A Location may have a Position specifying the exact location on the Planet. However, it might not
 * necessarily be applicable for certain concepts such as zones to have a distance
 */
interface Location {
    val location: GPSCoordinate?

    /**
     * Per default the distance between two locations should be calculated if both have a specified position. If either
     * location has an unspecified position the distance cannot be calculated, and it is up to the specific
     * implementation to solve distances of unknown positions
     */
    fun distance(other: Location): Distance {
        return other.location?.let { location?.distance(it) } ?: Distance.INFINITE
    }
}

object NOWHERE: Location {
    override val location: GPSCoordinate?
        get() = null

}
