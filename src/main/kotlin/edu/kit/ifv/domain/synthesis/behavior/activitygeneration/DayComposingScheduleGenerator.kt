package edu.kit.ifv.domain.synthesis.behavior.activitygeneration

import edu.kit.ifv.domain.shared.datastructure.schedule.action.RawActivity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.StationaryAction
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson
import kotlinx.datetime.DayOfWeek
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * Generates an activity schedule for a week by drawing a random day schedule for each day of the week.
 */
class DayComposingScheduleGenerator(
    private val random: Random,
    val dailyScheduleCandidates: Map<DayOfWeek, List<ReadonlyActivitySchedule>>,
) : GenerateActivitySchedule<MinimumHouseholdAttributes, MinimumPersonAttributes> {
    override fun generate(person: SurveyPerson<MinimumPersonAttributes>): PreliminaryActivitySchedule {
        val flatten: List<StationaryAction> = DayOfWeek.entries.map { day ->
            getRandomActivityPlanFor(day)
        }.flatten()
        return flatten.toPreliminaryActivitySchedule()
    }

    fun getRandomActivityPlanFor(day: DayOfWeek): ReadonlyActivitySchedule {
        val potentialCandidates = dailyScheduleCandidates[day] ?: throw NoSuchElementException(
            "There is nothing registered for " +
                "this day",
        )
        val dayschedule: ReadonlyActivitySchedule = potentialCandidates.randomOrNull(random = random) ?: listOf()
        val dayOffset: Duration = day.ordinal.days
        return dayschedule.map {
            RawActivity(
                location = it.location,
                startTime = it.startTime + dayOffset,
                endTime = it.endTime + dayOffset,
                earliestStartTime = it.earliestStartTime + dayOffset,
                latestEndTime = it.latestEndTime + dayOffset,
                type = it.type,
            )
        }
    }

    private fun Collection<StationaryAction>.toPreliminaryActivitySchedule(): PreliminaryActivitySchedule {
        if (isEmpty()) return PreliminaryActivitySchedule.Companion.STAY_AT_HOME
        return PreliminaryActivitySchedule(
            map {
                RawActivity(
                    location = it.location,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    earliestStartTime = it.earliestStartTime,
                    latestEndTime = it.latestEndTime,
                    type = it.type,
                )
            }.toMutableList(),
        )
    }
}
