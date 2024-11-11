package usecases.choicemodels.modechoice

import units.Distance
import units.kilometers
import utils.units.AbsoluteTime
import utils.units.Time
import java.time.DayOfWeek
import kotlin.math.pow

/**
 * This interface encapsulates all functions that may be required by a mode choice utility function call
 * currently assigned to the zone. The distance to the zone and the parking pressure at the zone are attributes
 * that may be evaluated by a utility function.
 *
 * DEBATE: Simulation Time is not an attribute of the zone but rather a standalone
 */
interface ZoneModeParameters {
    val constant: Double
    fun evaluateTime(scope: ModeZoneScope): Double = 0.0
    fun evaluateDistance(scope: ModeZoneScope): Double = 0.0
    fun evaluateParkingPressure(scope: ModeZoneScope): Double = 0.0

    fun evaluate(scope: ModeZoneScope) = constant + toParameters(scope).toDouble()

    fun toParameters(scope: ModeZoneScope): ModeZoneParameterBlock {
        return ModeZoneParameterBlock(
            evaluateTime(scope),
            evaluateDistance(scope),
            evaluateParkingPressure(scope)
        )
    }
}

/**
 * This interface overwrites the base implementation of [ZoneModeParameters.evaluateDistance] so that inheritors are
 * forced to provide their own implementation and cannot accidentally use the default implementation of evaluateDistance = 0.0
 *
 */
interface CustomDistance : ZoneModeParameters {
    override fun evaluateDistance(scope: ModeZoneScope): Double
}

/**
 * This interface provides a standard implementation of evaluating the distance of a trip in the mode choice.
 * The standard implementation adds a parameter if the distance is within the interval of (1km, 2km].
 *
 * @property closeDistance This parameter is added to the utility function if the distance is within the interval (1km, 2km]
 */
interface StandardDistance : ZoneModeParameters {
    val closeDistance: Double

    override fun evaluateDistance(scope: ModeZoneScope): Double {
        return when {
            (scope.distance <= 2.0.kilometers) && (scope.distance > 1.0.kilometers) -> closeDistance
            else -> 0.0
        }
    }
}

/**
 * This interface overwrites the base implementation of [ZoneModeParameters.evaluateTime] so that every inheritor
 * is forced to provide an implementation for [evaluateTime]. This is a convenience class supporting the compiler so
 * that when a developer adds this interface the default implementation of evaluateTime = 0.0 is omitted and causes
 * a compile error rather than using the default implementation.
 *
 */
interface CustomSimulationTime : ZoneModeParameters {
    override fun evaluateTime(scope: ModeZoneScope): Double
}

/**
 * This interface provides a standard implementation to parameterize the parking pressure of a destination zone
 *
 * [baseParkingPressure] * PARKPRESSURE ^ [elasticityParkingPressure] is the formula used to determine the value
 * to add to the utility function.
 *
 * @property baseParkingPressure the base Parameter multiplied with the parking pressure
 * @property elasticityParkingPressure the exponent which is applied to the parking pressure.
 */
interface StandardParkingPressure : ZoneModeParameters {
    val baseParkingPressure: Double
    val elasticityParkingPressure: Double
    override fun evaluateParkingPressure(scope: ModeZoneScope): Double {
        val pressure = baseParkingPressure * scope.parkingPressure.pow(elasticityParkingPressure)
        return pressure
    }
}

/**
 * This interface implements the evaluation of the simulation time based on the fact whether it is evening, before
 * midnight or after midnight as well as the day set
 *  @property evening Parameter used if the simulation time is between 17:00 and <21:00
 *  @property beforeMidnight Parameter used if the simulation time is between 21:00 and 24:00
 *  @property afterMidnight Parameter used if the simulation time is between 0:00 and <5:00
 *
 *  @property fridaysBeforeMidnight Parameter added to [beforeMidnight] if the day is a Friday (and 21:00 - 24:00)
 *  @property fridaysAfterMidnight Parameter added to [afterMidnight] if the days is a Friday (and 0:00 - 5:00)
 *  @property saturdaysBeforeMidnight Parameter added to [beforeMidnight] if the day is a Saturday (and 21:00 - 24:00)
 *  @property saturdaysAfterMidnight Parameter added to [afterMidnight] if the day is a Saturday (and 0:00 - 5:00)
 *  @property sundaysAfterMidnight Parameter added to  [afterMidnight] if the days is a Sunday (and 0:00 - 5:00)
 */
@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
interface StandardSimulationTime : ZoneModeParameters {
    val evening: Double
    val beforeMidnight: Double
    val afterMidnight: Double
    val fridaysBeforeMidnight: Double
    val fridaysAfterMidnight: Double
    val saturdaysBeforeMidnight: Double
    val saturdaysAfterMidnight: Double
    val sundaysAfterMidnight: Double

    override fun evaluateTime(scope: ModeZoneScope): Double {
        val time = scope.time
        return when (time.weekDay) {
            DayOfWeek.FRIDAY -> evaluateFriday(time)
            DayOfWeek.SATURDAY -> evaluateSaturday(time)
            DayOfWeek.SUNDAY -> evaluateSunday(time)
            else -> evaluateDay(time)
        }
    }

    private fun evaluateFriday(time: Time): Double {
        return when (time.hour) {
            in 0..<5 -> afterMidnight + fridaysAfterMidnight
            in 17..<21 -> evening
            in 21..24 -> beforeMidnight + fridaysBeforeMidnight
            else -> 0.0
        }
    }

    private fun evaluateSaturday(time: Time): Double {
        return when (time.hour) {
            in 0..<5 -> afterMidnight + saturdaysAfterMidnight
            in 17..<21 -> evening
            in 21..24 -> beforeMidnight + saturdaysBeforeMidnight
            else -> 0.0
        }
    }

    private fun evaluateSunday(time: Time): Double {
        return when (time.hour) {
            in 0..<5 -> afterMidnight + sundaysAfterMidnight
            in 17..<21 -> evening
            in 21..24 -> beforeMidnight
            else -> 0.0
        }
    }

    private fun evaluateDay(time: Time): Double {
        return when (time.hour) {
            in 0..<5 -> afterMidnight
            in 17..<21 -> evening
            in 21..24 -> beforeMidnight
            else -> 0.0
        }
    }
}

/**
 * Scopes in all the attributes that are considered potentially relevant for the mode choice in [ZoneModeParameters]
 *
 * @property time The simulation Time
 * @property distance The distance to the target zone
 * @property parkingPressure the parking pressure at the target zone
 */
class ModeZoneScope(
    val time: AbsoluteTime,
    val distance: Distance,
    val parkingPressure: Double
)

/**
 * The numeric factors of the [ModeZoneScope] are encapsulated in this class.
 *
 * @property time the numeric factor that should be applied to the utility function based on evaluation of [ModeZoneScope.time]
 * @property distance the numeric factor that should be applied to the utility function based on [ModeZoneScope.distance]
 * @property parkingPressure the numeric factor that should be applied to the utility function based on [ModeZoneScope.parkingPressure]
 */
data class ModeZoneParameterBlock(
    val time: Double,
    val distance: Double,
    val parkingPressure: Double
) {
    /**
     * Evaluates the parameter block by summing the individual parameters
     *
     * @return the sum of parameters found in this block
     */
    fun toDouble(): Double {
        return time + distance + parkingPressure
    }
}
