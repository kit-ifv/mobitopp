package edu.kit.ifv.domain.shared.datastructure.matrix.optimized
import edu.kit.ifv.domain.shared.datastructure.matrix.ZoneIdMatrix
import edu.kit.ifv.domain.shared.location.zone.DistanceZoneMetric
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.units.Distance

class DistanceMatrix(private val translatedMatrix: ZoneIdMatrix, private val converter: (Double) -> Distance) :
    DistanceZoneMetric {
    operator fun get(row: ZoneId, column: ZoneId): Distance = converter(translatedMatrix[row, column])

    override fun evaluate(origin: ZoneId, destination: ZoneId): Distance = this[origin, destination]
}
