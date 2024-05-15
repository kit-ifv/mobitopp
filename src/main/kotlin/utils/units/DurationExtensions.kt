package utils.units

import kotlin.time.Duration

fun max(first: Duration, second: Duration): Duration {
    return if (first >= second) first else second
}

fun min(first: Duration, second: Duration): Duration {
    return if (first <= second) first else second
}