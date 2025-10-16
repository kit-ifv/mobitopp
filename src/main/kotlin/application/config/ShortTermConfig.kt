package application.config

import domain.shared.behavior.ChoiceModelModes
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import domain.simulation.behavior.DestinationChoiceParameters
import utils.ErrorHandling
import java.nio.file.Path
import kotlin.io.path.exists

data class ShortTermConfig<MODECHOICEPARAMETERS>(
    /* impedance*/
    val visumNetwork: Path? = null,
    val fractionOfPopulation: Double = 1.0,
    val matrixRepo: Path,
    val costMatrixConfig: Path,
    val durationMatrixConfig: Path,
    val distanceMatrix: Path,
    val cachePath: Path,
    val zoneMatrixCreationMethod: ZoneMatrixCreation,

    /* simulation */
    val simulationContext: ExampleProjectContext,
    val errorHandling: ErrorHandling = ErrorHandling.THROW,
    val resultPath: Path,
    val resultName: String,

    /*  paths to individual csv files   */
    val personCSV: Path? = null,
    val householdCSV: Path? = null,
    val activityCSV: Path? = null,
    val privateCarsCSV: Path? = null,
    val fixedDestinationCSV: Path? = null,
    val attractivitiesCSV: Path? = null,
    val bikeSharingStationsCSV: Path? = null,
    val zonesCSV: Path? = null,

    /* repos*/
    val zoneRepo: Path,

    /* ChoiceParameters */
    val destinationChoiceParameterSet: DestinationChoiceParameters,
    val modeChoiceParameterSet: MODECHOICEPARAMETERS,
    val choiceModelModes: ChoiceModelModes,

    /* vehicle sharing */
    val sharingProviderName: String,
    val vehicleCountColumn: String,

) {
    fun validate() {
        val paths = mutableListOf(
            matrixRepo,
            simulationContext.dataFolder,
            cachePath,
            zoneRepo
        )
        if (costMatrixConfig.isAbsolute) {
            paths.add(costMatrixConfig)
        } else {
            paths.add(matrixRepo.resolve(costMatrixConfig))
        }
        if (durationMatrixConfig.isAbsolute) {
            paths.add(durationMatrixConfig)
        } else {
            paths.add(matrixRepo.resolve(durationMatrixConfig))
        }
        if (distanceMatrix.isAbsolute) {
            paths.add(distanceMatrix)
        } else {
            paths.add(matrixRepo.resolve(distanceMatrix))
        }

        paths.filter { !it.exists() }
        require(paths.isEmpty()) { "The following paths are not existing: $paths" }
    }
}
