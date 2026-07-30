package edu.kit.ifv.domain.shared.behavior

import edu.kit.ifv.domain.shared.location.zone.ZoneId

/**
 * Interface to provide a calculation function for parking pressure in a model.
 */
fun interface ParkingPressureModel {

    /**
     * Calculates the parking pressure for a specific [ZoneId] as [Double].
     * @param zoneId id of zone to calculate for
     * @return the calculated parking pressure
     */
    fun calculate(zoneId: ZoneId): Double
}
