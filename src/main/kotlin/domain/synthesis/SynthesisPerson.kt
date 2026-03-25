package domain.synthesis

import domain.shared.enums.ActivityType
import domain.shared.location.StandardLocation
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.behavior.activityGeneration.PreliminaryActivitySchedule
import domain.synthesis.data.Sex

/**
 * The mutable object that holds the infos of the person.
 */
class SynthesisPerson<S: MinimumHouseholdAttributes, T : MinimumPersonAttributes> constructor(
    private val household: SynthesisHousehold<S, T>,
    override val age: Int,
    override val sex: Sex,
    override val attributes: T,
    override val personId: Int = GLOBAL_PERSON_ID_GENERATOR,
) : SurveyPerson<T> {
    override val homeLocation: StandardLocation
        get() = household.attributes.location

    val householdID get() = household.id

    val hasAccessToCar get() = household.amountOfCars > 0



//    val homeLocation get() =
    var hasTransitPass = false
    var plannedActivities: PreliminaryActivitySchedule = PreliminaryActivitySchedule.Companion.STAY_AT_HOME
    val fixedDestinations: MutableMap<ActivityType, StandardLocation> = mutableMapOf()

    private val sharingMemberships: MutableMap<String, Boolean> = mutableMapOf()

    fun getSharingMemberships(): Map<String, Boolean> = sharingMemberships

    fun addMembership(name: String) {
        sharingMemberships[name] = true
    }

    companion object {
        var GLOBAL_PERSON_ID_GENERATOR = 0
            get() = field.also { field++ }
            private set
    }
}