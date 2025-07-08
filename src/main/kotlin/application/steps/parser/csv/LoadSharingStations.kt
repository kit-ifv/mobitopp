package application.steps.parser.csv

import core.modelsteps.AddResourceStep
import core.modelsteps.CsvResource
import core.modelsteps.LazyResource
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.SealStep
import core.modelsteps.ValidateCsvMetadata
import domain.shared.enums.Mode
import domain.shared.location.LegacyZone
import domain.shared.location.Location
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.simulation.config.DemandSimContext
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.MutableSharingStation
import domain.synthesis.data.SharingProviderId
import domain.synthesis.data.SharingStationId
import units.Coordinate
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.int
import utils.csv.long
import utils.units.toCoordinate
import java.nio.file.Path

interface LoadSharingProvidersContext : DemandSimContext {
    val sharingProviderRepository: MutableRepository<MutableSharingProvider, SharingProviderId>
    val zoneRepository: Repository<Zone, ZoneId>
    val zoneColumnIndex: Map<Int, LegacyZone>

    val defaultSharingStationPath: Path
        get() = dataFolder.resolve("zone-repository").resolve("sharing-stations.csv")
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
    path: Path = defaultSharingStationPath,
    columns: StationColumns = StationColumns(),
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    providerName: String,
    mode: Mode,
    coordinateParser: (String) -> Coordinate = String::parseCoordinate,
) {
    val sharingProvider: MutableSharingProvider = sharingProviderRepository.elements.find {
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

    this.prepareStationsFile(sharingProvider, csvParser, path, delimiter) // TODO
}

fun LoadSharingProvidersContext.prepareStationsFile(
    sharingProvider: MutableSharingProvider,
    parser: CsvParser<MutableSharingStation>,
    path: Path = defaultSharingStationPath,
    delimiter: String = SEMICOLON,
) = runStep {
    object : AddResourceStep<MutableSharingProvider, SharingProviderId>() {
        override val name = "Add sharing provider ${sharingProvider.name} and parse stations from csv: ${path.fileName}"

        private val csvResource = CsvResource(path, parser, delimiter)

        override val resource = LazyResource<MutableSharingProvider>(
            csvResource.name,
            csvResource.source,
        ) {
            csvResource.elements.toList()

            if (sharingProviderRepository.elements.none { it.name == sharingProvider.name }) {
                listOf(sharingProvider).asSequence()
            } else {
                emptySequence()
            }
        }

        override val repository = sharingProviderRepository
        override val dependentRepositories = setOf(zoneRepository)

        override fun verifyInput() = ValidateCsvMetadata(this, csvResource).validate()
        override fun mockElementsForValidation(): List<MutableSharingProvider> = emptyList() // TODO
    }
}

fun LoadSharingProvidersContext.finishSharingStations() = runStep {
    SealStep(sharingProviderRepository)
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
