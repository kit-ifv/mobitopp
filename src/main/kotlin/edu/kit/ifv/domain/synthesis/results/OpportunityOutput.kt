package edu.kit.ifv.domain.synthesis.results
import edu.kit.ifv.domain.shared.behavior.Attractiveness
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.ZonedRoadAccessLocationRecord

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
