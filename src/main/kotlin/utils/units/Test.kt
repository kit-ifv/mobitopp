package utils.units
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main() {

    val d = 1.toDuration(DurationUnit.SECONDS)
    val d2 = 2.toDuration(DurationUnit.SECONDS)
    println(d + d2)
    println(d * 0.5)
    println(1222 * d)
    println(d + 2.toDuration(DurationUnit.MINUTES))
    println(d.plusMinutes(2))
}

fun Duration.plusMinutes(minutes: Int): Duration{
    return this.plus(minutes.toDuration(DurationUnit.MINUTES))
}

operator fun Int.times(duration: Duration): Duration {
    return duration * this
}