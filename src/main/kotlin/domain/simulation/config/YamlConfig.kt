package domain.simulation.config

import java.nio.file.Path
import kotlin.io.path.exists

data class ShortTermConfig(
    val visumNetwork: Path? = null,
    val fractionOfPopulation: Double = 1.0,
    val dataFolder: Path,
    val seed: Long,
    val scenarioName: String,
    val attractivitiesFile: Path,
    val costMatrixConfigPath: Path,
    val durationMatrixConfigPath: Path,
    val distanceMatrixPath: Path,
) {
    fun validate() {
        val paths = listOf(dataFolder, attractivitiesFile, costMatrixConfigPath, durationMatrixConfigPath, distanceMatrixPath).filter { !it.exists() }
        require(paths.isEmpty()) { "The following paths are not existing: $paths" }
    }
}