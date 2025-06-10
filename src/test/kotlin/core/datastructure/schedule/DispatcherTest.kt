package core.datastructure.schedule

import FOURTH
import OTHER
import START
import THIRD
import core.datastructure.schedule.plans.ActionModel
import core.datastructure.schedule.plans.BlockModel
import core.datastructure.schedule.plans.Dispatcher
import core.datastructure.schedule.plans.shift
import utils.units.sinceStart
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
            yield(Activity.Companion.fromDuration(START, timer.sinceStart, 1.hours))
            timer += 2.hours

            yield(Activity.Companion.fromDuration(OTHER, timer.sinceStart, 1.hours))
            timer += 2.hours
        }
    }
    private val legGenerator: Sequence<Leg> = sequence {
        var timer = 1.hours
        while (true) {
            yield(Leg.Companion.fromDuration(timer.sinceStart, 30.minutes, START, THIRD))
            timer += 30.minutes
            yield(Leg.Companion.fromDuration(timer.sinceStart, 30.minutes, THIRD, OTHER))
            timer += 30.minutes
            timer += 1.hours

            yield(Leg.Companion.fromDuration(timer.sinceStart, 30.minutes, OTHER, THIRD))
            timer += 30.minutes
            yield(Leg.Companion.fromDuration(timer.sinceStart, 30.minutes, THIRD, START))
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

        trip.alternate {
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
        val legList = legGenerator.take(6).toList()
        legList.forEach { actionView.add(it) }
        val take = activityGenerator.drop(3).take(1)
        take.forEach { actionView.add(it) }
        val trip = blockView.first()
        trip.alternate {
            val set = sortedSetOf<Leg>()
            val first = originals.first()
            val last = originals.last()
            set.add(Leg.Companion.fromDuration(first.startTime, first.duration, first.startLocation, FOURTH))
            set.add(Leg.Companion.fromDuration(last.startTime, last.duration, FOURTH, last.endLocation))
        }

        assertContentEquals(blocks.actions(), actions.actions())
    }

    /**
     * Since shifting and squeezing operations modify the original element directly the views themselves should
     * automatically receive updates, even when shift/squeeze is called on a different model directly.
     */
    @Test
    fun squeezeAdaptsOtherView() {
        val dispatcher = Dispatcher()
        val actions = ActionModel(dispatcher)
        val blocks = BlockModel(dispatcher)
        val actionView = actions.view()
        val activities = activityGenerator.take(6).toList()
        activities.forEach { actionView.add(it) }

        assertContentEquals(actions.actions(), activities as Collection<Action>)
        assertContentEquals(blocks.actions(), activities as Collection<Action>)
        actions.shift(0.hours.sinceStart, 1.hours)
        assertContentEquals(blocks.actions(), actions.actions())
        assertContentEquals(blocks.actions(), actions.actions())
    }

    /**
     * The links of each of the models should generate the correct references to previous / next
     */
    @Test
    fun properLinking() {
        val dispatcher = Dispatcher()
        val actions = ActionModel(dispatcher)
        val blocks = BlockModel(dispatcher)
        val actionView = actions.view()
        val activities = activityGenerator.take(1)
        val legs = legGenerator.take(2)
        activities.forEach { actionView.add(it) }
        legs.forEach { actionView.add(it) }

        actions.actions().zip(blocks.actions()).forEach { (a, b) ->
            assertEquals(a, b)
            assertEquals(a.previous, b.previous)
            assertEquals(a.next, b.next)
        }
    }
}
