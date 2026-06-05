package edu.kit.ifv.domain.synthesis
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.enums.person.Sex
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson
import edu.kit.ifv.domain.synthesis.behavior.activitygeneration.PreliminaryActivitySchedule

/**
 * The mutable object that holds the infos of the person.
 */
class SynthesisPerson<S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> constructor(
    val household: SynthesisHousehold<S, T>,
    override val age: Int,
    override val sex: Sex,
    override val attributes: T,
    override val personId: Int = GLOBAL_PERSON_ID_GENERATOR,
) : SurveyPerson<T> {
    override val homeLocation: StandardLocation
        get() = household.attributes.location

    val householdID get() = household.id

    //    val homeLocation get() =
    var hasTransitPass = false
    var plannedActivities: PreliminaryActivitySchedule = PreliminaryActivitySchedule.STAY_AT_HOME
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
