package utils.units

import edu.kit.ifv.units.Currency
import kotlin.time.Duration

fun max(first: Duration, second: Duration): Duration {
    return if (first >= second) first else second
}

fun max(first: Currency, second: Currency): Currency {
    return if (first >= second) first else second
}

fun min(first: Duration, second: Duration): Duration {
    return if (first <= second) first else second
}
