package usecases.steps

import domain.data.LegacyZone
import domain.data.MutableSharingProvider
import domain.data.MutableSharingStation
import domain.data.SharingProviderId
import domain.data.SharingStationId
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.Mode
import domain.location.Location
import modeling.steps.AddResourceStep
import modeling.steps.Context
import modeling.steps.CsvResource
import modeling.steps.LazyResource
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.SealStep
import modeling.steps.ValidateCsvMetadata
import units.Coordinate
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.int
import utils.csv.long
import utils.units.toCoordinate
import java.io.File

interface LoadSharingProvidersContext : Context {
    val sharingProvidersRepository: MutableRepository<MutableSharingProvider, SharingProviderId>
    val zoneRepository: Repository<Zone, ZoneId>
    val zoneColumnIndex: Map<Int, LegacyZone>

    val defaultSharingStationFile: File
        get() = File(demandFolder.path + "\\zone-repository\\sharing-stations.csv")
}

data class StationColumns(
    val uidColumn: String = "uid",
    val nameColumn: String = "name",
    val coordinatesColumn: String = "coordinates",
    val vehicleCountColumn: String = "vehicles",
    val zoneColumn: String = "zone",
    val zonesByFootColumn: String = "zone_avail",
)

private var providerIdCounter = 0L
private var sharingIdCounter: Long = 0L

@Suppress("LongParameterList", "UnusedParameter")
fun LoadSharingProvidersContext.prepareSharingStations(
    file: File = defaultSharingStationFile,
    columns: StationColumns = StationColumns(),
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    providerName: String,
    mode: Mode,
    coordinateParser: (String) -> Coordinate = String::parseCoordinate,
) {
    val sharingProvider: MutableSharingProvider = sharingProvidersRepository.elements.find {
        it.name == providerName
    }?.also {
        require(it.mode == mode) {
            "Cannot add another sharing provider with same name: $providerName but different mode: ${it.mode}!=$mode"
        }
    } ?: MutableSharingProvider(
        id = SharingProviderId(providerIdCounter++)
    ) {
        name = providerName
        this.mode = mode
    }

    val csvParser = CsvParser<MutableSharingStation>(errorHandling) { row ->

        MutableSharingStation(
            id = SharingStationId(sharingIdCounter++),
            owner = sharingProvider,
        ) {
            uid = row(columns.uidColumn)
            name = row(columns.nameColumn)
            zonesByFoot.addAll(
                prepareZonesByFoot(row, columns.zonesByFootColumn).toMutableSet()
            )
            location = Location(
                zone = getZone(row.long(columns.zoneColumn)),
                coordinate = coordinateParser(row(columns.coordinatesColumn)),
                roadAccess = null
            )
            initialVehicleCount = row.int(columns.vehicleCountColumn)
        }
    }

    this.prepareStationsFile(sharingProvider, csvParser, file, delimiter) // TODO
}

fun LoadSharingProvidersContext.prepareStationsFile(
    sharingProvider: MutableSharingProvider,
    parser: CsvParser<MutableSharingStation>,
    file: File = defaultSharingStationFile,
    delimiter: String = SEMICOLON,
) = runStep {
    object : AddResourceStep<MutableSharingProvider, SharingProviderId>() {
        override val name = "Add sharing provider ${sharingProvider.name} and parse its stations from csv: ${file.name}"

        private val csvResource = CsvResource(file, parser, delimiter)

        override val resource = LazyResource<MutableSharingProvider>(
            csvResource.name,
            csvResource.source,
        ) {
            csvResource.elements.toList()

            if (sharingProvidersRepository.elements.none { it.name == sharingProvider.name }) {
                sequenceOf(sharingProvider)
            } else {
                emptySequence()
            }
        }

        override val repository = sharingProvidersRepository
        override val dependentRepositories = setOf(zoneRepository)

        override fun verifyInput() = ValidateCsvMetadata(this, csvResource).validate()
        override fun mockElementsForValidation(): List<MutableSharingProvider> = emptyList() // TODO
    }
}

fun LoadSharingProvidersContext.finishSharingStations() = runStep {
    SealStep(sharingProvidersRepository)
}

fun LoadSharingProvidersContext.loadSharingStations(
    providerName: String,
    mode: Mode,
) {
    this.prepareSharingStations(providerName = providerName, mode = mode)
    this.finishSharingStations()
}

fun String.parseCoordinate(): Coordinate =
    this.split(",")
        .takeIf { it.size == 2 }
        ?.let { it[0].toDouble() to it[1].toDouble() }
        ?.toCoordinate()
        ?: error(
            "Malformed Coordinate: could not parse coordinate string '$this'." +
                "Expected format: '<NUMBER>,<NUMBER>'!"
        )

// fun MutableSharingProvider.prepareVehicles(count: Int, mode: Mode = this.mode): Set<SharingVehicle> {
//    return (numberOfVehicles until numberOfVehicles + count).map {
//        SharingVehicle(
//            id = SharingVehicleId(it.toLong()),
//            mode = mode,
//            owner = this,
//        )
//    }.toSet()
// }

fun <C> C.prepareZonesByFoot(row: Row, column: String): Set<Zone> where C : LoadSharingProvidersContext {
    return row(column).split(",").map { id ->

        id.toLongOrNull()?.let {
            getZone(it)
        } ?: error(
            "Could not parse ZoneId $id (expected value of type Long) " +
                "in column $column row${row.index} of ${row.source}: ${row(column)}!"
        )
    }.toSet()
}

fun <C> C.getZone(id: Long): Zone where C : LoadSharingProvidersContext = requireNotNull(
    this.zoneRepository[ZoneId(id)] ?: zoneColumnIndex[id.toInt()]
) {
    "Referenced ZoneId $id could not be found in zoneRepo:" +
        " ${zoneRepository.elements.map { it.id }.toList()}"
}
