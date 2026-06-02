package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.zone.DistanceZoneMetric
import domain.shared.location.zone.ZoneId
import edu.kit.ifv.units.Distance

fun interface DoubleToDistance {
    fun from(x: Double): Distance
}

class DistanceMatrix(private val translatedMatrix: ZoneIdMatrix, private val converter: (Double) -> Distance) :
    DistanceZoneMetric {
    operator fun get(row: ZoneId, column: ZoneId): Distance = converter(translatedMatrix[row, column])

    override fun evaluate(origin: ZoneId, destination: ZoneId): Distance = this[origin, destination]
}
