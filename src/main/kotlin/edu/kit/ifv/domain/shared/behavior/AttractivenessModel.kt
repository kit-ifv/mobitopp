package edu.kit.ifv.domain.shared.behavior

import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.zone.ZoneId

fun interface AttractivenessModel {
    fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Attractiveness
    fun isAttractive(zone: ZoneId, activityType: ActivityType): Boolean =
        attractivenessFor(zone, activityType).value > .0
}

fun AttractivenessModel.sumAttractiveness(zone: ZoneId, vararg activityTypes: ActivityType): Double =
    activityTypes.sumOf { attractivenessFor(zone, it).value }
