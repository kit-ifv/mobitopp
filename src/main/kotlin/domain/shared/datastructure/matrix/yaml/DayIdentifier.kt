package domain.shared.datastructure.matrix.yaml

import java.time.DayOfWeek
import java.util.Locale

enum class DayIdentifier {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday,
    Weekday,
    Everyday;

    /*
     * The level determines which identifiers can overlay other identifiers. Higher level overrides lower level
     */
    fun getDays(): Pair<List<DayOfWeek>, Int> {
        val weekdays = when (this) {
            Monday -> listOf(DayOfWeek.MONDAY)
            Tuesday -> listOf(DayOfWeek.TUESDAY)
            Wednesday -> listOf(DayOfWeek.WEDNESDAY)
            Thursday -> listOf(DayOfWeek.THURSDAY)
            Friday -> listOf(DayOfWeek.FRIDAY)
            Saturday -> listOf(DayOfWeek.SATURDAY)
            Sunday -> listOf(DayOfWeek.SUNDAY)
            Weekday -> listOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
            )

            Everyday -> DayOfWeek.entries
        }

        val level = when (this) {
            Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday -> 2
            Weekday -> 1
            Everyday -> 0
        }

        return Pair(weekdays, level)
    }

    companion object {
        fun fromString(value: String): DayIdentifier {
            return when (value.lowercase(Locale.getDefault())) {
                "monday" -> Monday
                "tuesday" -> Tuesday
                "wednesday" -> Wednesday
                "thursday" -> Thursday
                "friday" -> Friday
                "saturday" -> Saturday
                "sunday" -> Sunday
                "weekday" -> Weekday
                "everyday" -> Everyday
                else -> throw IllegalArgumentException("Unknown day: $value")
            }
        }
    }
}