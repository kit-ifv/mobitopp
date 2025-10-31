package application.config

import domain.shared.behavior.ChoiceModelModes
import domain.shared.datastructure.matrix.KeyBasedMatrixCreation
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import utils.ErrorHandling
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

data class ShortTermConfig<MODECHOICEPARAMETERS, DESTINATIONCHOICEPARAMETERS>(
    /* impedance*/
    val visumNetwork: Path? = null,
    val fractionOfPopulation: Double = 1.0,

    val costMatrixConfig: Path,
    val durationMatrixConfig: Path,
    val distanceMatrix: Path,
    // Robin: Caching is optional, if the end user doesn't want caches then they shouldn't be forced to specify this
    val cachePath: Path? = null,
    val zoneMatrixCreationMethod: ZoneMatrixCreation = KeyBasedMatrixCreation,

    /* simulation */
    val simulationContext: ExampleProjectContext,
    val errorHandling: ErrorHandling = ErrorHandling.THROW,
    val resultPath: Path = Path("results"),

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

    /* ChoiceParameters */
    val destinationChoiceParameterSet: DESTINATIONCHOICEPARAMETERS,
    val modeChoiceParameterSet: MODECHOICEPARAMETERS,

    /* vehicle sharing */
    val sharingProviderName: String,

) {

    lateinit var matrixRepo: Path
    lateinit var resultName: String
    lateinit var zoneRepo: Path
    lateinit var choiceModelModes: ChoiceModelModes
    lateinit var vehicleCountColumn: String

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

        val nonExistantPaths = paths.filter { !(it?.exists() ?: true) }
        require(nonExistantPaths.isEmpty()) { "The following paths are not existing: $nonExistantPaths" }
    }
}
