package data


import utils.units.GPSPosition

/**
 * A position is a location that can be assigned a specific GPS coordinate on the globe.
 * @property pos The GPS location
 */
data class Position(private val pos: GPSPosition): Location {
    constructor(lat: Double, lon: Double): this(GPSPosition(lat, lon))
    override val location = pos

}
