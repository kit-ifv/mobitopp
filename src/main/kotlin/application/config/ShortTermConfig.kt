package application.config

import domain.shared.datastructure.matrix.ZoneMatrixCreation
import domain.simulation.behavior.DestinationChoiceParameters
import utils.ErrorHandling
import java.nio.file.Path
import kotlin.io.path.exists

data class ShortTermConfig(
    /* impedance*/
    val visumNetwork: Path? = null,
    val fractionOfPopulation: Double = 1.0,
    val costMatrixConfigPath: Path,
    val durationMatrixConfigPath: Path,
    val distanceMatrixPath: Path,
    val cachePath: Path,
    val matrixCreationMethod: ZoneMatrixCreation, // key based, visum, standard
    /* simulation */
    val simulationContext: ExampleProjectContext,
    val errorHandling: ErrorHandling = ErrorHandling.THROW,
    val resultPath: Path,
    val resultName: String,
    /* csv */
    val personCSV: Path? = null,
    val householdCSV: Path? = null,
    val activitiesCSV: Path? = null,
    val privateCarsCSV: Path? = null,
    val fixedDestinationCSV: Path? = null,
    val attractivitiesCSV: Path? = null,
    val bikeSharingStations: Path? = null,
    val zonesCSV: Path? = null,
    /* repos*/
    val zoneRepo: Path,

    /* behaviour */
    val destinationChoiceParameterSet: DestinationChoiceParameters,

    /* vehicle sharing */
    val sharingProviderName: String,
    val vehicleCountColumn: String,

    ) {
    fun validate() {
        val paths = listOf(
            costMatrixConfigPath,
            durationMatrixConfigPath,
            distanceMatrixPath,
            simulationContext.dataFolder,
            cachePath,
        ).filter { !it.exists() }
        require(paths.isEmpty()) { "The following paths are not existing: $paths" }
    }
}
