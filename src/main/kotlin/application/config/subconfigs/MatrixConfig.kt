package application.config.subconfigs

import com.fasterxml.jackson.annotation.JsonIgnore
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

private const val DEFAULT_COST_MATRIX_FILENAME = "cost-matrix-configuration_transmove_turbo.yaml"
private const val DEFAULT_DURATION_MATRIX_FILENAME = "time-matrix-configuration_transmove_turbo.yaml"
private const val DEFAULT_DISTANCE_MATRIX_FILENAME = "DIS_Car.mtx.bz2"

/**
 * This data class contains all necessary paths for cost, duration and distance matrices. It handles the
 * default structure and possible creation methods.
 * @param costMatrixConfig An absolute path to the cost-matrix.yaml.
 * @param durationMatrixConfig An absolute path to the duration-matrix.yaml.
 * @param distanceMatrix An absolute path to the .mtx.bz2 distance matrix.
 */
data class MatrixConfig(
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
        costMatrixConfig: Path? = null,
        durationMatrixConfig: Path? = null,
        distanceMatrix: Path? = null
    ) :
        this(
            costMatrixConfig = matrixRepo.resolve(costMatrixConfig ?: Path(DEFAULT_COST_MATRIX_FILENAME)),
            durationMatrixConfig = matrixRepo.resolve(durationMatrixConfig ?: Path(DEFAULT_DURATION_MATRIX_FILENAME)),
            distanceMatrix = matrixRepo.resolve(distanceMatrix ?: Path(DEFAULT_DISTANCE_MATRIX_FILENAME))
        )

    /**
     * Checks whether the cost-, duration-, and distance-matrix-path exists and returns the ones not existing.
     * @return list containing any of the three paths this class manages, if they don't exist.
     */
    @JsonIgnore
    fun getNonexistentPaths(): List<Path> {
        val paths = listOf(costMatrixConfig, durationMatrixConfig, distanceMatrix)
        return paths.filter { !it.exists() }
    }

    companion object {
        /**
         * @return all the constructor parameter names, including matrixRepo.
         */
        fun getParameterNames(): Set<String> {
            return setOf(
                "matrixRepo",
                "costMatrixConfig",
                "durationMatrixConfig",
                "distanceMatrix"
            )
        }
    }
}
