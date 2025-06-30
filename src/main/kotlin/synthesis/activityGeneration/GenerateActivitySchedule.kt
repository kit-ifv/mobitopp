package synthesis.activityGeneration

import domain.enums.ActivityType
import synthesis.domain.SynthesisHousehold
import synthesis.domain.SynthesisPerson
import usecases.LegacyActivityType
import utils.Decodable
import kotlin.time.Duration.Companion.hours

fun interface GenerateActivitySchedule<T>: GenerateHouseholdActivitySchedule<T> {
    fun generate(person: SynthesisPerson<out T>): PreliminaryActivitySchedule
    override fun generate(household: SynthesisHousehold<out T>): Map<SynthesisPerson<out T>, PreliminaryActivitySchedule> {
        return household.members.associateWith { generate(it) }
    }
}
fun interface GenerateHouseholdActivitySchedule<T> {
    fun generate(household: SynthesisHousehold<out T>): Map<SynthesisPerson<out T>, PreliminaryActivitySchedule>
}
class TrivialActivityGeneration(private val init: Decodable<ActivityType> = LegacyActivityType.Companion) :
    GenerateActivitySchedule<Any> {
    override fun generate(person: SynthesisPerson<out Any>): PreliminaryActivitySchedule {
        return PreliminaryActivitySchedule(init) {
            home(0.hours, 10.hours)
            shopping(11.hours, 13.5.hours)
            work(14.hours, 16.hours)
            leisure(17.hours, 18.hours)
            home(19.hours, 30.hours)
        }
    }
}
