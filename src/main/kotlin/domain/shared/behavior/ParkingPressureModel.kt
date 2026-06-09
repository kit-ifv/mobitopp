package domain.shared.behavior

import domain.shared.enums.ActivityType
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import kotlin.math.abs

interface ParkingPressureModel {
    fun calculate(zoneId: ZoneId): Double
}

class ParkingPressureByAttractiveness(
    private val work: ActivityType,
    private val privateVisit: ActivityType,
    private val zoneProvider: (ZoneId) -> Zone, // TODO: HasNumberOfParkingPlaces
    private val attractivenessModel: AttractivenessModel
): ParkingPressureModel {

    override fun calculate(zoneId: ZoneId): Double {
        val attractiveness = attractivenessModel.sumAttractiveness(zoneId, work, privateVisit)
        val parkingPlaces = zoneProvider(zoneId).parkingPlaces
        if (parkingPlaces == 0) {
            return if (abs(attractiveness) < 1e-6) 0.0 else 999.0
        }
        return attractiveness / parkingPlaces
    }

}
