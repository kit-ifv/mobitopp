package usecases.steps

import domain.data.LegacyZone
import domain.data.MutableSharingProvider
import domain.data.MutableSharingStation
import domain.data.SharingStationId
import domain.data.SharingVehicle
import domain.data.SharingVehicleId
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.Mode
import domain.location.Location
import modeling.steps.Context
import modeling.steps.LoadCsvStep
import modeling.steps.ModelExecution
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.SealStep
import units.Coordinate
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.int
import utils.csv.long
import utils.units.toCoordinate
import java.io.File

interface LoadSharingStationsContext : Context {
    val sharingStationsRepository: MutableRepository<MutableSharingStation, SharingStationId>
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

private var idCounter: Long = 0L

@Suppress("LongParameterList", "UnusedParameter")
fun <S, C> S.prepareSharingStations(
    file: File = context.defaultSharingStationFile,
    columns: StationColumns = StationColumns(),
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    providerName: String,
    mode: Mode,
    coordinateParser: (String) -> Coordinate = String::parseCoordinate,
) where S : ModelExecution<C>, C : LoadSharingStationsContext {
    val sharingProvider = MutableSharingProvider {
        name = providerName
        this.mode = mode
    }

    val csvParser = CsvParser<MutableSharingStation>(errorHandling) { row ->

        MutableSharingStation(
            id = SharingStationId(idCounter++),
            owner = sharingProvider,
        ) {
            uid = row(columns.uidColumn)
            name = row(columns.nameColumn)
            zonesByFoot.addAll(
                context.prepareZonesByFoot(row, columns.zonesByFootColumn).toMutableSet()
            )
            location = Location(
                zone = context.getZone(row.long(columns.zoneColumn)),
                coordinate = coordinateParser(row(columns.coordinatesColumn)),
                roadAccess = null
            )
            addVehicles(
                sharingProvider.prepareVehicles(
                    count = row.int(columns.vehicleCountColumn),
                )
            )
        }
    }

    this.prepareStationsFile(csvParser, file, delimiter) // TODO
}

fun <S, C> S.prepareStationsFile(
    parser: CsvParser<MutableSharingStation>,
    file: File = context.defaultSharingStationFile,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : LoadSharingStationsContext {
    this.addStep(
        LoadCsvStep(
            file = file,
            name = "Load sharing stations from csv",
            parser = parser,
            delimiter = delimiter,
            repository = context.sharingStationsRepository,
            dependentRepositories = setOf(context.zoneRepository),
            validationMock = listOf() // TODO
        )
    )
}

fun <S, C> S.finishSharingStations() where S : ModelExecution<C>, C : LoadSharingStationsContext {
    this.addStep(SealStep(context.sharingStationsRepository))
}

fun <S, C> S.loadSharingStations(
    providerName: String,
    mode: Mode,
) where S : ModelExecution<C>, C : LoadSharingStationsContext {
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

fun MutableSharingProvider.prepareVehicles(count: Int, mode: Mode = this.mode): Set<SharingVehicle> {
    return (numberOfVehicles until numberOfVehicles + count).map {
        SharingVehicle(
            id = SharingVehicleId(it.toLong()),
            mode = mode,
            owner = this,
        )
    }.toSet()
}

fun <C> C.prepareZonesByFoot(row: Row, column: String): Set<Zone> where C : LoadSharingStationsContext {
    return row(column).split(",").map { id ->

        id.toLongOrNull()?.let {
            getZone(it)
        } ?: error(
            "Could not parse ZoneId $id (expected value of type Long) " +
                "in column $column row${row.index} of ${row.source}: ${row(column)}!"
        )
    }.toSet()
}

fun <C> C.getZone(id: Long): Zone where C : LoadSharingStationsContext = requireNotNull(
    this.zoneRepository.getById(ZoneId(id)) ?: zoneColumnIndex[id.toInt()]
) {
    "Referenced ZoneId $id could not be found in zoneRepo:" +
        " ${zoneRepository.elements.map { it.id }.toList()}"
}
