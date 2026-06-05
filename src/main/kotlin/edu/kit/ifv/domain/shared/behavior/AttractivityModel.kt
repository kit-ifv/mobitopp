package edu.kit.ifv.domain.shared.behavior
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.zone.MaximalZone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import kotlin.math.abs

// TODO Debate with Jelle, There is a more generalized version of attractiveness, which takes in a location, rather than
//  a zoneID
interface AttractivenessModel {
    fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Attractiveness
    fun isAttractive(zone: ZoneId, activityType: ActivityType): Boolean =
        attractivenessFor(zone, activityType).value > .0

    // TODO Extract work/private visit, as they are only required to compute parkdruck/parkingpressure
    // Create new interface ParkingPressureModel and an implementation that wraps attractiveness model and knows about work/private visit
    val work: ActivityType
    val privateVisit: ActivityType
}

fun AttractivenessModel.sumAttractiveness(zone: ZoneId, vararg activityTypes: ActivityType): Double =
    activityTypes.sumOf {
        attractivenessFor(zone, it).value
    }

@Suppress("MagicNumber")
fun AttractivenessModel.parkingPressure(target: MaximalZone): Double {
    val attractiveness = sumAttractiveness(target.id, work, privateVisit)
    if (target.parkingPlaces == 0) {
        return if (abs(attractiveness) < 1e-6) 0.0 else 999.0
    }
    return attractiveness / target.parkingPlaces
}

