package domain.synthesis.results

import domain.shared.enums.ActivityType
import domain.shared.location.StandardLocation
import domain.synthesis.SynthesisPerson

data class FixedDestinationElements(
    val person: SynthesisPerson<*, *>,
    val activityType: ActivityType,
    val location: StandardLocation,
)
