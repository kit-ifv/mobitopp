package datastructure

import FOURTH
import OTHER
import START
import THIRD
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import utils.collections.cartesianProduct
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class LinkedActivityTest {

    private lateinit var firstLeg: LinkedLeg
    private lateinit var secondLeg: LinkedLeg
    private lateinit var thirdActivity: LinkedActivity
    private lateinit var fourthActivity: LinkedActivity
    private lateinit var fifthLeg: LinkedLeg

    @BeforeTest
    fun setup() {
        firstLeg = LinkedLeg(Leg.fromDuration(0.hours, 1.hours, START, THIRD))
        secondLeg = LinkedLeg(Leg.fromDuration(1.hours, 1.hours, THIRD, OTHER))
        thirdActivity =
            LinkedActivity(Activity.fromDuration(OTHER, 3.hours, 1.hours))
        fourthActivity = LinkedActivity(Activity.fromDuration(OTHER, 5.hours, 1.hours))
        fifthLeg = LinkedLeg(Leg.fromDuration(7.hours, 1.hours, OTHER, START))

        firstLeg.next = secondLeg
        secondLeg.previous = firstLeg
        secondLeg.next = thirdActivity
        thirdActivity.previous = secondLeg
        thirdActivity.next = fourthActivity
        fourthActivity.previous = thirdActivity
        fourthActivity.next = fifthLeg
        fifthLeg.previous = fourthActivity
    }

    @TestFactory
    fun changingValues(): List<DynamicTest> {
        val actions: List<(Location) -> Unit> = listOf(
            { secondLeg.endLocation = it },

            { thirdActivity.startLocation = it },
            { thirdActivity.location = it },
            { thirdActivity.endLocation = it },

            { fourthActivity.startLocation = it },
            { fourthActivity.location = it },
            { fourthActivity.endLocation = it },

            { fifthLeg.startLocation = it },
        )
        return actions.map {
            DynamicTest.dynamicTest(it.toString()) {
                // Weirdly, neither @BeforeEach nor @BeforeTest actually call setup() from the test factory.
                setup()
                assertEquals(thirdActivity.location, OTHER)
                assertEquals(fourthActivity.location, OTHER)
                assertEquals(fifthLeg.startLocation, OTHER)
                assertEquals(secondLeg.endLocation, OTHER)
                it(FOURTH)
                assertEquals(thirdActivity.location, FOURTH)
                assertEquals(fourthActivity.location, FOURTH)
                assertEquals(fifthLeg.startLocation, FOURTH)
                assertEquals(secondLeg.endLocation, FOURTH)
            }
        }
    }

    @TestFactory
    fun wildShuffle(): List<DynamicTest> {
        val newDurations = listOf((-10).days, 0.hours, 10.days)
        val actions: List<(Duration) -> Unit> = listOf(
            { firstLeg.startTime = it },
            { firstLeg.endTime = it },
            { secondLeg.startTime = it },
            { secondLeg.endTime = it },
            { thirdActivity.startTime = it },
            { thirdActivity.endTime = it },
            { fourthActivity.startTime = it },
            { fourthActivity.endTime = it },
            { fifthLeg.startTime = it },
            { fifthLeg.endTime = it },
        )
        return newDurations.cartesianProduct(actions).map {
            DynamicTest.dynamicTest(it.toString()) {
                setup()
                it.second(it.first)
                assertTrue(listOf(firstLeg, secondLeg, thirdActivity, fourthActivity, fifthLeg).isConsistent())
            }
        }
    }

    @Test
    fun badChanges() {
        secondLeg.startTime = 0.5.hours
        assertEquals(firstLeg.startTime, (-0.5).hours)
        assertEquals(firstLeg.endTime, 0.5.hours)
        assertEquals(secondLeg.startTime, 0.5.hours)
    }

    @Test
    fun validChange() {
        secondLeg.endTime = 2.5.hours
        assertEquals(secondLeg.endTime, 2.5.hours)
        assertEquals(thirdActivity.startTime, 3.hours)
    }
}
