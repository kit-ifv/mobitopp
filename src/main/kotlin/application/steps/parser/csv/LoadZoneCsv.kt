package application.steps.parser.csv

import core.modelsteps.LoadCsvStep
import core.modelsteps.MutableRepository
import core.modelsteps.SealStep
import domain.shared.enums.ZoneClassification
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.location.attributes.HasRoadAccess
import domain.shared.location.Location
import domain.shared.location.MutableLegacyZone
import domain.shared.location.ZoneId
import domain.shared.location.parseRoadPositionWGS
import domain.simulation.config.DemandSimContext
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.WGS84Coordinate
import edu.kit.ifv.units.meters
import utils.CodePlan
import utils.Decodable
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.DefaultCsvParser
import utils.csv.SEMICOLON
import utils.csv.boolean
import utils.csv.decode
import utils.csv.distance
import utils.csv.double
import utils.csv.int
import utils.csv.long
import java.nio.file.Path

interface LoadZonesContext : DemandSimContext {
    val zoneRepository: MutableRepository<MutableLegacyZone, ZoneId>
    val regionTypeCodes: CodePlan<RegionType>

    val defaultZonePath: Path
        get() = zoneFolder.resolve("zones.csv")
}

data class ZoneColumns(
    val idColumn: String = "id",
    val nameColumn: String = "name",
    val areaTypeColumn: String = "areaType",
    val regionTypeColumn: String = "regionType",
    val classificationColumn: String = "classification",
    val parkingPlacesColumn: String = "parkingPlaces",
    val centroidColumn: String = "centroidLocation",
    val isDestinationColumn: String = "isDestination",
    val reliefColumn: String = "relief",
)

@Suppress("LongParameterList", "UnusedParameter")
fun LoadZonesContext.prepareZones(
    path: Path = defaultZonePath,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: ZoneColumns = ZoneColumns(),
    centroidParser: (String) -> HasRoadAccess = String::parseRoadPositionWGS,
    reliefUnit: DistanceUnit = DistanceUnit.METERS,
) {
    val csvParser = defaultCsvParser(
        errorHandling,
        columns,
        centroidParser,
        reliefUnit,
        regionTypeCodePlan = regionTypeCodes,
        seed = simulationSeed
    )

    this.prepareZoneFile(csvParser, path, delimiter) // TODO filter?
}

@Suppress("LongParameterList")
fun defaultCsvParser(
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: ZoneColumns = ZoneColumns(),
    centroidParser: (String) -> HasRoadAccess = String::parseRoadPositionWGS,
    reliefUnit: DistanceUnit = DistanceUnit.METERS,
    regionTypeCodePlan: Decodable<RegionType> = RegioStaR17,
    seed: Long = 1,
): DefaultCsvParser<MutableLegacyZone> {
    val csvParser = CsvParser(errorHandling) { row ->
        MutableLegacyZone(
            id = ZoneId(row.long(columns.idColumn)),
            centroid = row(columns.centroidColumn, centroidParser),
            seed = seed
        ) {
            visumId = row.long(columns.idColumn)
            matrixColumn = row.index
            name = row(columns.nameColumn)
            regionType =
                row.decode(columns.regionTypeColumn, regionTypeCodePlan)
            classification = row(columns.classificationColumn).toZoneClassification()
            parkingPlaces = row.int(columns.parkingPlacesColumn)
            isDestination = row.boolean(columns.isDestinationColumn)
            relief = row.double().distance(columns.reliefColumn, reliefUnit)
        }
    }

    return csvParser
}

fun cheatyDefaultCsvParser(
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    seed: Long = 1,
): DefaultCsvParser<MutableLegacyZone> {
    val csvParser = CsvParser(errorHandling) { row ->
        MutableLegacyZone(
            id = ZoneId(row.long("id")),
            centroid = Location.BIELEFELD,
            seed = seed
        ) {
            visumId = row.long("id")
            matrixColumn = -1
            name = row("zone_name")
            regionType = RegioStaR17.REGIOPOLE
            classification = ZoneClassification.STUDY_AREA
            parkingPlaces = 0
            isDestination = true
            relief = 0.meters
        }
    }

    return csvParser
}

@Suppress("MagicNumber")
private val BIELEFELD = WGS84Coordinate.degreesMinutesSeconds(52, 0, 59.99, 8, 30, 59.99)

fun LoadZonesContext.prepareZoneFile(
    parser: CsvParser<MutableLegacyZone>,
    path: Path = defaultZonePath,
    delimiter: String = SEMICOLON,
) = runStep {
    LoadCsvStep<MutableLegacyZone, ZoneId>(
        path = path,
        name = "Load zones from csv",
        parser = parser,
        delimiter = delimiter,
        repository = zoneRepository,
        dependentRepositories = setOf(),
        validationMock = listOf() // TODO
    )
}

// fun <S, C> S.filterZones() where S : ModelExecution<C>, C : LoadZonesContext {
//    this.addStep(FilterStep("filter zones", mobitopp.zoneRepository) { it.visumId == 1L })
// } //TODO

fun LoadZonesContext.finishZones() = runStep {
    SealStep(zoneRepository)
}

fun LoadZonesContext.loadZones(
    errorHandling: ErrorHandling = ErrorHandling.WARNING
) {
    this.prepareZones(errorHandling = errorHandling)
//    this.filterZones()
    this.finishZones()
}

fun String.toZoneClassification() = when (this) {
    "studyArea" -> ZoneClassification.STUDY_AREA
    "outlyingArea" -> ZoneClassification.OUTLYING_AREA
    "extendedStudyArea" -> ZoneClassification.EXTENDED_STUDY_AREA
    else -> throw UnsupportedOperationException(
        "String '$this' cannot be parsed as a ZoneClassification! " +
            "Expected: 'studyArea', 'outlyingArea' or 'extendedStudyArea'"
    )
}
