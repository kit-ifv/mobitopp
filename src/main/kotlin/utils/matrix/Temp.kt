@file: Suppress("MagicNumber")

package utils.matrix

import CodePlan
import Encodable

enum class TempWeekday : Encodable {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    override fun encode(): Int {
        return when (this) {
            MONDAY -> 0
            TUESDAY -> 1
            WEDNESDAY -> 2
            THURSDAY -> 3
            FRIDAY -> 4
            SATURDAY -> 5
            SUNDAY -> 6
        }
    }

    companion object : CodePlan<TempWeekday> {
        override fun decode(s: String): TempWeekday {
            return when (s.lowercase()) {
                "monday", "montag", "mo", "mo." -> MONDAY
                "tuesday", "dienstag", "di", "tu." -> TUESDAY
                "wednesday", "mittwoch", "mi", "we." -> WEDNESDAY
                "thursday", "donnerstag", "do", "th." -> THURSDAY
                "friday", "freitag", "fr", "fr." -> FRIDAY
                "saturday", "samstag", "sa", "sa." -> SATURDAY
                "sunday", "sonntag", "so", "su." -> SUNDAY
                else -> throw IllegalArgumentException("Invalid weekday string: $s")
            }
        }

        override fun decode(i: Int): TempWeekday {
            return when (i) {
                0 -> MONDAY
                1 -> TUESDAY
                2 -> WEDNESDAY
                3 -> THURSDAY
                4 -> FRIDAY
                5 -> SATURDAY
                6 -> SUNDAY
                else -> throw IllegalArgumentException("Invalid weekday value: $i")
            }
        }
    }
}

interface TempAbsoluteTime : Comparable<TempAbsoluteTime> {
    val weekday: TempWeekday
    val hour: Int
    val minute: Int
    val second: Int
    val week: Int

    override fun compareTo(other: TempAbsoluteTime): Int {
        return compareValuesBy(
            this,
            other,
            TempAbsoluteTime::week,
            TempAbsoluteTime::weekday,
            TempAbsoluteTime::hour,
            TempAbsoluteTime::minute,
            TempAbsoluteTime::second
        )
    }
}

data class MyTempAbsoluteTime(
    override var weekday: TempWeekday,
    override var hour: Int,
    override var minute: Int,
    override var second: Int,
    override var week: Int
) : TempAbsoluteTime {
    init {
        minute += second / 60
        second %= 60
        if (second < 0) {
            second += 60
            minute -= 1
        }

        hour += minute / 60
        minute %= 60
        if (minute < 0) {
            minute += 60
            hour -= 1
        }

        var weekdayNum = weekday.encode()

        weekdayNum += hour / 24
        hour %= 24
        if (hour < 0) {
            hour += 24
            weekdayNum -= 1
        }

        week += weekdayNum / 7
        weekdayNum %= 7
        if (weekdayNum < 0) {
            weekdayNum += 7
            week -= 1
        }

        weekday = TempWeekday.decode(weekdayNum)
    }
}
