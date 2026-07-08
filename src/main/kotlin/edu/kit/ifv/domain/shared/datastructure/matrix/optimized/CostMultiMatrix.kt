package edu.kit.ifv.domain.shared.datastructure.matrix.optimized
import edu.kit.ifv.domain.shared.datastructure.matrix.ZoneMatrixLookup
import edu.kit.ifv.domain.shared.location.CostMetric
import edu.kit.ifv.utils.codes.Encodable
import edu.kit.ifv.utils.units.AbsoluteTime

class CostMultiMatrix<M: Encodable>(private val rawMatrix: ZoneMatrixLookup<M>, private val converter:
DoubleToCurrency) {
    operator fun get(mode: M, time: AbsoluteTime): CostMetric = CurrencyMatrix(rawMatrix[mode, time], converter)
}
