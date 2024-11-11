package usecases.steps

import domain.data.SharingProvider
import domain.data.SharingStationBuilder
import domain.data.SharingVehicle
import domain.data.SharingVehicleId
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.Mode
import domain.location.Location
import modeling.steps.AddCsvStep
import modeling.steps.BuildStep
import modeling.steps.Context
import modeling.steps.CsvResource
import modeling.steps.ModelExecution
import units.Coordinate
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.int
import utils.csv.long
import utils.units.toCoordinate
import java.io.File

fun String.parseCoordinate(): Coordinate =
    this.split(",")
        .takeIf { it.size == 2 }
        ?.let { it[0].toDouble() to it[1].toDouble() }
        ?.toCoordinate()
        ?: error(
            "Malformed Coordinate: could not parse coordinate string '$this'." +
                "Expected format: '<NUMBER>,<NUMBER>'!"
        )

@Suppress("LongParameterList")
fun <S, C> S.prepareSharingStations(
    file: File? = null,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    providerName: String,
    mode: Mode,
    uidColumn: String = "uid",
    nameColumn: String = "name",
    coordinatesColumn: String = "coordinates",
    coordinateParser: (String) -> Coordinate = String::parseCoordinate,
    vehicleCountColumn: String = "vehicles",
    zoneColumn: String = "zone",
    zonesByFootColumn: String = "zone_avail",
) where S : ModelExecution<C>, C : Context, C : LegacyZonesContext, C : SharingStationsContext {
    val sharingProvider = SharingProvider(providerName, mode)

    val csvParser = CsvParser(errorHandling) { row ->
        SharingStationBuilder().apply {
            owner = sharingProvider
            uid = row(uidColumn)
            name = row(nameColumn)
            zonesByFoot = this@prepareSharingStations.context.prepareZonesByFoot(row, zonesByFootColumn).toMutableSet()
            location = Location(
                zone = context.getZone(row.long(zoneColumn)),
                coordinate = coordinateParser(row(coordinatesColumn)),
                roadAccess = null
            )
            initialVehicles = sharingProvider.prepareVehicles(
                count = row.int(vehicleCountColumn),

            ).toMutableSet()
        }
//        SharingStation(
//            owner = sharingProvider,
//            uid = row(uidColumn),
//            name = row(nameColumn),
//            zonesByFoot = context.prepareZonesByFoot(row, zonesByFootColumn),
//            location = ZoneLocationImpl(
//                zone = context.getZone(row.long(zoneColumn)),
//                coordinate = coordinateParser(row(coordinatesColumn)),
//            ),
//            initialVehicles = prepareVehicles(
//                count = row.int(vehicleCountColumn),
//                mode,
//                sharingProvider
//            )
//        ).also { it.vehicles.forEach { v -> v.returnTo(it) } }.weakerBuilder()
    }

    this.prepareStationsFile(csvParser, file, delimiter)
}

private fun <C> C.prepareZonesByFoot(row: Row, column: String): Set<Zone> where C : LegacyZonesContext {
    return row(column).split(",").map { id ->

        id.toLongOrNull()?.let {
            getZone(it)
        } ?: error(
            "Could not parse ZoneId $id (expected value of type Long) " +
                "in column $column row${row.index} of ${row.source}: ${row(column)}!"
        )
    }.toSet()
}

fun SharingProvider.prepareVehicles(count: Int, mode: Mode = this.mode): Set<SharingVehicle> {
    return (numberOfVehicles until numberOfVehicles + count).map {
        SharingVehicle(
            id = SharingVehicleId(it.toLong()),
            mode = mode,
            owner = this,
        )
    }.toSet()
}

fun <S, C> S.prepareStationsFile(
    parser: CsvParser<SharingStationBuilder>,
    file: File? = null,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : Context, C : SharingStationsContext {
    val path = this.context.demandFolder.path + "\\zone-repository\\sharing-stations.csv"
    val personFile = file ?: File(path)

    val resource = CsvResource(personFile, parser, delimiter)

    this.addStep(
        AddCsvStep(
            name = "load sharing stations csv",
            csv = resource,
            repository = context.sharingStationsRepository
        )
    )
}

fun <S, C> S.finishSharingStations() where S : ModelExecution<C>, C : Context, C : SharingStationsContext {
    this.addStep(BuildStep("finish sharing stations", context.sharingStationsRepository))
}

fun <S, C> S.loadSharingStations(
    providerName: String,
    mode: Mode,
) where S : ModelExecution<C>, C : Context, C : SharingStationsContext, C : LegacyZonesContext {
    this.prepareSharingStations(providerName = providerName, mode = mode)
    this.finishSharingStations()
}

private fun <C> C.getZone(id: Long) where C : LegacyZonesContext = requireNotNull(
    zoneRepository.getById(ZoneId(id)) ?: zoneColumnIndex[id.toInt()]
) {
    "Referenced ZoneId $id could not be found in zoneRepo:" +
        " ${zoneRepository.elements.map { it.id }.toList()}"
}
