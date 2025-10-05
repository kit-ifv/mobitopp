package application.config

import domain.shared.behavior.ChoiceModelModes
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.simulation.behavior.ActivityDurationRandomizer
import domain.simulation.behavior.DestinationChoiceCharacteristics
import domain.simulation.behavior.ModeChoiceCharacteristics
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel
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
    val errorHandling: ErrorHandling = ErrorHandling.THROW, // silent, warning, error, throw, throw_no_log
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
    /* repos*/
    val zoneRepo: Path,

    /* behavour */
    val choiceModelModes: ChoiceModelModes, // hamburg, legacy
    val modeChoiceModel: FixedChoiceModel<Mode, ModeChoiceCharacteristics>, // default and standalone
    val destinationChoiceModel: UtilityBasedChoiceModel<Location, DestinationChoiceCharacteristics>, // hamburg, legacy, debug

    /* agents */
    val activityDurationRandomizer: ActivityDurationRandomizer // none, Gaussian

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
