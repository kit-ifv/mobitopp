package edu.kit.ifv.domain.shared.location

import edu.kit.ifv.core.datastructure.matrix.DoubleMatrix
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.zone.attributes.HasZoneId
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import edu.kit.ifv.utils.units.Time
import kotlin.time.Duration

/**
 * An Impedance is an object, which provides the costs, distances and times that are required to travel from one zone
 * to another. It should generally encode be able to
 */
interface Impedance {

    fun cost(from: HasZoneId, to: HasZoneId, mode: Mode, time: Time): Currency = costMetric(
        mode,
        time,
    ).evaluate(from, to)

    fun distance(from: HasZoneId, to: HasZoneId, mode: Mode): Distance = distanceMetric(mode).evaluate(from, to)

    fun duration(from: HasZoneId, to: HasZoneId, mode: Mode, time: Time): Duration = durationMetric(
        mode,
        time,
    ).evaluate(from, to)

    fun costMetric(mode: Mode, time: Time): CostMetric

    fun distanceMetric(mode: Mode): DistanceMetric

    fun durationMetric(mode: Mode, time: Time): DurationMetric
}

/**
 * An Impedance encodes costs, distances and times that are required to travel from one zone to another.
 * This type of impedance works with a fixed set of zones. It returns a full DoubleMatrix for each mode.
 */
interface ArrayBackedImpedance {
    /**
     * The distances between all zones for a given mode.
     * @return In the first row the DoubleMatrix encodes the distance between zone_0 to zone_0, then zone_0 and zone_1,
     * then zone_0 and zone_2 ...
     * What unit the value encodes is implementation specific.
     */
    fun distanceArray(mode: Mode): DoubleMatrix

    /**
     * The duration to travel between all zones for a given mode.
     * @return In the first row the DoubleMatrix encodes the duration for traveling between zone_0 to zone_0, then
     * zone_0 and zone_1, then zone_0 and zone_2 ...
     * What unit the value encodes is implementation specific.
     */
    fun durationArray(mode: Mode): DoubleMatrix

    /**
     * The cost to travel between all zones for a given mode.
     * @return In the first row the DoubleMatrix encodes the cost for traveling between zone_0 to zone_0, then
     * zone_0 and zone_1, then zone_0 and zone_2 ...
     * What unit the value encodes is implementation specific.
     */
    fun costArray(mode: Mode): DoubleMatrix
}
