package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.DistanceMetric
import domain.shared.location.ZoneId
import domain.shared.location.attributes.HasZoneID
import edu.kit.ifv.units.Distance

fun interface DoubleToDistance {
    fun from(x: Double): Distance
}

class DistanceMatrix(
    private val translatedMatrix: ZoneIdMatrix,
    private val converter: (Double) -> Distance,
) : DistanceMetric {
    operator fun get(row: ZoneId, column: ZoneId): Distance {
        return converter(translatedMatrix[row, column])
    }

    override fun evaluate(
        origin: HasZoneID,
        destination: HasZoneID,
    ): Distance {
        return this[origin.zoneID, destination.zoneID]
    }
}
