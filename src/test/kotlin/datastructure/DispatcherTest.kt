package datastructure

import FOURTH
import OTHER
import START
import THIRD
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class DispatcherTest {

    private val activityGenerator: Sequence<Activity> = sequence {
        var timer = Duration.ZERO
        while (true) {
            yield(Activity.fromDuration(START, timer, 1.hours))
            timer += 2.hours

            yield(Activity.fromDuration(OTHER, timer, 1.hours))
            timer += 2.hours
        }
    }
    private val legGenerator: Sequence<Leg> = sequence {
        var timer = 1.hours
        while (true) {
            yield(Leg.fromDuration(timer, 30.minutes, START, THIRD))
            timer += 30.minutes
            yield(Leg.fromDuration(timer, 30.minutes, THIRD, OTHER))
            timer += 30.minutes
            timer += 1.hours

            yield(Leg.fromDuration(timer, 30.minutes, OTHER, THIRD))
            timer += 30.minutes
            yield(Leg.fromDuration(timer, 30.minutes, THIRD, START))
            timer += 30.minutes
            timer += 1.hours
        }
    }

    @Test
    fun properTripView() {
        val dispatcher = Dispatcher()
        val actions = ActionModel(dispatcher)
        val actionView = actions.view()
        val blocks = BlockModel(dispatcher)
        val blockView = blocks.view()
        val legs = legGenerator.take(6).toList()
        legs.forEach { actionView.add(it) }
        val trip = blockView.first()
        assertEquals(trip.size, 6)
        actionView.remove(legs.first())
        assertEquals(trip.size, 5)
    }

    /**
     * A trip holds a reference to a leg block in the block-model. If the block is unlinked by external views via
     * removal of all legs within the leg block the trip should "float" and changes should no longer influence separate
     * views.
     */
    @Test
    fun floatingTrip() {
        val dispatcher = Dispatcher()
        val actions = ActionModel(dispatcher)
        val actionView = actions.view()
        val blocks = BlockModel(dispatcher)
        val blockView = blocks.view()
        val legs = legGenerator.take(6).toList()

        legs.forEach { actionView.add(it) }
        val take = activityGenerator.drop(3).take(1)
        take.forEach { actionView.add(it) }
        val trip = blockView.first()

        legs.forEach { actionView.remove(it) }
        take.forEach { actionView.remove(it) }
        assertTrue(blockView.isEmpty())

        trip.overwrite {
            +legs.subList(2, 6)
        }

        assertTrue(actionView.isEmpty())
        assertTrue(blockView.isEmpty())
    }

    /**
     * A proper trip should reflect changes in other sets that view the same action pool.
     */
    @Test
    fun linkedTrip() {
        val dispatcher = Dispatcher()
        val actions = ActionModel(dispatcher)
        val actionView = actions.view()
        val blocks = BlockModel(dispatcher)
        val blockView = blocks.view()
        val legs = legGenerator.take(6).toList()
        legs.forEach { actionView.add(it) }
        val take = activityGenerator.drop(3).take(1)
        take.forEach { actionView.add(it) }
        val trip = blockView.first()
        trip.overwrite {
            val first = originals.first()
            val last = originals.last()
            +Leg.fromDuration(first.startTime, first.duration, first.startLocation, FOURTH)
            +Leg.fromDuration(last.startTime, last.duration, FOURTH, last.endLocation)
        }

        assertContentEquals(blocks.actions(), actions.actions())
    }
}
