package data


import units.GPSCoordinate

/**
 * A position is a location that can be assigned a specific GPS coordinate on the globe.
 * @property pos The GPS location
 */
data class Position(private val pos: GPSCoordinate): Location {
    constructor(lat: Double, lon: Double): this(GPSCoordinate(lat, lon))
    override val location = pos

}
