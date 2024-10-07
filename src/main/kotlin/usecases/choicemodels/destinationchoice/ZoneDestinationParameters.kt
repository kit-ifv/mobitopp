package usecases.choicemodels.destinationchoice

import units.Distance
import units.kilometers
import kotlin.math.pow

/**
 * Collects all potential parameter attributes from a target zone that may take influence in the destination choice.
 * Sets parameters to 0.0 as default implementation.
 *
 */
interface ZoneDestinationParameters {
    fun getDistance(zoneScope: ZoneScope): Double = 0.0
    fun getParking(zoneScope: ZoneScope): Double = 0.0
    fun getUmland(zoneScope: ZoneScope): Double = 0.0

    /**
     * Extracts the raw parameter values from a given zone scope
     *
     * @param zoneScope
     * @return
     */
    fun toParameters(zoneScope: ZoneScope): DestinationParameterBlock {
        return DestinationParameterBlock(
            getDistance(zoneScope),
            getParking(zoneScope),
            getUmland(zoneScope)
        )
    }
}

/**
 * Encapsulates the Attributes that may be used for utility calculation in the destination choice
 *
 * @property parkingPressure the parking pressure in the zone
 * @property isUmland whether the zone is considered "umland"
 * @property distance the distance needed to drive to the zone from an origin, determined beforehand.
 */
data class ZoneScope(
    val parkingPressure: Double,
    val isUmland: Boolean,
    val distance: Distance
)

/**
 * This interface provides a default implementation to extract a numeric parameter if the target zone is considered
 * "umland" (whatever that may mean)
 *
 * @property isUmland this parameter is applied to the utility function if the zone is considered "umland"
 */
interface DestinationUmland : ZoneDestinationParameters {
    val isUmland: Double
    override fun getUmland(zoneScope: ZoneScope): Double {
        return if (zoneScope.isUmland) isUmland else 0.0
    }
}

/**
 * This interface provides a base implementation found in the legacy destination choice function, where short distances
 * between 0 and 1 kilometer are applied a factor [upToOneKilometer]. And distances between 1 and 2 kilometers are applied
 * a factor [betweenOneAndTwoKilometers]. Note that in the original implementation a distance of 0.kilometers was explicitly
 * excluded from influencing the parameter, so this interface maintains this decision/oversight
 *
 * @property upToOneKilometer the parameter that should be used in the utility function if the distance is within 0 to 1
 * kilometer (excluding exactly 0)
 * @property betweenOneAndTwoKilometers this parameter is used in the utility function if the distance is
 * within 1 to 2 kilometers.
 */
interface StandardDestinationDistance : ZoneDestinationParameters {
    val upToOneKilometer: Double
    val betweenOneAndTwoKilometers: Double

    override fun getDistance(zoneScope: ZoneScope): Double {
        return when (zoneScope.distance) {
            0.kilometers -> 0.0
            in 0.kilometers..1.kilometers -> upToOneKilometer
            in 1.kilometers..2.kilometers -> betweenOneAndTwoKilometers
            else -> 0.0
        }
    }
}

/**
 * Adding this interface to a composite object adds the default parkstress calculation with the following calculation:
 *
 * [parkingBaseline] * min(PARKDRUCK, 50.0) ^ [parkingElasticity]
 *
 * This calculation will return 0.0 if the parking stress is higher than 999.0. It appears that this behaviour was originally
 * used to avoid miscalculated parking pressure dominating the utility function.
 *
 * @property parkingBaseline base parameter influencing the parkdruck: [parkingBaseline] * PARKDRUCK ...
 * @property parkingElasticity exponential multiplied to the parking stress: PARKDRUCK ^ [parkingElasticity]
 */
@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
interface StandardDestinationParkstress : ZoneDestinationParameters {
    val parkingBaseline: Double
    val parkingElasticity: Double
    override fun getParking(zoneScope: ZoneScope): Double {
        if (zoneScope.parkingPressure !in 0.0..999.0) return 0.0
        return parkingBaseline * zoneScope.parkingPressure.coerceAtMost(50.0).pow(parkingElasticity)
    }
}

/**
 * Collects the parameters that may influence a destination choice parameter block.
 *
 * @property closeDistance parameter if the distance from origin to zone fulfills a certain condition
 * @property parkstress parameter determined by parkstress of zone.
 * @property umland parameter determined by the ominous umland factor.
 */
data class DestinationParameterBlock(
    val closeDistance: Double,
    val parkstress: Double,
    val umland: Double

) {
    fun toDouble(): Double = closeDistance + parkstress + umland
}
