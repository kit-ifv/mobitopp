package domain.shared.datastructure.schedule

import domain.shared.enums.ActivityType
import domain.shared.location.StandardLocation
import utils.units.sinceStart
import kotlin.collections.fold
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class ActConfig {
    lateinit var location: StandardLocation
    var startTime: Number = 0
        get
        set(value) {
            field = value
            earliestStartTime = (value.toInt() / 24).toDouble()
            latestEndTime = (value.toInt() / 24).toDouble()
        }
    lateinit var duration: Number

    fun setStart(duration: Duration) {
        startTime = duration.toDouble(DurationUnit.HOURS)
    }

    fun setEnd(end: Duration) {
        duration = end.toDouble(DurationUnit.HOURS) - startTime.toDouble()
    }

    fun setStart(string: String) {
        fromString(string, ::setStart)
    }

    fun setEnd(string: String) {
        fromString(string, ::setEnd)
    }

    private fun fromString(text: String, lambda: (Duration) -> Unit) {
        val map = text.split(" ").map { it.toSuffixDuration() }
        val newDuration = map.fold(Duration.ZERO) { a, b ->
            a + b
        }
        lambda(newDuration)
    }

    private fun String.toSuffixDuration(): Duration {
        val suffix = last()
        val remainder = dropLast(1).toInt()
        return when (suffix) {
            'd' -> remainder.days
            'h' -> remainder.hours
            'm' -> remainder.minutes
            's' -> remainder.seconds
            else -> throw IllegalArgumentException()
        }
    }

    var activityType: ActivityType = ActivityType.Companion.UNKNOWN
    var earliestStartTime: Number = startTime.toInt() / 24
    var latestEndTime: Number = startTime.toInt() / 24 + 24
    lateinit var location2: StandardLocation

    fun toActivity(): Activity {
        return Activity.fromDuration(
            location,
            startTime.toDouble().hours.sinceStart,
            duration.toDouble().hours,
            earliestStartTime.toDouble().hours.sinceStart,
            latestEndTime.toDouble().hours.sinceStart,
            type = activityType
        )
    }
}
