package domain.data

import Buildable
import domain.enums.AreaType
import domain.enums.Regiostar17
import domain.enums.ZoneClassification
import domain.location.Location
import units.Area
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
 * @property regionType the region type of the zone, could be Regiostar17 or some of the old legacy encodings :/
 * @property classification the zone classification in the research context
 * @property parkingPlaces the number of available parking spaces in the zone
 * @property centroid the centroid point of the zone
 * @property isDestination whether the zone can be selected as a destination
 * @property relief height difference in the zone
 */
@Buildable
@Suppress("LongParameterList")
open class Zone(
    override val id: ZoneId,
    val visumId: Long,
    val name: String,
    val regionType: AreaType,
    val classification: ZoneClassification,
    open val parkingPlaces: Int, // TODO only open for testing -> ugly :(
    centroid: Location, //TODO this should be a coordinate only, otherwise there is a circular dependency.
    val isDestination: Boolean,
    val relief: Distance,
) : Identifiable<ZoneId> {

    val centroid: Location = centroid.copy(zone = this)
    operator fun contains(location: Location): Boolean = location.zone == this
}

fun Zone.centroidLocation() = centroid.copy(zone = this)

fun Zone.point(gpsCoordinate: GPSCoordinate) = Location(gpsCoordinate, zone = this, roadAccess = null)

@Buildable
@Suppress("LongParameterList")
open class LegacyZone(
    id: ZoneId,
    visumId: Long,
    name: String,
    regionType: AreaType,
    classification: ZoneClassification,
    parkingPlaces: Int,
    centroid: Location,
    isDestination: Boolean,
    relief: Distance,
    val matrixColumn: Int,
) : Zone(
    id, visumId, name,  regionType, classification, parkingPlaces, centroid, isDestination, relief
)
