package domain.data

import Mutable
import domain.enums.ZoneClassification
import domain.enums.areatype.AreaType
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
    abstract val regionType: AreaType // Region type and area type are the same. RegionType is the more adequate name
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

fun Zone.point(gpsCoordinate: GPSCoordinate) = Location(gpsCoordinate, zone = this, roadAccess = null)
