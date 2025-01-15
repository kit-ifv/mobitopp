package synthesis.domain

import domain.enums.ActivityType
import domain.location.Location
import synthesis.GLOBAL_PERSON_ID_GENERATOR
import synthesis.activityGeneration.PreliminaryActivitySchedule

class SynthesisPerson<T>(
    val household: SynthesisHousehold<T>,
    val info: T,
    val personId: Int
) {

    constructor(household: SynthesisHousehold<T>, person: T) : this(household, person, GLOBAL_PERSON_ID_GENERATOR)
    val homeLocation get() = household.location
    var hasTransitPass = false
    var plannedActivities: PreliminaryActivitySchedule = PreliminaryActivitySchedule.STAY_AT_HOME
    val fixedDestinations: MutableMap<ActivityType, Location> = mutableMapOf()
}
