package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.DistanceMetric
import domain.shared.location.ZoneId
import edu.kit.ifv.units.Distance
import utils.Identifiable

fun interface DoubleToDistance {
    fun from(x: Double): Distance
}

class DistanceMatrix(private val translatedMatrix: ZoneIdMatrix, private val converter: (Double) -> Distance) :
    DistanceMetric {
    operator fun get(row: ZoneId, column: ZoneId): Distance = converter(translatedMatrix[row, column])

    override fun evaluate(origin: Identifiable<ZoneId>, destination: Identifiable<ZoneId>): Distance =
        this[origin.id, destination.id]
}
