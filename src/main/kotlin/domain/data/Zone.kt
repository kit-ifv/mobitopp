package domain.data

import Mutable
import domain.enums.AreaType
import domain.enums.ZoneClassification
import domain.location.Location
import units.Distance
import units.GPSCoordinate
import utils.ID
import utils.Identifiable
import utils.random.SeededActor

typealias ZoneId = ID<Zone>

@Mutable
abstract class Zone(
    override val id: ZoneId,
    centroid: Location,
    seed: Long,
) : SeededActor<Zone>(seed), Identifiable<ZoneId> {

    abstract val visumId: Long
    abstract val name: String
    abstract val areaType: AreaType
    abstract val regionType: Int
    abstract val classification: ZoneClassification
    abstract val parkingPlaces: Int
    abstract val isDestination: Boolean
    abstract val relief: Distance

    val centroid: Location = centroid.copy(zone = this)

    operator fun contains(location: Location): Boolean = location.zone == this
}

@Mutable
abstract class LegacyZone(
    id: ZoneId,
    centroid: Location,
    seed: Long,
) : Zone(id, centroid, seed) {

    abstract val matrixColumn: Int
}

//
// /**
// * The data class for a zone in the simulation. Maybe this should be refactored into an interface to allow future
// * patches/addons.
// * @property visumId the zone id in visum
// * @property name The name of the zone
// * @property areaType the area type of the zone
// * @property regionType the region type of the zone
// * @property classification the zone classification in the research context
// * @property parkingPlaces the number of available parking spaces in the zone
// * @property centroid the centroid point of the zone
// * @property isDestination whether the zone can be selected as a destination
// * @property relief height difference in the zone
// */
// @Mutable
// @Buildable
// @Suppress("LongParameterList")
// open class Zone(
//    override val id: ZoneId,
//    val visumId: Long,
//    val name: String,
//    val areaType: AreaType,
//    val regionType: Int,
//    val classification: ZoneClassification,
//    open val parkingPlaces: Int, // TODO only open for testing -> ugly :(
//    centroid: Location,
//    val isDestination: Boolean,
//    val relief: Distance,
// ) : Identifiable<ZoneId> {
//
//    val centroid: Location = centroid.copy(zone = this)
//    operator fun contains(location: Location): Boolean = location.zone == this
// }
//
// fun Zone.centroidLocation() = centroid.copy(zone = this)
//
fun Zone.point(gpsCoordinate: GPSCoordinate) = Location(gpsCoordinate, zone = this, roadAccess = null)
//
// @Mutable
// @Buildable
// @Suppress("LongParameterList")
// open class LegacyZone(
//    id: ZoneId,
//    visumId: Long,
//    name: String,
//    areaType: AreaType,
//    regionType: Int,
//    classification: ZoneClassification,
//    parkingPlaces: Int,
//    centroid: Location,
//    isDestination: Boolean,
//    relief: Distance,
//    val matrixColumn: Int,
// ) : Zone(
//    id, visumId, name, areaType, regionType, classification, parkingPlaces, centroid, isDestination, relief
// )
