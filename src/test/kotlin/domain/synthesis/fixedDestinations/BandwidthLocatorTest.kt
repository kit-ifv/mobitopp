@file:Suppress("FunctionNameMaxLength")

package domain.synthesis.fixedDestinations

import BIELEFELD
import ZoneTestAttributesFake
import core.datastructure.kdtree.WithMetric
import core.datastructure.kdtree.discardMetric
import domain.shared.enums.LegacyActivityType
import domain.shared.location.Location
import domain.shared.location.RoadAccess
import domain.shared.location.StandardLocation
import domain.shared.location.ZoneId
import domain.shared.location.toPoint
import domain.shared.location.zone.MaximalZone
import domain.synthesis.ControllableAttractiveness
import domain.synthesis.behavior.fixedDestinations.BandwidthLocator
import domain.synthesis.behavior.fixedDestinations.BandwidthParameters
import domain.synthesis.behavior.fixedDestinations.LocationAlternative
import domain.synthesis.behavior.fixedDestinations.standardBandwidthModel
import domain.synthesis.data.Sex
import domain.synthesis.householdgeneration.SynthesisTest
import edu.kit.ifv.mobitopp.discretechoice.selection.SelectionFunction
import edu.kit.ifv.units.kilometers
import org.junit.jupiter.api.Test
import kotlin.math.E
import kotlin.test.BeforeTest
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BandwidthLocatorTest : SynthesisTest() {
    private val testZone = MaximalZone(ZoneId(1L), ZoneTestAttributesFake(), BIELEFELD.toPoint())
    private val myActivityType = LegacyActivityType.LEISURE_SIGHTSEEING
    private lateinit var attractivenessModel: ControllableAttractiveness

    private fun MaximalZone.spawnUTM(eOffset: Number, nOffset: Number): StandardLocation = StandardLocation(
        Location.utm(500000.0 + eOffset.toDouble(), 5000000.0 + nOffset.toDouble()).position,
        this,
        RoadAccess.INVALID,
    )

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
        val hh = home.createHousehold(Attrs::copy) {
            person {
                Attrs(
                    10,
                    Sex.MALE,
                    3.kilometers,
                )
            }

            person {
                Attrs(
                    10,
                    Sex.MALE,
                    42.kilometers,
                )
            }
        }
        val person = hh[0]
        val person2 = hh[1]

        val parameters = BandwidthParameters(
            poleRadius = 1.5.kilometers,
            bDistance = 1.0,
            aDistance = 1.0,
        )
        // To avoid randomness, we overwrite the selection function to pick the maximum utility instead.
        val model = standardBandwidthModel.build(parameters).copy(
            selectionFunction = SelectionFunction { o, _ -> o.maxBy { it.value }.key },
        )
        val locator = BandwidthLocator<Attrs>(
            listOf(loc1, loc2, loc3, loc4, loc5, loc6, loc7),
            attractivenessModel,
            myActivityType,
            parameters = parameters,
            model = model,
        )

        val output = locator.validTargetsForAgent(person).discardMetric()
        assertContentEquals(output, listOf(loc2, loc3, loc4))

        val location = locator.locate(person)
        assertEquals(location, loc2)

        val otherParameters = BandwidthParameters(
            poleRadius = 0.kilometers,
            bDistance = 1.0,
            aDistance = 1.0,
        )
        val model2 = standardBandwidthModel.build(otherParameters).copy(
            selectionFunction = SelectionFunction { o, _ -> o.maxBy { it.value }.key },
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
            aDistance = 1.0,
        )
        val model = standardBandwidthModel.build(parameters)
        val sit1 = WithMetric(
            testZone.spawnFakeLoc(),
            1.kilometers,
        )
        val sit2 = WithMetric(testZone.spawnFakeLoc(), 2.kilometers)
        val sit3 = WithMetric(testZone.spawnFakeLoc(), 3.kilometers)
        context(LocationAlternative(attractivenessModel, myActivityType)) {
            assertEquals(1.0, model.utility(sit1))
            assertEquals(0.5, model.utility(sit2))
            assertEquals(1.0 / 3, model.utility(sit3))
        }

        val otherParameters = BandwidthParameters(
            poleRadius = 2.5.kilometers,
            bDistance = 1.5,
            aDistance = 2.0,
        )
        val model2 = standardBandwidthModel.build(otherParameters)
        context(LocationAlternative(attractivenessModel, myActivityType)) {
            assertEquals(1.0 / 1.5, model2.utility(sit1))
            assertEquals(1.0 / 6.0, model2.utility(sit2))
            assertEquals(1.0 / (1.5 * 9), model2.utility(sit3))
        }
    }
}
