package domain.synthesis

import LandUseParser
import NetfileParser
import UrbanAtlasGenerator
import VisumLocale
import ZoneType
import domain.shared.enums.ZoneClassification
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.location.Location
import domain.shared.location.RoadAccess
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.synthesis.behavior.householdlocation.ZoneDistributedLocations
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.Hemisphere
import edu.kit.ifv.units.meters
import readPolyZones
import utils.csv.DefaultCsvReader
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ZoneDistributedLocationsTest {
    val leopoldLocale = VisumLocale()

    init {
        leopoldLocale.connector {
            travelTimeCar = "T0_TSYS(BS)"
        }
    }

    val netfileParser = NetfileParser(
        file = Path("src/test/resources/synthesis/leopoldshafen.net"),
        locale = leopoldLocale,
        utmZone = 32,
        utmHemisphere = Hemisphere.NORTHERN
    )

    val weights: Map<ZoneType, Double> =
        DefaultCsvReader(Path("src/test/resources/synthesis/opportunity_weights_landuse.csv"))
            .rows()
            .filter { it.invoke("activityType") == "Home" }
            .map {
                Pair(ZoneType(it.invoke("landUseType").toInt()), it.invoke("weight").toDouble())
            }.toMap()
    val landUseModel = LandUseParser(
        zoneTypePropertyName = "landUseType",
        typeEncoder = ::ZoneType
    ).parse(Path("src/test/resources/synthesis/250410_landuse_rastatt.geojson"))

    private val polyZones = netfileParser.readPolyZones().associateBy { it.id }
    private val distributor = UrbanAtlasGenerator(
        landUseModel = landUseModel,
        weightFunction = { weights.getOrDefault(it, defaultValue = 0.0) },
        utmZone = netfileParser.utmZone,
        utmHemisphere = netfileParser.utmHemisphere
    )
    private val distributedLocations = ZoneDistributedLocations<TestHouseHold>(polyZones, distributor)
    //TODO this test is no longer testing sensible things since the location rework
    @Test
    fun singleAssign() {
        val generated = distributedLocations.generateLocation(
            TestZone(visumId = 1L),
            TestHouseHold("MyHousehold")
        )

        assertNotNull(generated)
        assertEquals(generated.roadAccess, RoadAccess.INVALID)
    }

    @Test
    fun groupAssign() {
        val size = 20
        val houseHolds = List(size) { TestHouseHold() }
        val generated = distributedLocations.generateLocations(
            TestZone(visumId = 35L),
            houseHolds
        )

        assert(generated.size == size)
        generated.forEach { pair ->
            assertEquals(pair.second.roadAccess, RoadAccess.INVALID)
            assert(pair.first.name == "TestHouseHold")
        }
    }
}

@Suppress("LongParameterList")
private class TestZone(
    override var parkingPlaces: Int = 1,
    override val visumId: Long = 0L,
    override val name: String = "TestZone",
    override val regionType: RegionType = RegioStaR17.METROPOLE,
    override val classification: ZoneClassification = ZoneClassification.STUDY_AREA,
    override val isDestination: Boolean = true,
    override val relief: Distance = 0.meters,
) : Zone(
    id = ZoneId(visumId),
    centroid = Location.wgs(9.0, 50.0),
    seed = 0L
)

private class TestHouseHold(
    val name: String = "TestHouseHold",
)
