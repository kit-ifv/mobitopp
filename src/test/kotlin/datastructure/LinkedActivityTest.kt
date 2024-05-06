package datastructure

import FOURTH
import OTHER
import START
import THIRD
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours

class LinkedActivityTest {

    private lateinit var firstLeg: LinkedLeg
    private lateinit var secondLeg: LinkedLeg
    private lateinit var thirdActivity: LinkedActivity
    private lateinit var fourthActivity: LinkedActivity
    private lateinit var fifthLeg: LinkedLeg

    @BeforeTest
    fun setup() {
        firstLeg = LinkedLeg(Leg.fromDuration(0.hours, 1.hours, START, THIRD), { null }, { secondLeg })
        secondLeg = LinkedLeg(Leg.fromDuration(1.hours, 1.hours, THIRD, OTHER), { firstLeg }, { thirdActivity })
        thirdActivity =
            LinkedActivity(Activity.fromDuration(OTHER, 3.hours, 1.hours), { secondLeg }, { fourthActivity })
        fourthActivity = LinkedActivity(Activity.fromDuration(OTHER, 5.hours, 1.hours), { thirdActivity }, { fifthLeg })
        fifthLeg = LinkedLeg(Leg.fromDuration(7.hours, 1.hours, OTHER, START), { fourthActivity }, { null })
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

    @Test
    fun badChanges() {
        assertThrows<IllegalStateException> { secondLeg.startTime = 0.5.hours }
        assertEquals(firstLeg.endTime, 1.hours)
        assertEquals(secondLeg.startTime, 1.hours)
    }

    @Test
    fun badChanges2() {
        assertThrows<IllegalStateException> { secondLeg.endTime = 3.5.hours }
        assertEquals(thirdActivity.startTime, 3.hours)
        assertEquals(secondLeg.endTime, 2.hours)
    }

    @Test
    fun badChanges3() {
        assertThrows<IllegalStateException> { thirdActivity.startTime = 1.5.hours }
        assertEquals(thirdActivity.startTime, 3.hours)
        assertEquals(secondLeg.endTime, 2.hours)
    }

    @Test
    fun badChanges4() {
        assertThrows<IllegalStateException> { thirdActivity.endTime = 5.5.hours }
        assertEquals(thirdActivity.endTime, 4.hours)
        assertEquals(fourthActivity.startTime, 5.hours)
    }

    @Test
    fun validChange() {
        secondLeg.endTime = 2.5.hours
        assertEquals(secondLeg.endTime, 2.5.hours)
        assertEquals(thirdActivity.startTime, 3.hours)
    }

    @Test
    fun badChanges5() {
        assertThrows<IllegalStateException> { secondLeg.endTime = 0.9.hours }
    }
}
