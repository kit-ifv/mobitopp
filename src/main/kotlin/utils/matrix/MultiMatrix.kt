package utils.matrix
/**
 * A matrix manager that provides a separate OD matrix for each mode and time. The characteristic of O or D is
 * generic. But at the moment it is mostly Zone. The output is also generic it could be time, cost or something similar.
 *
 * M: Mode enum
 * I: Origin Destination enum
 * O: Output for example Distance
 */
interface MultiMatrix<M, I, O> {
    operator fun get(mode: M, time: TempAbsoluteTime): Matrix<I, O>
}
