package edu.kit.ifv.domain.synthesis.behavior.activitygeneration.children

import edu.kit.ifv.domain.synthesis.behavior.activitygeneration.ReadonlyActivitySchedule

data class ChildActivitySchedule(
    val ageGroup: AgeGroup,
    val dayType: DayType,
    val activityPlan: ReadonlyActivitySchedule,
)
