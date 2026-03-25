package domain.shared.location

import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.meters
import edu.kit.ifv.units.share

data class RoadAccess(val roadId: Long, val position: UnitIntervalValue, val lateralDistance: Distance = 0.meters) {
    companion object {
        val INVALID = RoadAccess(Long.MIN_VALUE, 0.5.share())
    }
}