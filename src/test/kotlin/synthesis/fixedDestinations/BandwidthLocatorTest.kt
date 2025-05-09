package synthesis.fixedDestinations

import TestZone
import datastructure.discardMetric
import domain.data.Sex
import domain.data.Zone
import domain.data.ZoneId
import domain.location.Location
import modeling.discreteChoice.SelectionFunction
import org.junit.jupiter.api.Test
import synthesis.CommuteDistance
import synthesis.ControllableAttractiveness
import synthesis.householdgeneration.SynthesisTest
import units.Distance
import units.Hemisphere
import units.UTMPosition
import units.kilometers
import usecases.LegacyActivityType
import kotlin.math.E
import kotlin.test.BeforeTest
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BandwidthLocatorTest : SynthesisTest() {
    private val testZone = TestZone(id = ZoneId(1L))
    private val myActivityType = LegacyActivityType.LEISURE_SIGHTSEEING
    private lateinit var attractivenessModel: ControllableAttractiveness

    private fun Zone.spawnUTM(eOffset: Number, nOffset: Number): Location {
        val utm = UTMPosition(500000.0 + eOffset.toDouble(), 5000000.0 + nOffset.toDouble(), 32, Hemisphere.NORTHERN)
        return Location(utm.toWGS84(), this, null)
    }

    private fun Location.toSituation(distance: Distance): LocationAlternative {
        return LocationAlternative(this, distance, attractivenessModel, myActivityType)
    }

    @BeforeTest
    fun setup() {
        attractivenessModel = ControllableAttractiveness()
        attractivenessModel[testZone.id, myActivityType] = E
    }

    @Test
    fun testProperExtraction() {
        val home = testZone.spawnUTM(0, 0)
        val loc1 = testZone.spawnUTM(0, 1000)
        val loc2 = testZone.spawnUTM(0, 2000)
        val loc3 = testZone.spawnUTM(0, 3000)
        val loc4 = testZone.spawnUTM(0, 4000)
        val loc5 = testZone.spawnUTM(0, 5000)
        val loc6 = testZone.spawnUTM(0, 6000)
        val loc7 = testZone.spawnUTM(0, 7000)
        val hh = home.createHousehold<CommuteDistance> {
            person(10, Sex.MALE) {
                object : CommuteDistance {
                    override val distanceWork: Distance = 3.kilometers
                }
            }

            person(10, Sex.MALE) {
                object : CommuteDistance {
                    override val distanceWork: Distance = 42.kilometers
                }
            }
        }
        val person = hh[0]
        val person2 = hh[1]

        val parameters = BandwidthParameters(
            poleRadius = 1.5.kilometers,
            bDistance = 1.0,
            aDistance = 1.0
        )
        // To avoid randomness, we overwrite the selection function to pick the maximum utility instead.
        val model = standardBandwidthModel.build(parameters).copy(
            selectionFunction = SelectionFunction { o, _ -> o.maxBy { it.value }.key }
        )
        val locator = BandwidthLocator(
            listOf(loc1, loc2, loc3, loc4, loc5, loc6, loc7),
            attractivenessModel,
            myActivityType,
            parameters = parameters,
            model = model
        )

        val output = locator.validTargetsForAgent(person).discardMetric()
        assertContentEquals(output, listOf(loc2, loc3, loc4))

        val location = locator.locate(person)
        assertEquals(location, loc2)

        val otherParameters = BandwidthParameters(
            poleRadius = 0.kilometers,
            bDistance = 1.0,
            aDistance = 1.0
        )
        val model2 = standardBandwidthModel.build(otherParameters).copy(
            selectionFunction = SelectionFunction { o, _ -> o.maxBy { it.value }.key }
        )

        locator.parameters = otherParameters
        locator.model = model2

        val location2 = locator.locate(person2)
        assertEquals(location2, loc1)
    }

    @Test
    fun testBandwidthDiscreteChoiceModel() {
        val parameters = BandwidthParameters(
            poleRadius = 2.5.kilometers,
            bDistance = 1.0,
            aDistance = 1.0
        )
        val model = standardBandwidthModel.build(parameters)
        val sit1 = testZone.spawnFakeLoc().toSituation(1.kilometers)
        val sit2 = testZone.spawnFakeLoc().toSituation(2.kilometers)
        val sit3 = testZone.spawnFakeLoc().toSituation(3.kilometers)
        assertEquals(1.0, model.utility(sit1))
        assertEquals(0.5, model.utility(sit2))
        assertEquals(1.0 / 3, model.utility(sit3))

        val otherParameters = BandwidthParameters(
            poleRadius = 2.5.kilometers,
            bDistance = 1.5,
            aDistance = 2.0
        )
        val model2 = standardBandwidthModel.build(otherParameters)
        assertEquals(1.0 / 1.5, model2.utility(sit1))
        assertEquals(1.0 / 6.0, model2.utility(sit2))
        assertEquals(1.0 / (1.5 * 9), model2.utility(sit3))
    }
}
