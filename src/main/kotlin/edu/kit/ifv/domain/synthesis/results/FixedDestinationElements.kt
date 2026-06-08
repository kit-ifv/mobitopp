package edu.kit.ifv.domain.synthesis.results
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.SynthesisPerson

data class FixedDestinationElements(
    val person: SynthesisPerson<*, *>,
    val activityType: ActivityType,
    val location: StandardLocation,
)
