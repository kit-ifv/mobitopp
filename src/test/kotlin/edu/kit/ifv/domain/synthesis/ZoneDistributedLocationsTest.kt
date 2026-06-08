package edu.kit.ifv.domain.synthesis
import edu.kit.ifv.LandUseParser
import edu.kit.ifv.NetfileParser
import edu.kit.ifv.UrbanAtlasGenerator
import edu.kit.ifv.VisumLocale
import edu.kit.ifv.ZoneType
import edu.kit.ifv.domain.shared.enums.areatype.RegioStaR17
import edu.kit.ifv.domain.shared.enums.areatype.RegionType
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.road.RoadAccess
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRegionType
import edu.kit.ifv.domain.shared.location.zone.attributes.HasVisumId
import edu.kit.ifv.domain.shared.location.zone.toZoneId
import edu.kit.ifv.domain.synthesis.behavior.householdlocation.ZoneDistributedLocations
import edu.kit.ifv.readPolyZones
import edu.kit.ifv.units.Hemisphere
import edu.kit.ifv.utils.csv.DefaultCsvReader
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
        utmHemisphere = Hemisphere.NORTHERN,
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
        typeEncoder = ::ZoneType,
    ).parse(Path("src/test/resources/synthesis/250410_landuse_rastatt.geojson"))

    private val polyZones = netfileParser.readPolyZones().associateBy { it.id }
    private val distributor = UrbanAtlasGenerator(
        landUseModel = landUseModel,
        weightFunction = { weights.getOrDefault(it, defaultValue = 0.0) },
        utmZone = netfileParser.utmZone,
        utmHemisphere = netfileParser.utmHemisphere,
    )

    private val distributedLocations = ZoneDistributedLocations<NecessaryAttributes, TestHouseHold>(
        polyZones,
        distributor,
    )

    private data class NecessaryAttributes(override val visumId: Int, override val regionType: RegionType) :
        HasVisumId,
        HasRegionType

    private class AdvancedZone(override val zoneId: ZoneId, override val attributes: NecessaryAttributes) :
        Zone<NecessaryAttributes> {
        constructor(id: Number, visumId: Number) : this(
            zoneId = id.toZoneId(),
            NecessaryAttributes(
                visumId.toInt(),
                RegioStaR17.URBAN_AREA_METRO,
            ),
        )

        override val centroidLocation: StandardLocation
            get() = throw UnsupportedOperationException()
    }

    // TODO this test is no longer testing sensible things since the location rework
    @Test
    fun singleAssign() {
        val generated = distributedLocations.generateLocation(
            AdvancedZone(1, 1),
            TestHouseHold("MyHousehold"),
        )

        assertNotNull(generated)
        assertEquals(generated.attributes.roadAccess, RoadAccess.INVALID)
    }

    @Test
    fun groupAssign() {
        val size = 20
        val houseHolds = List(size) { TestHouseHold() }
        val generated = distributedLocations.generateLocations(
            AdvancedZone(1, 35),
            houseHolds,
        )

        assert(generated.size == size)
        generated.forEach { pair ->
            assertEquals(pair.second.attributes.roadAccess, RoadAccess.INVALID)
            assert(pair.first.name == "TestHouseHold")
        }
    }
}

private class TestHouseHold(val name: String = "TestHouseHold")
