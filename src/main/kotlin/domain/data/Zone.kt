package domain.data

import Buildable
import domain.enums.AreaType
import domain.enums.ZoneClassification
import domain.location.Position
import domain.location.RoadPosition
import domain.location.RoadPositionInZone
import domain.location.ZoneLocation
import domain.location.ZoneLocationImpl
import units.Distance
import units.GPSCoordinate
import utils.ID
import utils.Identifiable

typealias ZoneId = ID<Zone>

/**
 * The data class for a zone in the simulation. Maybe this should be refactored into an interface to allow future
 * patches/addons.
 * @property visumId the zone id in visum
 * @property name The name of the zone
 * @property areaType the area type of the zone
 * @property regionType the region type of the zone
 * @property classification the zone classification in the research context
 * @property parkingPlaces the number of available parking spaces in the zone
 * @property centroid the centroid point of the zone
 * @property isDestination whether the zone can be selected as a destination
 * @property relief height difference in the zone
 */
@Buildable
interface Zone : Identifiable<ZoneId> {
    val visumId: Long
    val name: String
    val areaType: AreaType
    val regionType: Int
    val classification: ZoneClassification
    val parkingPlaces: Int
    val centroid: Position // TODO type point
    val isDestination: Boolean
    val relief: Distance
}

fun Zone.point(gpsCoordinate: GPSCoordinate): ZoneLocation {
    return ZoneLocationImpl(gpsCoordinate, this)
}

/**
 * I feel this operator makes sense, a zone plus a road position results in a roadPositionInZone
 */
operator fun Zone.plus(roadPosition: RoadPosition): RoadPositionInZone {
    return RoadPositionInZone(roadPosition, this)
}

@Buildable
interface LegacyZone : Zone {
    val matrixColumn: Int
}
