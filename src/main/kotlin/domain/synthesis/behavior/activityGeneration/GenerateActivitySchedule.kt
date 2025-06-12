package domain.synthesis.behavior.activityGeneration

import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.synthesis.behavior.domain.SynthesisPerson
import utils.Decodable
import kotlin.time.Duration.Companion.hours

fun interface GenerateActivitySchedule<T> {
    fun generate(person: SynthesisPerson<out T>): PreliminaryActivitySchedule
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
