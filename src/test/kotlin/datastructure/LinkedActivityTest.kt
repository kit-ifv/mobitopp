package datastructure

import FOURTH
import OTHER
import START
import THIRD
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
        fifthLeg = LinkedLeg(Leg.fromDuration(7.hours, 1.hours, START, OTHER), { fourthActivity }, { null })
    }

    @Test
    fun changingValues() {
        firstLeg.endLocation = FOURTH
        assertEquals(firstLeg.endLocation, FOURTH)
        assertEquals(secondLeg.startLocation, FOURTH)
    }

    @Test
    fun changingActivityLocation() {
        thirdActivity.location = FOURTH
        assertEquals(thirdActivity.location, FOURTH)
        assertEquals(fourthActivity.location, FOURTH)
        assertEquals(fifthLeg.startLocation, FOURTH)
        assertEquals(secondLeg.endLocation, FOURTH)
    }

    @Test
    fun badChanges() {
        assertThrows<IllegalStateException> { secondLeg.startTime = 0.5.hours }
        assertEquals(firstLeg.endTime, 1.hours)
        assertEquals(secondLeg.startTime, 1.hours)
    }
}