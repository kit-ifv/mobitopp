package domain.data

import utils.units.Coordinate
import utils.units.Distance
import utils.units.UnitIntervalValue

/**
 * A location is the most generic representation for points of interest of the Simulation and should be representative
 * for any modelling of physical locations, be it known Points or abstract concepts such as Zones or Counties or maybe
 * even Countries
 * @property location A Location may have a Position specifying the exact location on the Planet. However, it might not
 * necessarily be applicable for certain concepts such as zones to have a distance
 */
interface Location {

    /**
     * Per default the distance between two locations should be calculated if both have a specified position. If either
     * location has an unspecified position the distance cannot be calculated, and it is up to the specific
     * implementation to solve distances of unknown positions
     */
    fun distance(other: Location): Distance  //{ TODO rename to flight distance
//        return other.location?.let { location?.distance(it) } ?: Distance.INFINITE
//    }
}



// Location <- Position(coord)
// Location <- Zone(centroid coord)
// Location <- PositionInZone(coord + zone)
// Location <- PositionAtRoad(zone + coord + road id + road access point) <- household | opportunity

// GpsPosition (= Coordinate)
/**
 * A position is a location that can be assigned a specific GPS coordinate on the globe.
 * @property pos The GPS location
 */
interface Position: Location {
    val coordinate: Coordinate
}

interface RoadPosition: Position {
    val road: Long
    val roadAccess: UnitIntervalValue //[0,1] TODO range value type

}

interface PositionInZone: Position {
    val zone: ZoneData //ID<ZoneData>
}

interface RoadPositionInZone: RoadPosition, PositionInZone
