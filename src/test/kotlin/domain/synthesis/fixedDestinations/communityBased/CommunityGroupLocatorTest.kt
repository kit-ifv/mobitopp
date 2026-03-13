package domain.synthesis.fixedDestinations.communityBased

import TestZone
import assertNotContains
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.synthesis.behavior.fixedDestinations.communityBased.CommunityBasedGroupLocator
import domain.synthesis.behavior.fixedDestinations.communityBased.CommunityNumber
import domain.synthesis.behavior.fixedDestinations.communityBased.CommuterDemandsMatrix
import domain.synthesis.behavior.fixedDestinations.communityBased.TrivialDemands
import domain.synthesis.data.Sex
import domain.synthesis.fixedDestinations.AsymmetricMockDistance
import domain.synthesis.householdgeneration.SynthesisTest
import org.junit.jupiter.api.assertThrows
import utils.ConsoleCaptor
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CommunityGroupLocatorTest : SynthesisTest() {
    private val testZone1 = TestZone(id = ZoneId(1L))
    private val testZone2 = TestZone(id = ZoneId(2L))
    private val testZone3 = TestZone(id = ZoneId(3L))

    private val home1 = testZone1.spawnFakeLoc()
    private val home2 = testZone2.spawnFakeLoc()

    private val work1 = testZone1.spawnFakeLoc()
    private val work2 = testZone2.spawnFakeLoc()
    private val work3 = testZone3.spawnFakeLoc()

    private val household1 = home1.createHousehold<Any> {
        person(20, Sex.MALE) {}
    }
    private val household2 = home2.createHousehold<Any> {
        person(10, Sex.MALE) {}
    }
    private val person1 = household1[0]
    private val person2 = household2[0]
    private val c1 = CommunityNumber(1)
    private val c2 = CommunityNumber(2)
    private val c3 = CommunityNumber(3)
    private val zoneCommunityMapping: Map<Zone, CommunityNumber> = mapOf(
        testZone1 to c1,
        testZone2 to c2,
        testZone3 to c3,

    )
    private lateinit var metric: AsymmetricMockDistance
    private lateinit var demand: CommuterDemandsMatrix
    private lateinit var strategy: TrivialDemands<Any>

    @BeforeTest
    fun setup() {
        metric = AsymmetricMockDistance()
        demand = CommuterDemandsMatrix(converter = { zoneCommunityMapping.getValue(it.zone) })
        strategy = TrivialDemands(metric)
    }

    @Test
    fun noDemandPromptsErrorMessage() {
        val exception = assertThrows<IllegalArgumentException> {
            val locator = CommunityBasedGroupLocator(demand, strategy, listOf(work3))
            locator.match(listOf(person1, person2))
        }
        assertContains(exception.message!!, c1.toString())
        assertContains(exception.message!!, c2.toString())
    }

    @Test
    fun specialMissingDemandPromptsErrorMessage() {
        demand[c2, c2] = 1.0
        val exception = assertThrows<IllegalArgumentException> {
            val locator = CommunityBasedGroupLocator(demand, strategy, listOf(work3))
            locator.match(listOf(person1, person2))
        }
        assertContains(exception.message!!, c1.toString())
        assertNotContains(exception.message!!, c2.toString())
    }

    @Test
    fun noFittingLocationsPromptsErrorMessage() {
        demand[c1, c1] = 1.0
        demand[c2, c2] = 1.0
        val location = testZone1.spawnFakeLoc()
        val exception = assertThrows<IllegalArgumentException> {
            val locator = CommunityBasedGroupLocator(demand, strategy, listOf(location))
            locator.match(listOf(person1, person2))
        }
        assertNotContains(exception.message!!, c1.toString())
        assertContains(exception.message!!, c2.toString())
    }

    @Test
    fun assignmentByTriviality() {
        demand[c1, c1] = 1.0
        demand[c1, c3] = 1.0
        demand[c2, c2] = 1.0

        val locator = CommunityBasedGroupLocator(demand, strategy, listOf(work1, work2, work3))

        metric[home1, work1] = 1.0
        metric[home1, work2] = 0.0
        metric[home1, work3] = 0.5

        val output = locator.match(person1)[0]
        // The output should be location 3, as location 2 is in a community that has no saturated demand
        assertEquals(work3, output.assignedLocation)
        assertEquals(person1, output.targetPerson)
    }

    @Test
    fun trivialAssignRespectsSaturation() {
        demand[c1, c1] = 1.0
        demand[c1, c3] = 1.0
        demand[c2, c2] = 1.0

        val locator = CommunityBasedGroupLocator(demand, strategy, listOf(work1, work2, work3))

        metric[home1, work1] = 1.0
        metric[home1, work2] = 0.0
        metric[home1, work3] = 0.5

        val mimicHousehold = home1.createHousehold<Any> {
            person(20, Sex.MALE) {}
        }
        val mimicPerson = mimicHousehold[0]
        assertNotEquals(mimicPerson, person1)
        assertEquals(mimicPerson.age, person1.age)
        assertEquals(mimicPerson.sex, person1.sex)
        val output = locator.match(person1, mimicPerson)
        assertEquals(2, output.size)

        val firstOutput = output[0]
        val secondOutput = output[1]

        firstOutput.nonInlineRun {
            assertEquals(person1, targetPerson)
            assertEquals(work3, assignedLocation)
        }

        secondOutput.nonInlineRun {
            assertEquals(mimicPerson, targetPerson)
            assertEquals(work1, assignedLocation)
        }
    }

    @Test
    fun oversaturation() {
        demand[c2, c2] = 1.0
        demand[c2, c3] = 1.0
        demand[c1, c1] = 1.0

        metric[home2, work2] = 1.0
        metric[home2, work3] = 2.5

        val captor = ConsoleCaptor()
        val locator = CommunityBasedGroupLocator(demand, strategy, listOf(work1, work2, work3))
        val output = locator.match(person2, person1, person2, person2, person1)

        val consoleOutput = captor.getText()
        assertContains(
            consoleOutput,
            "(3) to be assigned in community CommunityNumber(int=2)"
        )

        assertContains(
            consoleOutput,
            "(2) to be assigned in community CommunityNumber(int=1)"
        )
        output[0].nonInlineRun {
            assertEquals(person2, targetPerson)
            assertEquals(work2, assignedLocation)
        }

        output[1].nonInlineRun {
            assertEquals(person2, targetPerson)
            assertEquals(work3, assignedLocation)
        }

        output[2].nonInlineRun {
            assertEquals(person2, targetPerson)
            assertEquals(work2, assignedLocation)
        }

        output[3].nonInlineRun {
            assertEquals(person1, targetPerson)
            assertEquals(work1, assignedLocation)
        }
        output[4].nonInlineRun {
            assertEquals(person1, targetPerson)
            assertEquals(work1, assignedLocation)
        }
    }
}

// To get syntax highlighting if something fails
private fun <T> T.nonInlineRun(block: T.() -> Unit) {
    this.block()
}
