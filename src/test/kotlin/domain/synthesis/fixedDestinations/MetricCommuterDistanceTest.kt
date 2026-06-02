@file:Suppress("FunctionMaxNameLength")

package domain.synthesis.fixedDestinations

import TestZone
import domain.shared.location.DistanceMetric
import domain.shared.location.ZoneId
import domain.shared.location.attributes.HasZoneId
import domain.synthesis.behavior.fixedDestinations.communityBased.CommunityNumber
import domain.synthesis.behavior.fixedDestinations.communityBased.CommuterDistance
import domain.synthesis.behavior.fixedDestinations.communityBased.MetricCommuterDistance
import domain.synthesis.behavior.fixedDestinations.communityBased.MutableCommunityDemand
import domain.synthesis.data.Sex
import domain.synthesis.householdgeneration.SynthesisTest
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.WGS84Coordinate
import edu.kit.ifv.units.kilometers
import edu.kit.ifv.units.toDistance
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import utils.ConsoleCaptor
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MetricCommuterDistanceTest : SynthesisTest() {
    private val testZone1 = TestZone(1)
    private val testZone2 = TestZone(2)
    private val testZone3 = TestZone(3)
    private val home1 = testZone1.spawnFakeLoc()
    private val home2 = testZone1.spawnFakeLoc()
    private val work1 = testZone1.spawnFakeLoc()
    private val work2 = testZone2.spawnFakeLoc()
    private val work3 = testZone3.spawnFakeLoc()

    private val defaultHome = home1.createHousehold(Attrs::copy) {
        person {
            Attrs(
                age = 10,
                sex = Sex.MALE,
                distanceWork = 1.kilometers,
            )
        }
        person {
            Attrs(
                age = 10,
                sex = Sex.FEMALE,
                distanceWork = 2.kilometers,
            )
        }
        person {
            Attrs(
                age = 20,
                sex = Sex.FEMALE,
                distanceWork = (-2).kilometers,
            )
        }
    }

    private val secondHome = home2.createHousehold(Attrs::copy) {
        person {
            Attrs(
                age = 10,
                sex = Sex.MALE,
                distanceWork = 1.kilometers,
            )
        }
    }
    private val person1 = defaultHome[0]
    private val person2 = defaultHome[1]
    private val person3 = defaultHome[2]
    private val person4 = secondHome[0]

    private val distances = AsymmetricMockDistance().apply {
        this[home1, work1] = .5
        this[home1, work2] = 1.0
        this[home1, work3] = 1.5
        this[home2, work1] = .7
        this[home2, work2] = 1.2
        this[home2, work3] = 1.7
    }

    private val strategy = MetricCommuterDistance<Attrs>(distances)

    @Test
    fun initialSelfTest() {
        assertNotEquals(home1, home2)
        assertNotEquals(home1, home2)
        assertNotEquals(home1, work1)
        assertNotEquals(home1, work2)
        assertNotEquals(home1, work3)
        assertNotEquals(testZone1, testZone2)
    }

    @Test
    fun properDifferenceToCommuteDistance() {
        val strategy = MetricCommuterDistance<Attrs>(distances)

        assertEquals(strategy.differenceToCommuteDistance(person1, work1), .5.kilometers)
        assertEquals(strategy.differenceToCommuteDistance(person1, work2), 0.kilometers)
        assertEquals(strategy.differenceToCommuteDistance(person1, work3), .5.kilometers)

        assertEquals(strategy.differenceToCommuteDistance(person2, work1), 1.5.kilometers)
        assertEquals(strategy.differenceToCommuteDistance(person2, work2), 1.kilometers)
        assertEquals(strategy.differenceToCommuteDistance(person2, work3), .5.kilometers)

        assertEquals(strategy.differenceToCommuteDistance(person3, work1), 2.5.kilometers)
        assertEquals(strategy.differenceToCommuteDistance(person3, work2), 3.kilometers)
        assertEquals(strategy.differenceToCommuteDistance(person3, work3), 3.5.kilometers)

        assertEquals(strategy.differenceToCommuteDistance(person4, work1), .3.kilometers)
        assertEquals(strategy.differenceToCommuteDistance(person4, work2), .2.kilometers)
        assertEquals(strategy.differenceToCommuteDistance(person4, work3), .7.kilometers)
    }

    @Test
    fun throwsNoExceptionWhenNoAgentIsPresent() {
        val demand = MutableCommunityDemand(
            converter = { CommunityNumber(-42) },
            communityID = CommunityNumber(-42),
        )
        // The strategy should not throw an exception if no agent is there to be assigned a location
        assertDoesNotThrow {
            strategy.assign(listOf(), demand, listOf())
        }
    }

    @Test
    fun throwsExceptionWhenNoLocationsArePresent() {
        val demand = MutableCommunityDemand(
            converter = { CommunityNumber(-42) },
            communityID = CommunityNumber(-42),
        )
        // If a person is present and no location, there should be an exception
        assertThrows<IllegalArgumentException> {
            strategy.assign(listOf(person1), demand, listOf())
        }
    }

    @Test
    fun respectsSaturationLevels() {
        val demand = generateStandardDemand()
        val output = strategy.assign(listOf(person1, person2, person3), demand, listOf(work1, work2, work3))
        assertEquals(output[0], work2)
        assertEquals(output[1], work1)
        assertEquals(output[2], work1)
    }

    @Test
    fun copiesOfAgentAreNotDiscarded() {
        val demand = generateStandardDemand()
        val output = strategy.assign(listOf(person4, person4), demand, listOf(work1))
        assertEquals(output.size, 2)
    }

    @TestFactory
    fun configurations(): List<DynamicTest> {
        val e = listOf(
            listOf(person1),
            listOf(person2),
            listOf(person3),
            listOf(person4),
            listOf(person2, person1, person3),
            listOf(person4, person3, person2),
            listOf(person4, person3, person2, person1),
            listOf(person3, person1, person4, person2),
            listOf(person4, person1, person2, person3),
            listOf(person3, person2, person1, person4),

        )
        val locations = listOf(
            listOf(work2),
            listOf(work3),
            listOf(work1),
            listOf(work2),
            listOf(work3, work1, work1),
            listOf(work2, work1, work1),
            listOf(work2, work1, work1, work2),
            listOf(work1, work2, work1, work3),
            listOf(work2, work1, work1, work1),
            listOf(work1, work3, work1, work2),
        )
        return e.zip(locations).map {
            DynamicTest.dynamicTest(it.toString()) {
                val captor = ConsoleCaptor()
                val agents = it.first
                val destinations = listOf(work1, work2, work3)
                val demand = generateStandardDemand()

                val output = strategy.assign(agents, demand, destinations)
                val text = captor.getText()
                if (agents.size > 3) {
                    assertContains(text, "the total demand 3.0")
                    assertFalse(text.isEmpty())
                } else {
                    assertTrue(text.isEmpty())
                }

                assertContentEquals(output, it.second)
            }
        }
    }

    @Test
    fun testLocationBased() {
        val strategy = CommuterDistance<Attrs>()
        val home = testZone1.spawnLocation(WGS84Coordinate.decimalDegree(0.0, 0.0))
        val household = home.createHousehold(Attrs::copy) {
            person {
                Attrs(
                    10,
                    Sex.MALE,
                    1.0.kilometers,
                )
            }
        }
        val work1 = testZone1.spawnLocation(WGS84Coordinate.decimalDegree(0.0, 0.0))

        assertEquals(strategy.differenceToCommuteDistance(household[0], work1), 1.0.kilometers)
    }

    private fun generateStandardDemand(): MutableCommunityDemand {
        val zoneCommunityMapping: Map<ZoneId, CommunityNumber> = mapOf(
            testZone1.zoneId to CommunityNumber(1),
            testZone2.zoneId to CommunityNumber(2),
            testZone3.zoneId to CommunityNumber(2),

        )

        val demand = MutableCommunityDemand(
            converter = { zoneCommunityMapping.getValue(it.zoneId) },
            communityID = CommunityNumber(1),
        )
        demand[2] = 1.0
        demand[1] = 2.0
        return demand
    }
}

class SymmetricMockDistance(default: Distance = 0.kilometers) : AsymmetricMockDistance(default) {

    override operator fun set(origin: HasZoneId, destination: HasZoneId, value: Number) {
        map.getOrPut(origin) { mutableMapOf() }[destination] =
            value.toDouble().toDistance(DistanceUnit.KILOMETERS)
        map.getOrPut(destination) { mutableMapOf() }[origin] =
            value.toDouble().toDistance(DistanceUnit.KILOMETERS)
    }
}

open class AsymmetricMockDistance(private val default: Distance = 0.kilometers) : DistanceMetric {
    protected val map: MutableMap<HasZoneId, MutableMap<HasZoneId, Distance>> = mutableMapOf()

//    open operator fun set(origin: ZoneId, destination: ZoneId, value: Number) {
//        map.getOrPut(origin) { mutableMapOf() }[destination] = value.toDouble().toDistance(DistanceUnit.KILOMETERS)
//    }

    open operator fun set(origin: HasZoneId, destination: HasZoneId, value: Number) {
        map.getOrPut(origin) { mutableMapOf() }[destination] =
            value.toDouble().toDistance(DistanceUnit.KILOMETERS)
    }

//    operator fun get(origin: ZoneId, destination: ZoneId): Distance =
//        (map[origin] ?: mutableMapOf())[destination] ?: default
    operator fun get(origin: HasZoneId, destination: HasZoneId): Distance =
        (map[origin] ?: mutableMapOf())[destination] ?: default

    override fun evaluate(origin: HasZoneId, destination: HasZoneId): Distance = get(origin, destination)
}

class MockDistanceTest : SynthesisTest() {
    @Test
    fun setAndGet() {
        val l1 = fakeLocation()
        val l2 = fakeLocation()
        val l3 = fakeLocation()

        assertNotEquals(l1, l2)
        assertNotEquals(l1, l3)
        assertNotEquals(l2, l3)

        val distances = AsymmetricMockDistance()
        assertEquals(distances[l1, l2], 0.kilometers)
        assertEquals(distances[l1, l3], 0.kilometers)
        assertEquals(distances[l2, l1], 0.kilometers)
        assertEquals(distances[l2, l3], 0.kilometers)
        assertEquals(distances[l3, l1], 0.kilometers)
        assertEquals(distances[l3, l2], 0.kilometers)

        distances[l1, l2] = 1
        assertEquals(distances[l1, l2], 1.kilometers)
        assertEquals(distances[l2, l1], 0.kilometers)

        distances[l1, l2] = 2
        assertEquals(distances[l1, l2], 2.kilometers)
        assertEquals(distances[l2, l1], 0.kilometers)
    }

    @Test
    fun setAndGetSymmetric() {
        val l1 = fakeLocation()
        val l2 = fakeLocation()
        val l3 = fakeLocation()

        assertNotEquals(l1, l2)
        assertNotEquals(l1, l3)
        assertNotEquals(l2, l3)

        val distances = SymmetricMockDistance()
        assertEquals(distances[l1, l2], 0.kilometers)
        assertEquals(distances[l1, l3], 0.kilometers)
        assertEquals(distances[l2, l1], 0.kilometers)
        assertEquals(distances[l2, l3], 0.kilometers)
        assertEquals(distances[l3, l1], 0.kilometers)
        assertEquals(distances[l3, l2], 0.kilometers)

        distances[l1, l2] = 1
        assertEquals(distances[l1, l2], 1.kilometers)
        assertEquals(distances[l2, l1], 1.kilometers)

        distances[l1, l2] = 2
        assertEquals(distances[l1, l2], 2.kilometers)
        assertEquals(distances[l2, l1], 2.kilometers)
    }
}
