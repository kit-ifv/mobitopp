package domain.shared.datastructure.schedule

import FOURTH
import OTHER
import START
import THIRD
import domain.shared.datastructure.schedule.action.Activity
import domain.shared.datastructure.schedule.action.Leg
import domain.shared.datastructure.schedule.action.LinkedActivity
import domain.shared.datastructure.schedule.action.LinkedLeg
import domain.shared.datastructure.schedule.action.isWeaklyConsistent
import domain.shared.location.StandardLocation
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import utils.collections.cartesianProduct
import utils.units.AbsoluteTime
import utils.units.sinceStart
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
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
        firstLeg = LinkedLeg(Leg.fromDuration(0.hours.sinceStart, 1.hours, START, THIRD))
        secondLeg = LinkedLeg(Leg.fromDuration(1.hours.sinceStart, 1.hours, THIRD, OTHER))
        thirdActivity =
            LinkedActivity(Activity.fromDuration(OTHER, 3.hours.sinceStart, 1.hours))
        fourthActivity = LinkedActivity(Activity.fromDuration(OTHER, 5.hours.sinceStart, 1.hours))
        fifthLeg = LinkedLeg(Leg.fromDuration(7.hours.sinceStart, 1.hours, OTHER, START))

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
        val actions: List<(StandardLocation) -> Unit> = listOf(
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
        val actions: List<(AbsoluteTime) -> Unit> = listOf(
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
                it.second(it.first.sinceStart)
                assertTrue(listOf(firstLeg, secondLeg, thirdActivity, fourthActivity, fifthLeg).isWeaklyConsistent())
            }
        }
    }

    @Test
    fun badChanges() {
        secondLeg.startTime = 0.5.hours.sinceStart
        assertEquals(firstLeg.startTime, (-0.5).hours.sinceStart)
        assertEquals(firstLeg.endTime, 0.5.hours.sinceStart)
        assertEquals(secondLeg.startTime, 0.5.hours.sinceStart)
    }

    @Test
    fun validChange() {
        secondLeg.endTime = 2.5.hours.sinceStart
        assertEquals(secondLeg.endTime, 2.5.hours.sinceStart)
        assertEquals(thirdActivity.startTime, 3.hours.sinceStart)
    }
}
