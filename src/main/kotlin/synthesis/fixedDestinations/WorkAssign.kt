package synthesis.fixedDestinations

import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.location.Location
import modeling.discreteChoice.GlobalRandomizer
import synthesis.SynthesisPerson
import synthesis.randomCoordinate
import utils.collections.select
import java.nio.file.Path
import kotlin.io.path.Path


fun interface DetermineLocationInZone {
    fun getLocation(zone: Zone): Location
}

/**
 * Assign a location randomly in a radius of 100 meters around the zone centroid
 */
object DebugZoneAssigner : DetermineLocationInZone {
    override fun getLocation(zone: Zone): Location {
        return Location(zone.centroid.coordinate.randomCoordinate(100.0, GlobalRandomizer), zone, null)
    }

}

object CentroidAssigner: DetermineLocationInZone {
    override fun getLocation(zone: Zone): Location {
        return Location(zone.centroid.coordinate, zone, null)
    }
}

