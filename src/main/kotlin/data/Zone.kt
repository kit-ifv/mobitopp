package data

import units.Distance
import units.GPSCoordinate
import units.meters

typealias Attractivity = Map<LegacyActivityType, Double>

/**
 * The data class for a zone in the simulation. Maybe this should be refactored into an interface to allow future
 * patches/addons.
 * @property location the location of the zone. Can be deliberately set to null
 * @property name The name of the zone
 * @property parkingPlaces the number of available parking spaces in the zone
 * @property isDestination Whether the zone can be selected as a destination
 * @property relief Maybe the height of the centroid location of the zone
 * @property attractivity a map of the attractivity of the zone
 */
data class Zone(
    override val location: GPSCoordinate? = GPSCoordinate(0.0 , 0.0),
    val name: String = "Unnamed Zone",
    val parkingPlaces: Int = 0,
    val isDestination: Boolean = false,
    val relief: Distance = 0.meters,
    val attractivity: Attractivity = mapOf()
    ): Location {
    override fun distance(other: Location): Distance {
        return location?.distance(other.location?:
        throw IllegalArgumentException("Location of other zone $other is unspecified"))
            ?: throw IllegalArgumentException("Location of zone $this is unspecified")
    }
}

