package domain.shared.datastructure.schedule

import domain.shared.datastructure.schedule.replanning.HomeActivityEndTimeAnchorStrategy
import domain.shared.datastructure.schedule.replanning.ReplanningStrategy
import domain.shared.enums.legacyChoiceModelPurposes
import org.junit.jupiter.api.Assertions.assertTrue
import utils.units.AbsoluteTime
import utils.units.sinceStart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ReschedulingSkipTillHomeTest {

    private val replanningStrategy = HomeActivityEndTimeAnchorStrategy(legacyChoiceModelPurposes)

    /**
     * In this test the activity can be simply shifted to fulfill the new start time.
     */
    @Test
    fun aNormalShift() {
        val (schedule, actions) = scheduleStartingAtHome {
            activity {
                startTime = 10
                duration = 2
            }
            activity {
                startTime = 15
                duration = 1
            }
            home {
                startTime = 22
                duration = 10
                latestEndTime = 24 + 8
            }
            activity {
                startTime = 24 + 8 + 1.0 / 60
                duration = 20
            }
        }
        replanningStrategy.replan(schedule, 11.hours.sinceStart, 1)

        assertEquals(actions[1].startTime, 11.hours.sinceStart)
        assertEquals(actions[1].duration, 2.hours)

        assertEquals(actions[2].startTime, 15.hours.sinceStart)
        assertEquals(actions[2].duration, 1.hours)

        assertEquals(actions[3].startTime, 22.hours.sinceStart)
        assertEquals(actions[3].duration, 10.hours)

        assertEquals(actions[4].startTime, 24.hours.sinceStart + 8.hours + 1.minutes)
        assertTrue(schedule.isConsistent())
    }

    /**
     * Here the actions are packed as close as possible so that no removal would be triggered, but all need to be
     * shifted as hard as possible.
     */
    @Test
    fun hardestShift() {
        val (schedule, actions) = scheduleStartingAtHome {
            activity {
                startTime = 12
                duration = 2
            }
            activity {
                startTime = 15
                duration = 1
            }

            activity {
                startTime = 18
                duration = 1
            }
            home {
                startTime = 22
                duration = 10
                latestEndTime = 24 + 8
            }
            activity {
                startTime = 24 + 8.02
                duration = 20
            }
        }
        val closestStart = 24 + 8 - 10 - 1 - 1 - 2 - 0.75
        replanningStrategy.replan(schedule, closestStart.hours.sinceStart, 1)
        assertTrue(schedule.isConsistent()) {
            "No consistent schedule"
        }

        assertEquals(actions[4].endTime, (24 + 8).hours.sinceStart)
        assertEquals(actions[1].startTime, closestStart.hours.sinceStart)
        assertEquals(actions[1].duration, 2.hours)

        assertEquals(actions[2].startTime, (closestStart + 2 + 1.0 / 4).hours.sinceStart)
        assertEquals(actions[2].duration, 1.hours)

        assertEquals(actions[3].startTime, (closestStart + 2 + 1 + 2.0 / 4).hours.sinceStart)
        assertEquals(actions[3].duration, 1.hours)

        assertEquals(actions[4].startTime, 22.hours.sinceStart)
        assertEquals(actions[4].duration, 10.hours)

        assertEquals(actions[5].startTime, 32.02.hours.sinceStart)
        assertEquals(actions[5].duration, 20.hours)
    }

    @Test
    fun overshotBy15Minutes() {
        val (schedule, actions) = scheduleStartingAtHome {
            activity {
                startTime = 12
                duration = 2
            }
            activity {
                startTime = 15
                duration = 1
            }

            activity {
                startTime = 18
                duration = 1
            }
            home {
                startTime = 22
                duration = 10
                latestEndTime = 24 + 8
            }
            activity {
                startTime = 24 + 8.02
                duration = 20
            }
        }
        val closestStart = 24 + 8 - 10 - 1 - 1 - 2 - 0.5
        replanningStrategy.replan(schedule, closestStart.hours.sinceStart, 1)
        assertTrue(schedule.isConsistent()) {
            "No consistent schedule"
        }
    }

    @Test
    fun theLastHomeActivityIsConflictuary() {
        val (schedule, actions) = scheduleStartingAtHome {
            activity {
                startTime = 12
                duration = 1
            }
            home {
                startTime = 15
                duration = 1
            }
        }

        replanningStrategy.replan(schedule, 16.hours.sinceStart, 1)
        assertTrue(schedule.isConsistent()) {
            "No consistent schedule"
        }
        assertEquals(actions[2].startTime, (16.hours + 15.minutes + 1.seconds).sinceStart)
    }

    @Test
    fun noHomeButStillaShift() {
        val (schedule, actions) = scheduleStartingAtHome {
            activity {
                startTime = 12
                duration = 2
            }
        }

        replanningStrategy.replan(schedule, 13.hours.sinceStart, 1)
        assertEquals(actions[1].startTime, 13.hours.sinceStart)
    }

    private fun Schedule.isConsistent() = actions().isConsistent()
    private fun ReplanningStrategy.replan(schedule: Schedule, newStartTime: AbsoluteTime, activityIndex: Int) =
        replan(schedule, newStartTime, schedule.activities().drop(activityIndex).first())

    private fun scheduleStartingAtHome(
        duration: Number = 8,
        lambda: ScheduleBuilderForTests.() -> Unit
    ): Pair<Schedule, List<StationaryAction>> {
        val builder = ScheduleBuilderForTests(duration)
        lambda(builder)
        return builder.build() to builder.actions()
    }
}
