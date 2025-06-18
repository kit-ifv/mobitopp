package core.datastructure.matrix

import utils.units.AbsoluteTime

/**
 * A matrix manager that provides a separate OD matrix for each mode and time. The characteristic of O or D is
 * generic. But at the moment it is mostly Zone. The output is also generic it could be time, cost or something similar.
 *
 * M: Mode enum
 * I: Origin Destination enum
 * O: Output for example Distance
 */
interface MultiMatrix<M, I, O> {
    operator fun get(mode: M, time: AbsoluteTime): Matrix<I, O>
}

fun <T, M, I, O> T.matrixAt(
    mode: M,
    time: AbsoluteTime,
): Matrix<I, O> where T : MultiMatrix<M, I, O> = object : Matrix<I, O> {

    override fun get(row: I, column: I): O = this@matrixAt[mode, time][row, column]
}
