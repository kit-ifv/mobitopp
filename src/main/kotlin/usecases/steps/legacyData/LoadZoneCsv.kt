package usecases.steps.legacyData

import domain.data.MutableLegacyZone
import domain.data.ZoneId
import domain.enums.AreaType
import domain.enums.Regiostar17
import domain.enums.ZoneClassification
import domain.location.Location
import domain.location.parseRoadPosition
import modeling.steps.Context
import modeling.steps.LoadCsvStep
import modeling.steps.ModelExecution
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
fun <S, C> S.prepareZones(
    file: File = context.defaultZoneFile,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: ZoneColumns = ZoneColumns(),
    centroidParser: (String) -> Location = String::parseRoadPosition,
    reliefUnit: DistanceUnit = DistanceUnit.METERS,
) where S : ModelExecution<C>, C : LoadZonesContext {
    val csvParser = defaultCsvParser(errorHandling, columns, centroidParser, reliefUnit, regionTypeCodePlan = context.areaTypeCodes, seed= context.simulationSeed)

    this.prepareZoneFile(csvParser, file, delimiter) // TODO filter?
}

fun defaultCsvParser(
    errorHandling: ErrorHandling= ErrorHandling.WARNING,
    columns: ZoneColumns = ZoneColumns(),
    centroidParser: (String) -> Location = String::parseRoadPosition,
    reliefUnit: DistanceUnit= DistanceUnit.METERS,
    regionTypeCodePlan: Decodable<AreaType> = Regiostar17,
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
                row.decode(columns.regionTypeColumn, regionTypeCodePlan) //TODO remove either areaType or RegionType
            classification = row(columns.classificationColumn).toZoneClassification()
            parkingPlaces = row.int(columns.parkingPlacesColumn)
            isDestination = row.boolean(columns.isDestinationColumn)
            relief = row.double().distance(columns.reliefColumn, reliefUnit)
        }
    }
    return csvParser
}

fun <S, C> S.prepareZoneFile(
    parser: CsvParser<MutableLegacyZone>,
    file: File = context.defaultZoneFile,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : LoadZonesContext {
    this.addStep(
        LoadCsvStep<MutableLegacyZone, ZoneId>(
            file = file,
            name = "Load zones from csv",
            parser = parser,
            delimiter = delimiter,
            repository = context.zoneRepository,
            dependentRepositories = setOf(),
            validationMock = listOf() // TODO
        )
    )
}

// fun <S, C> S.filterZones() where S : ModelExecution<C>, C : LoadZonesContext {
//    this.addStep(FilterStep("filter zones", context.zoneRepository) { it.visumId == 1L })
// } //TODO

fun <S, C> S.finishZones() where S : ModelExecution<C>, C : LoadZonesContext {
    this.addStep(SealStep(context.zoneRepository))
}

fun <S, C> S.loadZones(
    errorHandling: ErrorHandling = ErrorHandling.WARNING
) where S : ModelExecution<C>, C : LoadZonesContext {
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
