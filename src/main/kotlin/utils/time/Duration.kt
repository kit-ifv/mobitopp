package utils.time

/**
 * A representation of the SI unit time
 */
interface RelativeTime : Comparable<RelativeTime>{
    operator fun plus(other: RelativeTime): RelativeTime
    operator fun minus(other: RelativeTime): RelativeTime
    operator fun times(scalar: Double): RelativeTime
    operator fun div(scalar: Double): RelativeTime
    operator fun unaryMinus(): RelativeTime

    fun seconds(): Double

}