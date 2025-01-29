package synthesis.activityGeneration

import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import synthesis.SurveyInfo
import synthesis.domain.SynthesisHousehold
import synthesis.domain.SynthesisPerson
import utils.Decodable
import kotlin.time.Duration.Companion.hours

fun interface GenerateActivitySchedule<T> {
    fun generate(person: SynthesisPerson<out T>): PreliminaryActivitySchedule
}

fun List<SynthesisHousehold<Any>>.generateSchedules(generator: GenerateActivitySchedule<Any>): Map<SynthesisHousehold<Any>, List<PreliminaryActivitySchedule>> {
    return associateWith { household ->
        household.members.map { person ->
            val schedule = generator.generate(person)
            schedule.forEach { act ->
                if (act.type.encode() == 0) {
                    act.location = household.location
                }
            }
            schedule
        }
    }
}

class SimpleActivityGeneration(private val init: Decodable<ActivityType> = LegacyActivityType.Companion) :
    GenerateActivitySchedule<SurveyInfo> {

    private val schoolSchedule: PreliminaryActivitySchedule
        get() = PreliminaryActivitySchedule(init) {
            home(0.hours, 7.hours)
            education(7.5.hours, 14.hours)
            leisure(15.hours, 17.hours)
            home(18.hours, 28.hours)
        }

    private val workingSchedule
        get() = PreliminaryActivitySchedule(init) {
            home(0.hours, 6.hours)
            work(7.hours, 12.hours)
            leisure(12.5.hours, 13.5.hours)
            work(14.hours, 18.hours)
            home(19.hours, 28.hours)
        }

    private val homekeeperSchedule
        get() = PreliminaryActivitySchedule(init) {
            home(0.hours, 10.hours)
            shopping(11.hours, 13.5.hours)
            home(14.hours, 16.hours)
            leisure(17.hours, 18.hours)
            home(19.hours, 30.hours)
        }

    private val seniorSchedule
        get() = PreliminaryActivitySchedule(init) {
            home(0.hours, 5.hours)
            leisure(6.hours, 8.hours)
            home(12.hours, 30.hours)
        }

    override fun generate(person: SynthesisPerson<out SurveyInfo>): PreliminaryActivitySchedule {
        if (person.age <= 18) return schoolSchedule
        if (person.age <= 40) return workingSchedule
        if (person.age <= 65) return homekeeperSchedule
        return seniorSchedule
    }
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
