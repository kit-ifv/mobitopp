package synthesis

import CRS84
import LandUseModel
import LandUseParser
import LanduseDistributedCoordinates
import NetfileParser
import VisumLocale
import ZoneType
import asLocation
import domain.data.MutableLegacyZone
import domain.data.ZoneId
import domain.enums.AreaType
import domain.enums.Regiostar17
import domain.enums.ZoneClassification
import readPolyZones
import units.Distance
import units.GPSCoordinate
import units.Hemisphere
import units.meters
import utils.csv.DefaultCsvReader
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ZoneDistributedLocationsTest {
    val netfileParser = NetfileParser(
        Path(""),
        locale = VisumLocale(),
        utmZone = 32,
        utmHemisphere = Hemisphere.NORTHERN
    )

    val weights: Map<ZoneType, Double> =
        DefaultCsvReader(Path("src/test/resources/synthesis/opportunity_weights_landuse.csv").toFile())
            .rows()
            .filter { it.invoke("activityType") == "Home" }
            .map {
                Pair(ZoneType(it.invoke("landUseType").toInt()), it.invoke("weight").toDouble())
            }.toMap()
    val landUseModel: LandUseModel = LandUseParser(
        gpsParser = CRS84(),
        zoneTypePropertyName = "landUseType"
    ).parse(Path("src/test/resources/synthesis/250410_landuse_rastatt.geojson"))

    private val polyZones = netfileParser.readPolyZones().associateBy { it.id }
    private val distributor = LanduseDistributedCoordinates(
        landUseModel = landUseModel,
        weights = weights,
        utmZone = netfileParser.utmZone,
        utmHemisphere = netfileParser.utmHemisphere
    )
    private val distributedLocations = ZoneDistributedLocations<TestHouseHold>(polyZones, distributor)

    @Test
    fun singleAssign() {
        val generated = distributedLocations.generateLocation(
            TestZone(visumId = 6113),
            TestHouseHold("MyHousehold")
        )

        assert(generated.zone?.visumId == 6113.toLong())
        assertNotNull(generated.zone)
        assertNull(generated.roadAccess)
    }

    @Test
    fun groupAssign() {
        val size = 20
        val houseHolds = List(size) { TestHouseHold() }
        val generated = distributedLocations.generateLocations(
            TestZone(visumId = 6113),
            houseHolds
        )

        assert(generated.size == size)
        generated.forEach { pair ->
            assertNotNull(pair.second.zone)
            assert(pair.second.zone?.visumId == 6113.toLong())
            assertNull(pair.second.roadAccess)
            assert(pair.first.name == "TestHouseHold")
        }
    }
}

@Suppress("LongParameterList")
private class TestZone(
    point: GPSCoordinate = GPSCoordinate.decimalDegree(50.0, 9.5),
    visumId: Long = 6112L,
    matrixColumn: Int = 0,
    name: String = "TestZone",
    regionType: AreaType = Regiostar17.METROPOLE,
    classification: ZoneClassification = ZoneClassification.STUDY_AREA,
    override var parkingPlaces: Int = 1,
    isDestination: Boolean = true,
    relief: Distance = 0.meters,
    id: ZoneId = ZoneId(1L)
) : MutableLegacyZone(
    id,
    point.asLocation(),
    42L,
    {
        this.visumId = visumId
        this.name = name
        this.regionType = regionType
        this.classification = classification
        this.parkingPlaces = parkingPlaces
        this.isDestination = isDestination
        this.relief = relief
        this.matrixColumn = matrixColumn
    }
) {

    override fun toString(): String {
        return "TestZone$id"
    }
}

private class TestHouseHold(
    val name: String = "TestHouseHold",
)