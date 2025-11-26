package application.config.subconfigs

import com.fasterxml.jackson.annotation.JsonIgnore
import domain.jackson.JSONInitializer
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

    companion object : JSONInitializer<MatrixConfig> {
        private const val REPO_PARAM = "matrixRepo"
        private const val COST_PARAM = "costMatrixConfig"
        private const val DURATION_PARAM = "durationMatrixConfig"
        private const val DISTANCE_PARAM = "distanceMatrix"

        /**
         * @return all the constructor parameter names, including matrixRepo.
         */
        override fun getParameterNames(): Set<String> {
            return setOf(
                REPO_PARAM,
                COST_PARAM,
                DURATION_PARAM,
                DISTANCE_PARAM
            )
        }

        private fun Map<String, String>.retrieveAsPath(name: String): Path? {
            return if (containsKey(name)) { Path(get(name)!!) } else null
        }

        /**
         * Constructs a config out of the given params.
         * @throws error If the given params do not contain either 'matrixRepo' or all other fields since
         * no sensible config can be constructed then.
         */
        override fun init(givenParams: Map<String, String>): MatrixConfig {
            val matrixRepo: Path? = givenParams.retrieveAsPath(REPO_PARAM)
            val costMatrixConfig: Path? = givenParams.retrieveAsPath(COST_PARAM)
            val durationMatrixConfig: Path? = givenParams.retrieveAsPath(DURATION_PARAM)
            val distanceMatrix: Path? = givenParams.retrieveAsPath(DISTANCE_PARAM)

            if (matrixRepo != null) {
                return MatrixConfig(
                    matrixRepo = matrixRepo,
                    costMatrixConfig = costMatrixConfig,
                    durationMatrixConfig = durationMatrixConfig,
                    distanceMatrix = distanceMatrix
                )
            } else if (
                costMatrixConfig != null &&
                durationMatrixConfig != null &&
                distanceMatrix != null
            ) {
                return MatrixConfig(
                    costMatrixConfig = costMatrixConfig,
                    durationMatrixConfig = durationMatrixConfig,
                    distanceMatrix = distanceMatrix
                )
            } else {
                error("Missing mandatory fields. Either set 'matrixRepo' or all other fields.")
            }
        }
    }
}
