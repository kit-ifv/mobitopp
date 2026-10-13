package edu.kit.ifv.domain.synthesis.behavior.activitygeneration

import edu.kit.ifv.domain.shared.datastructure.schedule.action.RawActivity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.StationaryAction
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson
import kotlinx.datetime.DayOfWeek
import kotlin.random.Random

/**
 * Generates an activity schedule for a week by drawing a random day schedule for each day of the week.
 */
class DayComposingScheduleGenerator(
    private val random: Random,
    val dayPrefabs: Map<DayOfWeek, List<ReadonlyActivitySchedule>>,
) : GenerateActivitySchedule<MinimumHouseholdAttributes, MinimumPersonAttributes> {
    override fun generate(person: SurveyPerson<MinimumPersonAttributes>): PreliminaryActivitySchedule {
        return DayOfWeek.entries.map { day -> getRandomActivityPlanFor(day) }.flatten().toPreliminaryActivitySchedule()

    }

    fun getRandomActivityPlanFor(day: DayOfWeek): ReadonlyActivitySchedule {
        val potentialCandidates = dayPrefabs[day] ?: throw NoSuchElementException(
            "There is nothing registered for " +
                    "this day"
        )
        return potentialCandidates.randomOrNull(random = random) ?: listOf()
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
                    type = it.type
                )
            }.toMutableList(),
        )
    }


}