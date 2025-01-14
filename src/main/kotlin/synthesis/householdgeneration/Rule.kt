package synthesis.householdgeneration

import synthesis.SurveyHousehold
import synthesis.SurveyInfo
import synthesis.toInt
import kotlin.math.abs

/**
 * A rule for IPU requires a
 */
interface Rule<T> {
    val target: Int
    val description: String
    fun check(surveyHousehold: SurveyHousehold<out T>): Int

    fun appliesTo(surveyHousehold: SurveyHousehold<out T>): Boolean = check(surveyHousehold) != 0

    fun verify(output: Collection<SurveyHousehold<out T>>): Double {
        return target.toDouble() - output.sumOf { check(it) }
    }

    fun filter(target: Collection<SurveyHousehold<out T>>): List<SurveyHousehold<out T>> {
        return target.filter { appliesTo(it) }
    }
}

fun interface CountRule<T> {
    fun matches(surveyHousehold: SurveyHousehold<out T>): Int
}

fun interface CheckRule<T> {
    fun matches(surveyHousehold: SurveyHousehold<out T>): Boolean
}

class ZoneRule<T>(override val description: String, override val target: Int, val matcher: CountRule<T>) : Rule<T> {

    override fun check(surveyHousehold: SurveyHousehold<out T>): Int {
        return matcher.matches(surveyHousehold)
    }

    override fun toString(): String {
        return "[$description] expected = $target"
    }
}

class ZoneCheckRule<T>(override val description: String, override val target: Int, val matcher: CheckRule<T>) : Rule<T> {
    override fun check(surveyHousehold: SurveyHousehold<out T>): Int {
        return matcher.matches(surveyHousehold).toInt()
    }

    override fun toString(): String {
        return "[$description] expected = $target"
    }
}

