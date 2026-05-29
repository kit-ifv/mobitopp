package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneMatrixLookup
import domain.shared.location.DurationMetric
import utils.units.AbsoluteTime

class DurationMultiMatrix<M>(private val rawMatrix: ZoneMatrixLookup<M>, private val converter: DoubleToDuration) {
    operator fun get(mode: M, time: AbsoluteTime): DurationMetric = DurationMatrix(rawMatrix[mode, time], converter)
}
