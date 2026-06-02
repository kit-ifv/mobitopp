package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.zone.DurationZoneMetric
import domain.shared.location.zone.ZoneId
import kotlin.time.Duration

fun interface DoubleToDuration {
    fun from(x: Double): Duration
}

class DurationMatrix(private val translatedMatrix: ZoneIdMatrix, private val converter: DoubleToDuration) :
    DurationZoneMetric {
    operator fun get(row: ZoneId, column: ZoneId): Duration = converter.from(translatedMatrix[row, column])

    override fun evaluate(origin: ZoneId, destination: ZoneId): Duration = this[origin, destination]
}
