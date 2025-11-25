package application.config

import application.config.subconfigs.CSVConfig
import application.config.subconfigs.MatrixConfig
import domain.shared.behavior.ChoiceModelModes
import domain.shared.datastructure.matrix.KeyBasedMatrixCreation
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.simulation.behavior.DestinationChoiceCharacteristics
import domain.simulation.behavior.ModeChoiceCharacteristics
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel
import utils.ErrorHandling
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

data class ShortTermConfig(
    /* impedance*/
    val visumNetwork: Path? = null,
    val fractionOfPopulation: Double = 1.0,

    // Robin: Caching is optional, if the end user doesn't want caches then they shouldn't be forced to specify this
    val cachePath: Path? = null,
    val zoneMatrixCreationMethod: ZoneMatrixCreation = KeyBasedMatrixCreation,

    /* simulation */
    val simulationContext: ExampleProjectContext,
    val errorHandling: ErrorHandling = ErrorHandling.THROW,
    val resultPath: Path = Path("results"),

    /*  paths to individual csv files   */
    val sourceFiles: CSVConfig,

    /* ChoiceParameters */
    val destinationChoiceModel: UtilityBasedChoiceModel<Location, DestinationChoiceCharacteristics>,
    val modeChoiceModel: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,

    /* vehicle sharing */
    val sharingProviderName: String,

) {
    lateinit var matrixConfig: MatrixConfig
    lateinit var resultName: String
    lateinit var choiceModelModes: ChoiceModelModes
    lateinit var vehicleCountColumn: String

    fun validate() {
        val paths = mutableListOf(
            simulationContext.dataFolder,
            cachePath
        )

        val nonExistentPaths =
            paths.filter { !(it?.exists() ?: true) } +
                matrixConfig.getNonexistentPaths() +
                sourceFiles.getNonexistentPaths()
        require(nonExistentPaths.isEmpty()) { "The following paths are not existing: $nonExistentPaths" }
    }
}
