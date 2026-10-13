package edu.kit.ifv.domain.synthesis.behavior.activitygeneration.childs

import edu.kit.ifv.domain.synthesis.behavior.activitygeneration.DayComposingScheduleGenerator
import kotlinx.datetime.DayOfWeek
import kotlin.random.Random

object Spawner {

    fun  fromListOfPotentialPlans(
        random: Random,
        potentialPlans: List<ChildActivitySchedule>,
        filter: (ChildActivitySchedule) -> Boolean,
    ): DayComposingScheduleGenerator {
        val ageMap = potentialPlans.filter(filter).groupBy { it.dayType }.mapValues { (k, v) ->
            v.map { it.activityPlan }
        }
        val lookup = DayTypeMap(ageMap)
        val target = DayOfWeek.entries.associateWith { lookup.getValue(it) }
        return DayComposingScheduleGenerator(random, target)
    }

}