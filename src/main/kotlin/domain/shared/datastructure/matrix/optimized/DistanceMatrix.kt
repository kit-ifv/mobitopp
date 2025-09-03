package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.DistanceMetric
import domain.shared.location.Location
import domain.shared.location.ZoneId
import units.Distance

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
        origin: Location,
        destination: Location,
    ): Distance {
        return this[origin.requireZone().id, destination.requireZone().id]
    }
}
