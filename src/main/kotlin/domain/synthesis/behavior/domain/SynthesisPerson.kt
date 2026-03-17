package domain.synthesis.behavior.domain

import domain.shared.enums.ActivityType
import domain.shared.location.StandardLocation
import domain.synthesis.behavior.LocatedHousehold
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.behavior.activityGeneration.PreliminaryActivitySchedule
import domain.synthesis.data.Sex

// TODO I don't think that age and sex are mandatory attributes, and should be in the info block. Debate with Jelle?
class SynthesisPerson<out T>(
    var homeLocation: StandardLocation,
    override val age: Int,
    override val sex: Sex,
    override val information: T,
    override val personId: Int = GLOBAL_PERSON_ID_GENERATOR,
) : SurveyPerson<T> {

    constructor(household: LocatedHousehold<*, T>, age: Int, sex: Sex, info: T) : this(
       household.location,
        age,
        sex,
        info,
        GLOBAL_PERSON_ID_GENERATOR
    )

    constructor(household: SynthesisHousehold<*, T>, info: T) : this(household, 0, Sex.MALE, info)

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

data class SynthesisPersonInfo<T>(
    var age: Int = 0,
    var sex: Sex = Sex.UNKNOWN,
    var personId: Int = -1,
    var information: T? = null

) {
    constructor(person: SynthesisPerson<T>): this(person.age, person.sex, person.personId, person.information)
}
