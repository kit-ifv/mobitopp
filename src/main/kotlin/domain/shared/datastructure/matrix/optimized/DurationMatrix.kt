package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.DurationMetric
import domain.shared.location.ZoneId
import utils.Identifiable
import kotlin.time.Duration

fun interface DoubleToDuration {
    fun from(x: Double): Duration
}

class DurationMatrix(private val translatedMatrix: ZoneIdMatrix, private val converter: DoubleToDuration) :
    DurationMetric {
    operator fun get(row: ZoneId, column: ZoneId): Duration = converter.from(translatedMatrix[row, column])

    override fun evaluate(origin: Identifiable<ZoneId>, destination: Identifiable<ZoneId>): Duration =
        this[origin.id, destination.id]
}
