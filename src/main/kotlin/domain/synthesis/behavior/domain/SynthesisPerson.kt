package domain.synthesis.behavior.domain

import domain.shared.enums.ActivityType
import domain.shared.location.Location
import domain.synthesis.behavior.GLOBAL_PERSON_ID_GENERATOR
import domain.synthesis.behavior.activityGeneration.PreliminaryActivitySchedule
import domain.synthesis.data.Sex

// TODO I don't think that age and sex are mandatory attributes, and should be in the info block. Debate with Jelle?
class SynthesisPerson<T>(
    val household: SynthesisHousehold<T>,
    val age: Int,
    val sex: Sex,
    val info: T,
    val personId: Int
) {

    constructor(household: SynthesisHousehold<T>, age: Int, sex: Sex, info: T) : this(
        household,
        age,
        sex,
        info,
        GLOBAL_PERSON_ID_GENERATOR
    )

    constructor(household: SynthesisHousehold<T>, info: T) : this(household, 0, Sex.MALE, info)

    val homeLocation get() = household.location
    var hasTransitPass = false
    var plannedActivities: PreliminaryActivitySchedule = PreliminaryActivitySchedule.Companion.STAY_AT_HOME
    val fixedDestinations: MutableMap<ActivityType, Location> = mutableMapOf()
}
