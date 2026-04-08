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
    @Deprecated("Is this field even used?")
    val businessTravel: ActivityType,
    val education: ActivityType,
    val leisure: ActivityType,
    val undefined: ActivityType,

    val allActivityTypes: Set<ActivityType>,
    // TODO remove this field, all it does is obscure implicitly assumed behavior: Work & Home do not need an attractiveness
    //   this has been true for all projects up to this date, but not an assumption that i would like to carry into the
    //   future at all.
    @Deprecated(
        "That is not up to choice model purposes, but the attractiveness model, using it for verification only is bad"
    )
    val typesWithAttractivity: Set<ActivityType>,
    val leisureTypes: Set<ActivityType>,
    val educationTypes: Set<ActivityType>,
    val shoppingTypes: Set<ActivityType>,
    val businessTypes: Set<ActivityType>,
)
