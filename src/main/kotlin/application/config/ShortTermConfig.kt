package application.config

import application.config.subconfigs.BaseCSVFiles
import application.config.subconfigs.MatrixConfig
import domain.shared.behavior.ChoiceModelModes
import domain.shared.datastructure.matrix.KeyBasedMatrixCreation
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import domain.shared.enums.Mode
import domain.shared.location.StandardLocation
import domain.simulation.behavior.DestinationChoiceCharacteristics
import domain.simulation.behavior.ModeChoiceCharacteristics
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel
import utils.ErrorHandling
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

data class ShortTermConfig<CSVFiles : BaseCSVFiles>(
    /* impedance*/
    val visumNetwork: Path? = null,
    val fractionOfPopulation: Double = 1.0,

    val seed: Long = 42,

    // Robin: Caching is optional, if the end user doesn't want caches then they shouldn't be forced to specify this
    val cachePath: Path? = null, //TODO instead of nullable maybe specify default path
    val zoneMatrixCreationMethod: ZoneMatrixCreation = KeyBasedMatrixCreation,

    /* simulation */
//    val simulationContext: ExampleProjectContext,
    val errorHandling: ErrorHandling = ErrorHandling.THROW,
    val resultPath: Path = Path("results"),

    /*  paths to individual csv files   */
    val sourceFiles: CSVFiles,

    /* ChoiceParameters */
    val destinationChoiceModel: UtilityBasedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
    val modeChoiceModel: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,

) {
    lateinit var matrixConfig: MatrixConfig
    lateinit var resultName: String
    lateinit var choiceModelModes: ChoiceModelModes

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
