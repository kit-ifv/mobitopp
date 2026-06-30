package edu.kit.ifv.domain.shared.behavior
import edu.kit.ifv.domain.shared.enums.ActivityType

data class ChoiceModelPurposes constructor(
    val home: ActivityType,
    val work: ActivityType,
    val business: ActivityType,
    val shopping: ActivityType,
    val privateBusiness: ActivityType,
    val service: ActivityType,
    val privateVisit: ActivityType,
    val leisureTravel: ActivityType,
    @Deprecated("Is this field even used?")
    val businessTravel: ActivityType,
    val education: ActivityType,
    val leisure: ActivityType,
    val undefined: ActivityType,

    val allActivityTypes: Set<ActivityType>,
    val leisureTypes: Set<ActivityType>,
    val educationTypes: Set<ActivityType>,
    val shoppingTypes: Set<ActivityType>,
    val businessTypes: Set<ActivityType>,
)
