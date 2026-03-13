package domain.shared.datastructure.schedule.replanning

import domain.shared.datastructure.schedule.ScheduleBuilderForTests
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.legacyChoiceModelPurposes
import domain.shared.location.StandardLocation
import org.junit.jupiter.api.Assertions.assertTrue
import utils.units.AbsoluteTime
import utils.units.sinceStart
import kotlin.test.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class HolyHomeActivityResolverTest {

    /**
     * Taken from a real mobiTopp run where the rescheduler failed spectacularly
     *
     */
    @Test
    fun conflictResolutionStrategy() {
        val conflict = createConflict {
            startTime = AbsoluteTime(51.minutes + 46.seconds)
            endTime = AbsoluteTime(1.days + 6.hours + 39.minutes + 44.seconds)
            activities {
                activity {
                    setStart("35m")
                    setEnd("59m 27s")
                    activityType = LegacyActivityType.WORK
                }
                activity {
                    setStart("1h 6m")
                    setEnd("14h 13m 21s")
                    activityType = LegacyActivityType.WORK
                }
                activity {
                    setStart("14h 13m 22s")
                    setEnd("23h 38m 5s")
                    activityType = LegacyActivityType.WORK
                }
                home {
                    setStart("1d 0h 3m 23s")
                    setEnd("1d 6h 39m 44s")
                }
            }
        }
        val resolver = PriorityBasedConflictResolver(legacyChoiceModelPurposes)
        val resolution = resolver.resolveConflict(conflict)
        assertTrue(resolution.first() != null)
    }

    @Test
    fun anotherSimulationConflict() {
        val conflict = createConflict {
            startTime = AbsoluteTime(23.minutes + 3.seconds)
            endTime = AbsoluteTime(53.minutes)
            activities {
                activity {
                    setStart("10m")
                    setEnd("27m 41s")
                    activityType = LegacyActivityType.SERVICE
                }
                home {
                    setStart("52m")
                    setEnd("53m")
                }
            }
        }
        val resolver = PriorityBasedConflictResolver(legacyChoiceModelPurposes)
        val resolution = resolver.resolveConflict(conflict)
        assertTrue(resolution.first() != null)
        assertTrue(resolution[1] == null)
    }

    @Test
    fun aThirdSimConflict() {
        val conflict = createConflict {
            startTime = AbsoluteTime(46.minutes + 57.seconds)
            endTime = AbsoluteTime(54.minutes + 13.seconds)
            activities {
                home {
                    setStart("43m")
                    setEnd("54m 13s")
                }
            }
        }
        val resolver = PriorityBasedConflictResolver(legacyChoiceModelPurposes)
        val resolution = resolver.resolveConflict(conflict)
        assertTrue { resolution.first() != null }
    }

    private fun createConflict(lambda: ConflictBuilder.() -> Unit): Conflict {
        val builder = ConflictBuilder()
        builder.lambda()
        return builder.build()
    }
}

private class ConflictBuilder {
    var startTime: AbsoluteTime = 0.hours.sinceStart
    var endTime: AbsoluteTime = 999.hours.sinceStart
    var startLocation: StandardLocation = StandardLocation.fromID(1)
    var endLocation: StandardLocation = StandardLocation.fromID(1)

    private val scheduleBuilder = ScheduleBuilderForTests(0.0001)

    fun activities(lambda: ScheduleBuilderForTests.() -> Unit) {
        scheduleBuilder.lambda()
    }

    fun build(): Conflict {
        return Conflict(
            startTime,
            endTime,
            startLocation,
            endLocation,
            scheduleBuilder.actions().drop(1) // Remove the home activity that the usual builder chucks in
        )
    }
}
