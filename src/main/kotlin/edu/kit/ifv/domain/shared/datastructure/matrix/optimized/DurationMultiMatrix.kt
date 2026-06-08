package edu.kit.ifv.domain.shared.datastructure.matrix.optimized
import edu.kit.ifv.domain.shared.datastructure.matrix.ZoneMatrixLookup
import edu.kit.ifv.domain.shared.location.DurationMetric
import edu.kit.ifv.utils.units.AbsoluteTime

class DurationMultiMatrix<M>(private val rawMatrix: ZoneMatrixLookup<M>, private val converter: DoubleToDuration) {
    operator fun get(mode: M, time: AbsoluteTime): DurationMetric = DurationMatrix(rawMatrix[mode, time], converter)
}
