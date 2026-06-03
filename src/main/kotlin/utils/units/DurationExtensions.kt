package utils.units

import edu.kit.ifv.units.Currency
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun max(first: Duration, second: Duration): Duration = if (first >= second) first else second

fun max(first: Currency, second: Currency): Currency = if (first >= second) first else second

fun min(first: Duration, second: Duration): Duration = if (first <= second) first else second

operator fun Duration.rem(other: Duration): Duration = (this.inWholeSeconds % other.inWholeSeconds).toDuration(
    DurationUnit.SECONDS,
)

fun Duration.floorRem(other: Duration): Duration {
    val result = this % other
    return if (result < Duration.ZERO) result + other else result
}
