package synthesis

import domain.data.Sex
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import utils.Decodable
import utils.Encodable
import kotlin.time.Duration.Companion.hours

interface GenerateActivitySchedule {
    fun generate(person: SurveyPerson): ActivitySchedule
}

fun List<SynthesisHouseholdBuilder>.generateSchedules( generator: GenerateActivitySchedule): Map<SynthesisHouseholdBuilder, List<ActivitySchedule>> {
    return associateWith {household ->
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

class TrivialActivityScheduleGeneration(private val init: Decodable<ActivityType> = LegacyActivityType.Companion) :
    GenerateActivitySchedule {


    private val schoolSchedule: ActivitySchedule
        get() = ActivitySchedule(init) {
            home(0.hours, 7.hours)
            education(7.5.hours, 14.hours)
            leisure(15.hours, 17.hours)
            home(18.hours, 28.hours)
        }

    private val workingSchedule
        get() = ActivitySchedule(init) {
            home(0.hours, 6.hours)
            work(7.hours, 12.hours)
            leisure(12.5.hours, 13.5.hours)
            work(14.hours, 18.hours)
            home(19.hours, 28.hours)
        }

    private val homekeeperSchedule
        get() = ActivitySchedule(init) {
            home(0.hours, 10.hours)
            shopping(11.hours, 13.5.hours)
            home(14.hours, 16.hours)
            leisure(17.hours, 18.hours)
            home(19.hours, 30.hours)
        }

    private val seniorSchedule
        get() = ActivitySchedule(init) {
            home(0.hours, 5.hours)
            leisure(6.hours, 8.hours)
            home(12.hours, 30.hours)
        }

    override fun generate(person: SurveyPerson): ActivitySchedule {
        if (person.age <= 18) return schoolSchedule
        if (person.age <= 40) return workingSchedule
        if (person.age <= 65) return homekeeperSchedule
        return seniorSchedule

    }

}
