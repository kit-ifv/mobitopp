package domain.synthesis.results

import domain.shared.behavior.Attractiveness
import domain.shared.enums.ActivityType
import domain.shared.location.StandardLocation
import domain.shared.location.ZonedRoadAccessLocationRecord

data class OpportunityOutput constructor(
    val location: ZonedRoadAccessLocationRecord,
    val attractiveness: Attractiveness,
    val activityType: ActivityType,
) {
    constructor(location: StandardLocation, attractiveness: Attractiveness, activityType: ActivityType) : this(
        location.toRecord(),
        attractiveness,
        activityType,
    )
}
