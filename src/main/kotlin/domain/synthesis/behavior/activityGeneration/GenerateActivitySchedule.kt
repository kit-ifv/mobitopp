package domain.synthesis.behavior.activityGeneration

import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SurveyPerson
import utils.Decodable
import kotlin.time.Duration.Companion.hours

fun interface GenerateActivitySchedule<S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> : GenerateHouseholdActivitySchedule<S, T> {
    fun generate(person: SurveyPerson<T>): PreliminaryActivitySchedule
    override fun generate(household: ISurveyHousehold<S, T>): List<PreliminaryActivitySchedule> {
        return household.members.map { generate(it) }
    }
}
fun interface GenerateHouseholdActivitySchedule<in S : MinimumHouseholdAttributes, in T : MinimumPersonAttributes> {
    fun generate(household: ISurveyHousehold<S, T>): List<PreliminaryActivitySchedule>
//    fun generate(household: SynthesisHousehold<out T>): Map<SynthesisPerson<out T>, PreliminaryActivitySchedule>
}
class TrivialActivityGeneration(private val init: Decodable<ActivityType> = LegacyActivityType.Companion) :
    GenerateActivitySchedule<MinimumHouseholdAttributes, MinimumPersonAttributes> {
    override fun generate(person: SurveyPerson<*>): PreliminaryActivitySchedule {
        return PreliminaryActivitySchedule(init) {
            home(0.hours, 10.hours)
            shopping(11.hours, 13.5.hours)
            work(14.hours, 16.hours)
            leisure(17.hours, 18.hours)
            home(19.hours, 30.hours)
        }
    }
}
