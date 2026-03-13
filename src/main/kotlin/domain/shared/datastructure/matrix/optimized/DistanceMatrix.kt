package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.DistanceMetric
import domain.shared.location.HasZone
import domain.shared.location.LocationOld
import domain.shared.location.ZoneId
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
        origin: HasZone,
        destination: HasZone,
    ): Distance {
        return this[origin.zoneID, destination.zoneID]
    }
}
