package usecases.steps.legacyData

import domain.data.MutableLegacyZone
import domain.data.ZoneId
import domain.enums.ZoneClassification
import domain.enums.areatype.AreaType
import domain.enums.areatype.RegioStaR17
import domain.location.Location
import domain.location.parseRoadPosition
import modeling.steps.Context
import modeling.steps.LoadCsvStep
import modeling.steps.MutableRepository
import modeling.steps.SealStep
import units.DistanceUnit
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
import utils.csv.id
import utils.csv.int
import utils.csv.long
import java.io.File

interface LoadZonesContext : Context {
    val zoneRepository: MutableRepository<MutableLegacyZone, ZoneId>
    val areaTypeCodes: CodePlan<AreaType>

    val defaultZoneFile: File
        get() = File(demandFolder.path + "\\zone-repository\\zones.csv")
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
    file: File = defaultZoneFile,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: ZoneColumns = ZoneColumns(),
    centroidParser: (String) -> Location = String::parseRoadPosition,
    reliefUnit: DistanceUnit = DistanceUnit.METERS,
) {
    val csvParser = defaultCsvParser(
        errorHandling,
        columns,
        centroidParser,
        reliefUnit,
        regionTypeCodePlan = areaTypeCodes,
        seed = simulationSeed
    )

    this.prepareZoneFile(csvParser, file, delimiter) // TODO filter?
}

@Suppress("LongParameterList")
fun defaultCsvParser(
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: ZoneColumns = ZoneColumns(),
    centroidParser: (String) -> Location = String::parseRoadPosition,
    reliefUnit: DistanceUnit = DistanceUnit.METERS,
    regionTypeCodePlan: Decodable<AreaType> = RegioStaR17,
    seed: Long = 1,
): DefaultCsvParser<MutableLegacyZone> {
    val csvParser = CsvParser(errorHandling) { row ->
        MutableLegacyZone(
            id = row.id(columns.idColumn),
            centroid = row(columns.centroidColumn, centroidParser),
            seed = seed
        ) {
            visumId = row.long(columns.idColumn)
            matrixColumn = row.index
            name = row(columns.nameColumn)
            regionType =
                row.decode(columns.regionTypeColumn, regionTypeCodePlan) // TODO remove either areaType or RegionType
            classification = row(columns.classificationColumn).toZoneClassification()
            parkingPlaces = row.int(columns.parkingPlacesColumn)
            isDestination = row.boolean(columns.isDestinationColumn)
            relief = row.double().distance(columns.reliefColumn, reliefUnit)
        }
    }

    return csvParser
}

fun LoadZonesContext.prepareZoneFile(
    parser: CsvParser<MutableLegacyZone>,
    file: File = defaultZoneFile,
    delimiter: String = SEMICOLON,
) = runStep {
    LoadCsvStep<MutableLegacyZone, ZoneId>(
        file = file,
        name = "Load zones from csv",
        parser = parser,
        delimiter = delimiter,
        repository = zoneRepository,
        dependentRepositories = setOf(),
        validationMock = listOf() // TODO
    )
}

// fun <S, C> S.filterZones() where S : ModelExecution<C>, C : LoadZonesContext {
//    this.addStep(FilterStep("filter zones", context.zoneRepository) { it.visumId == 1L })
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
