package edu.kit.ifv.domain.shared.behavior

import edu.kit.ifv.domain.shared.location.zone.ZoneId

interface ParkingPressureModel {
    fun calculate(zoneId: ZoneId): Double
}
