package edu.kit.ifv.domain.shared.behavior

import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.domain.shared.location.zone.attributes.HasNumberParkingPlaces
import kotlin.math.abs

private const val ATTRACTIVENESS_THRESHOLD = 1e-6
private const val MAX_PARKING_PRESSURE = 999.0

/**
 * A [ParkingPressureModel] which calculates with an [AttractivenessModel].
 */
class ParkingPressureByAttractiveness(
    private val work: ActivityType,
    private val privateVisit: ActivityType,
    private val zoneProvider: (ZoneId) -> Zone<HasNumberParkingPlaces>,
    private val attractivenessModel: AttractivenessModel,
) : ParkingPressureModel {

    override fun calculate(zoneId: ZoneId): Double {
        val attractiveness = attractivenessModel.sumAttractiveness(zoneId, work, privateVisit)
        val parkingPlaces = zoneProvider(zoneId).attributes.parkingPlaces
        if (parkingPlaces == 0) {
            return if (abs(attractiveness) < ATTRACTIVENESS_THRESHOLD) 0.0 else MAX_PARKING_PRESSURE
        }
        return attractiveness / parkingPlaces
    }
}
