package edu.kit.ifv.application.steps
import edu.kit.ifv.application.config.subconfigs.BaseCSVFiles
import edu.kit.ifv.core.modelsteps.Config
import edu.kit.ifv.domain.shared.car.CarSegment
import edu.kit.ifv.domain.shared.datastructure.matrix.ZoneMatrixCreation
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.enums.areatype.RegionType
import edu.kit.ifv.domain.shared.enums.household.EconomicStatus
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.utils.CodePlan
import edu.kit.ifv.utils.units.AbsoluteTime
import java.nio.file.Path
import kotlin.time.Duration
import kotlin.time.DurationUnit

interface SimulationConfig : Config {
    val fractionOfPopulation: UnitIntervalValue
    val simulationStart: AbsoluteTime
    val simulationEnd: AbsoluteTime
    val timeStep: Duration
}

interface SourceFilesConfig : Config {
    val sourceFiles: BaseCSVFiles
    val cachePath: Path
}

interface ResultsConfig {
    val resultDir: Path
}

interface SharingSourceFilesConfig : Config {
    val bikeSharingStations: Path
    val carSharingStations: Path
    val carSharingFloatingArea: Path
}

interface SharingModesConfig : Config {
    val bikeSharingMode: Mode
    val carSharingStationMode: Mode
    val carSharingFloatingMode: Mode
}

interface DrtSourceFilesConfig : Config {
    val ridePoolingServiceAreas: Path
}

interface DrtModesConfig : Config {
    val ridePoolingMode: Mode
}

// config requirements //TODO find/create proper package for common requirements
interface MatrixConfig : Config {
    val costMatrixConfig: Path
    val durationMatrixConfig: Path
    val distanceMatrix: Path
    val matrixCreation: ZoneMatrixCreation
}

interface UnitConfig : Config {
    val distanceUnit: DistanceUnit
    val durationUnit: DurationUnit
    val currencyUnit: CurrencyUnit
}

interface RegionCodesConfig : Config {
    val regionTypeCodes: CodePlan<RegionType>
}

interface HouseholdCodesConfig : Config {
    val economicStatusCodes: CodePlan<EconomicStatus>
}

interface CarCodesConfig : Config {
    val carSegmentCodes: CodePlan<CarSegment>
}

interface ActivityTypesConfig : Config {
    val activityTypes: CodePlan<ActivityType>
}

interface PurposesConfig {
    val work: ActivityType
    val privateVisit: ActivityType
}

interface ModesConfig : Config {
    val modes: CodePlan<Mode>
}

interface AttractivenessFileConfig {
    val attractivenessFile: Path
}
