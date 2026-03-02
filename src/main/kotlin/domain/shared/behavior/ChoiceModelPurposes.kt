package domain.shared.behavior

import domain.shared.enums.ActivityType

data class ChoiceModelPurposes constructor(
    val home: ActivityType,
    val work: ActivityType,
    val business: ActivityType,
    val shopping: ActivityType,
    val privateBusiness: ActivityType,
    val service: ActivityType,
    val privateVisit: ActivityType,
    val leisureTravel: ActivityType,
    val businessTravel: ActivityType,
    val education: ActivityType,
    val leisure: ActivityType,
    val undefined: ActivityType,

    val allActivityTypes: Set<ActivityType>,
    val typesWithAttractivity: Set<ActivityType>,
    val leisureTypes: Set<ActivityType>,
    val educationTypes: Set<ActivityType>,
    val shoppingTypes: Set<ActivityType>,
    val businessTypes: Set<ActivityType>,
)
