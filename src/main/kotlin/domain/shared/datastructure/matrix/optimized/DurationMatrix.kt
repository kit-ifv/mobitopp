package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.DurationMetric
import domain.shared.location.LocationOld
import domain.shared.location.ZoneId
import kotlin.time.Duration

fun interface DoubleToDuration {
    fun from(x: Double): Duration
}

class DurationMatrix(
    private val translatedMatrix: ZoneIdMatrix,
    private val converter: DoubleToDuration,
) :
    DurationMetric {
    operator fun get(row: ZoneId, column: ZoneId): Duration {
        return converter.from(translatedMatrix[row, column])
    }

    override fun evaluate(
        origin: LocationOld,
        destination: LocationOld,
    ): Duration {
        return this[origin.requireZone().id, destination.requireZone().id]
    }
}
