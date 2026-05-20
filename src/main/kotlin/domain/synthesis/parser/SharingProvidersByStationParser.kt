package domain.synthesis.parser

import core.modelsteps.resources.Repository
import domain.shared.enums.Mode
import domain.shared.location.Impedance
import domain.shared.location.PointCreator
import domain.shared.location.RoadAccess
import domain.shared.location.StandardLocation
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.MutableSharingStation
import domain.synthesis.data.SharingProviderId
import domain.synthesis.data.SharingStationId
import edu.kit.ifv.units.Distance
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.double
import utils.csv.int
import utils.csv.long

// operating hours
val allDay = 0..24

// id providers
object GlobalSharingProviderIdCounter : (Row) -> SharingProviderId {
    private var counter = 0L
    override operator fun invoke(row: Row) = SharingProviderId(counter++)
}

object GlobalSharingStationIdCounter : (Row) -> SharingStationId {
    private var counter = 0L
    override operator fun invoke(row: Row) = SharingStationId(counter++)
}

// parsers for zones by foot
fun onlySameZoneByFoot(): (Row, Mode, StandardLocation, GetZone) -> List<Zone> = { _, _, stationLocation, getZone ->
    listOf(getZone(stationLocation.zoneID))
}

fun parseCommaSeparatedZones(column: String): (Row, Mode, StandardLocation, GetZone) -> List<Zone> = { row, _, _, getZone ->
    row(column).split(',').map { getZone(ZoneId(it.toLong())) }
}

fun filterZonesByFootInRadius(
    threshold: Distance,
    zoneRepository: Repository<Zone, ZoneId>,
    impedance: Impedance,
): (
    Row,
    Mode,
    StandardLocation,
    GetZone
) -> List<Zone> = { _, mode, stationLocation, getZone ->
    val zone = getZone(stationLocation.zoneID)
    zoneRepository.elements.filter {
        impedance.distance(zone.centroid, it.centroid, mode) <= threshold
    }.toList()
}

// station location parser
fun locationAtZoneCentroid(): (Row, Zone) -> StandardLocation = { _, zone ->
    zone.centroid
}

fun parseLocationXY(
    xColumn: String = "x",
    yColumn: String = "y",
): (Row, Zone) -> StandardLocation = { row, zone ->

    StandardLocation.Companion(
        position = PointCreator.createWGS(row.double(xColumn), row.double(yColumn)),
        zone = zone,
        roadAccess = RoadAccess.INVALID,
    )
}

typealias GetZone = (ZoneId) -> Zone

data class SharingProviderByStationCsvConfig(
    var columns: SharingProviderByStationCsvColumns = SharingProviderByStationCsvColumns(),
    var sharingMode: Mode,
    var getZone: GetZone,
    var zonesByFoot: (Row, Mode, StandardLocation, GetZone) -> List<Zone>,
    var locationParser: (Row, Zone) -> StandardLocation,
    var providerIdSource: (Row) -> SharingProviderId,
    var stationIdSource: (Row) -> SharingStationId,
    var operatingHours: IntRange,
    var errorHandling: ErrorHandling,
    val seed: Long,
)

data class SharingProviderByStationCsvColumns(
    val provider: String = "owner",
    val numVehicles: String = "numVehicles",
    val uid: String = "uid",
    val name: String = "name",
    val zone: String = "zone",
)

fun createSharingProvidersByStationParser(
    csvConfig: SharingProviderByStationCsvConfig
): CsvParser<MutableSharingProvider> = csvConfig.run {
    val providers = mutableMapOf<String, MutableSharingProvider>()

    CsvParser.Companion { row ->

        val providerName = row(columns.provider)
        var newProvider = false
        val provider = providers.computeIfAbsent(providerName) { n ->
            newProvider = true
            MutableSharingProvider(providerIdSource(row)) {
                this.name = n
                this.mode = sharingMode
                this.operatingHours = csvConfig.operatingHours
            }
        }

        val uid = row(columns.uid)
        val name = row(columns.name)
        val initVehicles = row.int(columns.numVehicles)
        val zone = getZone(ZoneId(row.long(columns.zone)))
        val location = locationParser(row, zone)
        val zonesByFoot = zonesByFoot(row, sharingMode, location, getZone)

        MutableSharingStation(stationIdSource(row), owner = provider) {
            this.uid = uid
            this.name = name
            this.location = location
            this.initialVehicleCount = initVehicles
            this.zonesByFoot.addAll(zonesByFoot)
        }

        provider.takeIf { newProvider }
    }
}