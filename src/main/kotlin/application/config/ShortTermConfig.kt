package application.config

import java.nio.file.Path
import kotlin.io.path.exists

data class ShortTermConfig(
    val visumNetwork: Path? = null,
    val fractionOfPopulation: Double = 1.0,
    val costMatrixConfigPath: Path,
    val durationMatrixConfigPath: Path,
    val distanceMatrixPath: Path,
    val simulationContext: ExampleProjectContext
) {
    fun validate() {
        val paths = listOf(
            costMatrixConfigPath,
            durationMatrixConfigPath,
            distanceMatrixPath
        ).filter { !it.exists() }
        require(paths.isEmpty()) { "The following paths are not existing: $paths" }
    }
}
