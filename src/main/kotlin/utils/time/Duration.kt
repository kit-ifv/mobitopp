package utils.time

import java.security.cert.CRLException

interface RelativeTime : Comparable<RelativeTime>{
    operator fun plus(other: RelativeTime): RelativeTime
    operator fun minus(other: RelativeTime): RelativeTime
    operator fun unaryMinus(): RelativeTime

    fun seconds(): Int

}

class Duration(private val seconds: Int) : RelativeTime {

    override fun plus(other: RelativeTime): Duration {
        return Duration(seconds + other.seconds())
    }

    override fun minus(other: RelativeTime): Duration {
        return Duration(seconds - other.seconds())
    }

    override fun unaryMinus(): Duration {
        return Duration(-seconds)
    }

    override fun seconds(): Int {
        return seconds
    }

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: RelativeTime): Int {
        TODO()
    }

}