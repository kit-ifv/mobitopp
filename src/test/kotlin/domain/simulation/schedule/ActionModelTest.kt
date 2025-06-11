package domain.simulation.schedule

import START
import domain.simulation.schedule.plans.ActionModel
import domain.simulation.schedule.plans.PlanModel
import domain.simulation.schedule.plans.shift
import domain.simulation.schedule.plans.squeeze
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.units.AbsoluteTime
import utils.units.sinceStart
import kotlin.test.BeforeTest
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * The [ActionModel] provides a basic implementation of a [PlanModel] using a sorted set. Tests can use this model as
 * a baseline i.e. tests should usually work with this reasonably simple model.
 */
class ActionModelTest : PlanModelTest() {

    override lateinit var model: PlanModel
    private fun activity(
        start: Int,
        duration: Int,
        earliestStartTime: AbsoluteTime = (-Duration.INFINITE).sinceStart,
        latestEndTime: AbsoluteTime = Duration.INFINITE.sinceStart,
    ): Activity {
        return activity(start.toDouble(), duration.toDouble(), earliestStartTime, latestEndTime)
    }

    private fun activity(
        start: Double,
        duration: Double,
        earliestStartTime: AbsoluteTime = AbsoluteTime.MINUS_INFINITY,
        latestEndTime: AbsoluteTime = AbsoluteTime.INFINITY,
    ): Activity {
        return Activity.Companion.fromDuration(
            START,
            AbsoluteTime.START + start.toDuration(DurationUnit.HOURS),
            duration.toDuration(DurationUnit.HOURS)
        ).apply {
            this.earliestStartTime = earliestStartTime
            this.latestEndTime = latestEndTime
        }
    }

    @BeforeTest
    fun setup() {
        model = ActionModel()
    }

    @Test
    fun squeeze() {
        model.clear()
        model.add(activity(0, 1))
        model.add(activity(2, 1))
        model.add(activity(4, 1))

        model.squeeze(activity(0, 3))
        model.add(activity(0, 3))
        assertContentEquals(
            model.actions(),
            setOf<Action>(
                activity(0, 3),
                activity(3, 1),
                activity(4, 1),
                activity(5, 1)
            )
        )
    }

    @Test
    fun squeezeOverlap() {
        model.clear()
        model.add(activity(0, 1))
        model.add(activity(2, 1))
        model.add(activity(4, 1))
        model.add(activity(6, 1))
        model.add(activity(8, 1))
        model.add(activity(10, 1))

        model.squeeze(activity(1.5, 4.0))
        model.add(activity(1.5, 4.0))
        assertContentEquals(
            model.actions(),
            setOf<Action>(
                activity(0, 1),
                activity(1.5, 4.0),
                activity(5.5, 1.0),
                activity(6.5, 1.0),
                activity(7.5, 1.0),
                activity(8.5, 1.0),
                activity(10, 1)
            )
        )
    }

    @Test
    fun unforcedSqueezeWithBlockage() {
        model.clear()
        model.add(activity(0, 1))
        model.add(activity(2, 1))
        // This action does not support the squeeze, as the actions before it stack up too much time difference
        model.add(activity(4, 1, earliestStartTime = 4.hours.sinceStart, latestEndTime = 7.hours.sinceStart))
        model.add(activity(6, 1))
        val target = assertThrows<IllegalStateException> { model.squeeze(activity(1.5, 4.0)) }
        assertContains(target.message!!, "endTime=${5.hours}")
        assertContains(target.message!!, "necessaryShift=2h 30m")
        assertContains(target.message!!, "latestEndTime=${7.hours}")
        assertContentEquals(
            model.actions(),
            setOf<Action>(
                activity(0, 1),
                activity(2, 1),
                activity(4, 1, earliestStartTime = 4.hours.sinceStart, latestEndTime = 7.hours.sinceStart),
                activity(6, 1)
            )
        )
    }

    @Test
    fun forcesSqueezeWithBlockage() {
        model.clear()
        model.add(activity(0, 1))
        model.add(activity(2, 1))
        model.add(activity(4, 1, earliestStartTime = 4.hours.sinceStart, latestEndTime = 7.hours.sinceStart))
        model.add(activity(6, 1))
        model.add(activity(8, 1))
        model.add(activity(10, 1))

        model.squeeze(activity(1.5, 4.0), force = true)
        model.add(activity(1.5, 4.0))
        assertContentEquals(
            model.actions(),
            setOf<Action>(
                activity(0, 1),
                activity(1.5, 4.0),
                activity(5.5, 1.0),
                activity(6.5, 1.0, earliestStartTime = 4.hours.sinceStart, latestEndTime = 7.hours.sinceStart),
                activity(7.5, 1.0),
                activity(8.5, 1.0),
                activity(10, 1)
            )
        )
    }

    @Test
    fun shiftWithForce() {
        model.add(activity(0, 1))
        model.add(activity(2, 1))
        model.add(activity(4, 1, earliestStartTime = 4.hours.sinceStart, latestEndTime = 7.hours.sinceStart))
        model.add(activity(6, 1))
        model.add(activity(8, 1))
        model.add(activity(10, 1))
        model.shift(4.5.hours.sinceStart, 2.hours, force = true)

        assertContentEquals(
            model.actions(),
            setOf<Action>(
                activity(0, 1),
                activity(2, 1),
                activity(6, 1, earliestStartTime = 4.hours.sinceStart, latestEndTime = 7.hours.sinceStart),
                activity(8, 1),
                activity(10, 1),
                activity(12, 1)
            )
        )
    }
}
