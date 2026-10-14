package edu.kit.ifv.domain.synthesis.behavior.activitygeneration.children

import kotlinx.datetime.DayOfWeek

enum class DayType {
    MO,
    DI_MI_DO,
    FR,
    SA,
    SO,
    ;

    companion object {
        @Suppress("MagicNumber")
        fun decode(code: Int): DayType = when (code) {
            1 -> MO
            2 -> DI_MI_DO
            3 -> FR
            4 -> SA
            5 -> SO
            else -> throw IllegalArgumentException("Invalid DayType code $code")
        }

        fun from(dayOfWeek: DayOfWeek): DayType = when (dayOfWeek) {
            DayOfWeek.MONDAY -> MO

            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            -> DI_MI_DO

            DayOfWeek.FRIDAY -> FR

            DayOfWeek.SATURDAY -> SA

            DayOfWeek.SUNDAY -> SO
        }
    }
}
