package application.steps

import application.config.subconfigs.BaseCSVFiles
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import domain.shared.enums.ActivityType
import domain.shared.enums.areatype.RegionType
import domain.synthesis.data.EconomicStatus
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.UnitIntervalValue
import utils.CodePlan
import utils.ErrorHandling
import java.nio.file.Path
import kotlin.time.DurationUnit

interface Config {
    val seed: Long
    val errorHandling: ErrorHandling //maybe move to source file config?
}

interface SimulationConfig: Config {
    val fractionOfPopulation: UnitIntervalValue
}

interface SourceFilesConfig: Config {
    val sourceFiles: BaseCSVFiles
    val cachePath: Path
}

//config requirements //TODO find/create proper package for common requirements
interface MatrixConfig: Config {
    val costMatrixConfig: Path
    val durationMatrixConfig: Path
    val distanceMatrix: Path
    val matrixCreation: ZoneMatrixCreation
}

interface UnitConfig: Config {
    val distanceUnit: DistanceUnit
    val durationUnit: DurationUnit
    val currencyUnit: CurrencyUnit
}

interface RegionCodesConfig: Config {
    val regionTypeCodes: CodePlan<RegionType>
}

interface HouseholdCodesConfig: Config {
    val economicStatusCodes: CodePlan<EconomicStatus>
}

interface ActivityTypesConfig: Config {
    val activityTypes: CodePlan<ActivityType>
}