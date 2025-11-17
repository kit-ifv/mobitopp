package application.config.subconfigs


import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

private const val defaultCostMatrixName = "cost-matrix-configuration_transmove_turbo.yaml"
private const val defaultDurationMatrixName = "time-matrix-configuration_transmove_turbo.yaml"
private const val defaultDistanceMatrixName = "DIS_Car.mtx.bz2"

/**
 * This data class contains all necessary paths for cost, duration and distance matrices. It handles the
 * default structure and possible creation methods.
 * @param costMatrixConfig An absolute path to the cost-matrix.yaml.
 * @param durationMatrixConfig An absolute path to the duration-matrix.yaml.
 * @param distanceMatrix An absolute path to the .mtx.bz2 distance matrix.
 */
class MatrixConfig (
    val costMatrixConfig: Path,
    val durationMatrixConfig: Path,
    val distanceMatrix: Path
) {

    /**
     * Creation method for a rooted structure.
     * @param matrixRepo Path to the root directory where the matrix files are.
     * @param costMatrixConfig Path to the cost-matrix.yaml relative to the matrixRepo. If not set, this file is
     * expected to be a direct child of the matrixRepo directory. When given an absolute path, only that path is
     * considered and no resolution takes place.
     * @param durationMatrixConfig Path to the duration-matrix.yaml relative to the matrixRepo. If not set, this file
     * is expected to be a direct child of the matrixRepo directory. When given an absolute path, only that path is
     * considered and no resolution takes place.
     * @param distanceMatrix Path to the *.mtx.bz2 distance matrix relative to the matrixRepo. If not set, this file is
     * expected to be a direct child of the matrixRepo directory.When given an absolute path, only that path is
     * considered and no resolution takes place.
     */
    constructor(
        matrixRepo: Path,
        costMatrixConfig: Path = Path(defaultCostMatrixName),
        durationMatrixConfig: Path = Path(defaultDurationMatrixName),
        distanceMatrix: Path = Path(defaultDistanceMatrixName)
    ):
            this(
                costMatrixConfig = matrixRepo.resolve(costMatrixConfig),
                durationMatrixConfig = matrixRepo.resolve(durationMatrixConfig),
                distanceMatrix = matrixRepo.resolve(distanceMatrix)
            )

    /**
     * Checks whether the cost-, duration-, and distance-matrix-path exists and returns the ones not existing.
     * @return list containing any of the three paths this class manages, if they don't exist.
     */
    fun getNonexistentPaths(): List<Path> {
        val paths = listOf(costMatrixConfig, durationMatrixConfig, distanceMatrix)
        return paths.filter { !it.exists() }
    }
}