package domain.shared.datastructure.matrix.optimized

import domain.shared.datastructure.matrix.ZoneMatrixLookup
import domain.shared.location.CostMetric
import utils.units.AbsoluteTime

class CostMultiMatrix<M>(private val rawMatrix: ZoneMatrixLookup<M>, private val converter: DoubleToCurrency) {
    operator fun get(mode: M, time: AbsoluteTime): CostMetric = CurrencyMatrix(rawMatrix[mode, time], converter)
}
