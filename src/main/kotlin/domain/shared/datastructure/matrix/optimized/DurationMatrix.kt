package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneIdMatrix
import domain.shared.location.DurationMetric
import domain.shared.location.HasZone
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
        origin: HasZone,
        destination: HasZone,
    ): Duration {
        return this[origin.zoneID, destination.zoneID]
    }
}
