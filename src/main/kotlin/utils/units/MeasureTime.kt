package utils.units

import kotlin.time.measureTime

fun <R> logTime(label: String, block: () -> R): R {
    val result: R

    measureTime {
        result = block()
    }.also {
        println(
            "$label took ${it.inWholeHours} h " +
                "${it.inWholeMinutes % MINUTES_PER_HOUR} m " +
                "${it.inWholeSeconds % SECONDS_PER_MINUTE} s"
        )
    }

    return result
}
