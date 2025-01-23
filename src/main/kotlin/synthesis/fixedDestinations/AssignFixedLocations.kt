package synthesis.fixedDestinations

import domain.data.Zone
import domain.enums.ActivityType
import domain.location.Location
import modeling.discreteChoice.GlobalRandomizer
import synthesis.domain.SynthesisPerson
import synthesis.randomCoordinate

/**
 * Finds a proper location for a given person, and activity type.
 */
fun interface IndividualOldActivityLocator<T> {

    fun find(individual: SynthesisPerson<out T>, activityType: ActivityType): Location
}

/**
 * Sometimes it is smarter to assign locations for an activity type in groups, rather than individually. For
 * example, when assigning 300 students to three schools which each can house 100 students, it may not be
 * prudent to just randomly assign each student individually, but to consider all students and perform a matching.
 */
fun interface OldGroupActivityLocator<T> {
    fun find(
        group: Collection<SynthesisPerson<out T>>,
        activityType: ActivityType
    ): Collection<Pair<SynthesisPerson<out T>, Location>>
}

/**
 * The most trivial implementation of a group locator is to simply use an individual assignment strategy and use said
 * strategy to generate locations.
 */

class TrivialGroupActivityLocator<T>(val original: IndividualOldActivityLocator<T>) : OldGroupActivityLocator<T> {
    override fun find(
        group: Collection<SynthesisPerson<out T>>,
        activityType: ActivityType
    ): Collection<Pair<SynthesisPerson<out T>, Location>> {
        return group.map { it to original.find(it, activityType) }
    }
}


/**
 * A functional interface to provide a location from the knowledge of a zone. In the synthesis this step is necessary to
 * determine the correct location of synthesis households, which know only the zone in which they are located
 */
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

/**
 * Assign the location based on the centroid coordinate.
 */
object CentroidAssigner : DetermineLocationInZone {
    override fun getLocation(zone: Zone): Location {
        return Location(zone.centroid.coordinate, zone, null)
    }
}


