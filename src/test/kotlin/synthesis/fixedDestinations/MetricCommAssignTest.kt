package synthesis.fixedDestinations

import choicemodels.TestZone
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.LegacyActivityType
import domain.enums.Regiostar17
import domain.enums.ZoneClassification
import domain.location.FlightDistance
import domain.location.Location
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import synthesis.CommuteDistance
import synthesis.domain.SynthesisHousehold
import synthesis.domain.SynthesisPerson
import synthesis.fixedDestinations.BiMap.Companion.toBiMap
import units.Distance
import units.GPSCoordinate
import units.kilometers
import usecases.AttractivenessModel
import usecases.steps.legacyData.defaultCsvParser


class MetricCommAssignTest {

    private data class CommuteDistancer(override val distanceWork: Distance) : CommuteDistance

    private val attractivenessModel: AttractivenessModel by lazy {
        val attractivenessModel = AttractivenessModel { _, _ ->
            1000.0
        }
        attractivenessModel
    }

    @Test
    fun worklocationAssigmentConsideringDistance() {
        val household = SynthesisHousehold<CommuteDistance>()

        val person = SynthesisPerson(household, CommuteDistancer(1.kilometers))
        val zones =
            defaultCsvParser(regionTypeCodePlan = Regiostar17).parse("src/test/resources/synthesis/zones.csv")
                .toList()


        household.location = DebugZoneAssigner.getLocation(zones.first())
        val metric = FlightDistance()

        val assigner = MetricCommAssign(
            CommuterMatrix.parse(
                zoneMapping = zones.associateBy { it.id },
            ),
            metric = metric,
            attractivenessModel = attractivenessModel,
            findProperWorkspace = GREEDY_BY_DISTANCE,
            zoneLocationAssigner = DebugZoneAssigner
        )
        assigner.find(person, LegacyActivityType.WORK)
    }
    /*
        point: GPSCoordinate = BIELEFELD,
    visumId: Long = 1L,
    matrixColumn: Int = 0,
    name: String = "HomeZone",
    areaType: AreaType = ZoneAreaType.DEFAULT,
    regionType: Int = 0,
    classification: ZoneClassification = ZoneClassification.STUDY_AREA,
    override var parkingPlaces: Int = 1,
    isDestination: Boolean = true,
    relief: Distance = 0.meters,
    id: ZoneId = ZoneId(1L)
     */
    @Test
    fun communitySaturationIsProperlyUpdated() {
        val zone1 = TestZone(
            id = ZoneId(1),

            areaType = Regiostar17.REGIOPOLE,
        )
        val zone2 = TestZone(
            id = ZoneId(2),
            areaType = Regiostar17.REGIOPOLE,

        )
        val zone3 = TestZone(
            id = ZoneId(3),
            areaType = Regiostar17.REGIOPOLE,
        )
        val c1 = CommunityNumber(1)
        val c2 = CommunityNumber(2)
        val communities = mapOf(
            zone1.id to c1,
            zone2.id to c1,
            zone3.id to c2,
        )

        val fakeMatrix = CommuterMatrix(
            communities.toBiMap(),
            listOf(
                CommuterInfo(c1, c2, 1),
                CommuterInfo(c1, c1, 9999)
            ),
            zoneMapping = listOf(zone1, zone2, zone3).associateBy { it.id },
        )
        val household = SynthesisHousehold<CommuteDistance>()

        val person = SynthesisPerson(household, CommuteDistancer(1.kilometers))

        val household2 = SynthesisHousehold<CommuteDistance>()

        val person2 = SynthesisPerson(household, CommuteDistancer(1.kilometers))
        household.location = CentroidAssigner.getLocation(zone1)
        household2.location = CentroidAssigner.getLocation(zone2)
        val assignStrat = MetricCommAssign(
            fakeMatrix,
            FlightDistance(),
            attractivenessModel,
            GREEDY_BY_DISTANCE,
            CentroidAssigner
        )
        assertEquals(assignStrat.find(person, LegacyActivityType.WORK).requireZone(), zone3)
        assertNotEquals(assignStrat.find(person2, LegacyActivityType.WORK).requireZone(), zone3)
    }
}
