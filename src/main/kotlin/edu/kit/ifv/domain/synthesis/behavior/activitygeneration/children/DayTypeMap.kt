package edu.kit.ifv.domain.synthesis.behavior.activitygeneration.children

import kotlinx.datetime.DayOfWeek

class DayTypeMap<V>(private val values: Map<DayType, V>) {
    operator fun get(day: DayOfWeek): V? = values[DayType.from(day)]

    fun getValue(day: DayOfWeek): V = values.getValue(DayType.from(day))
}
