package domain.shared.datastructure.schedule

import FOURTH
import OTHER
import START
import org.junit.jupiter.api.assertThrows
import utils.units.sinceStart
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.hours

class LinkedTripTest {
    private val leg1: Leg = Leg.Companion.fromDuration(1.hours.sinceStart, 1.hours, START, FOURTH)
    private val leg2: Leg = Leg.Companion.fromDuration(2.hours.sinceStart, 1.hours, FOURTH, OTHER)
    private val leg3: Leg = Leg.Companion.fromDuration(3.hours.sinceStart, 1.hours, FOURTH, OTHER)
    private val activityBeforeLegs: Activity = Activity.Companion.fromDuration(START, 0.hours.sinceStart, 1.hours)
    private val activityOverlapsLeg1: Activity = Activity.Companion.fromDuration(START, 0.hours.sinceStart, 1.1.hours)
    private val activityOverlapsLeg3: Activity = Activity.Companion.fromDuration(START, 3.9.hours.sinceStart, 1.hours)
    private val activityAfterLegs: Activity = Activity.Companion.fromDuration(START, 4.1.hours.sinceStart, 1.hours)

    private val allMissingActivities =
        listOf(activityAfterLegs, activityBeforeLegs, activityOverlapsLeg1, activityOverlapsLeg3)

    private lateinit var legBlock: LinkedTrip
    private lateinit var prevActivityBlock: ActivityBlock
    private lateinit var succActivityBlock: ActivityBlock

    @BeforeTest
    fun setup() {
        prevActivityBlock = ActivityBlock(sortedSetOf())
        succActivityBlock = ActivityBlock(sortedSetOf())
        legBlock = LinkedTrip(sortedSetOf(), prevActivityBlock, succActivityBlock)
        legBlock.previous = prevActivityBlock
        legBlock.next = succActivityBlock

        legBlock.insert(leg1)
        legBlock.insert(leg2)
        legBlock.insert(leg3)
    }

    @Test
    fun accepts() {
        allMissingActivities.forEach { assertFalse(legBlock.accepts(it)) }
        assertFalse(legBlock.accepts(leg1))
        assertFalse(legBlock.accepts(leg2))
        assertFalse(legBlock.accepts(leg3))
    }

    @Test
    fun insert() {
        assertThrows<IllegalArgumentException> { legBlock.insert(activityOverlapsLeg3) }
    }
}
