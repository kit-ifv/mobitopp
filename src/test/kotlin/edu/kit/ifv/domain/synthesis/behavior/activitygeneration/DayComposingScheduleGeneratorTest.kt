package edu.kit.ifv.domain.synthesis.behavior.activitygeneration

import edu.kit.ifv.domain.shared.datastructure.schedule.action.RawActivity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.isConsistent
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.enums.person.Sex
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SmallestSurveyPerson
import edu.kit.ifv.utils.units.sinceStart
import kotlinx.datetime.DayOfWeek
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class DayComposingScheduleGeneratorTest {
    private class Attrs(override val age: Int, override val sex: Sex) : MinimumPersonAttributes

    private enum class DayActivities(override val code: Int) : ActivityType {
        MO(0),
        DI(1),
        MI(2),
        DO(3),
        FR(4),
        SA(5),
        SO(6),
        ;

        override val description: String
            get() = code.toString()
        companion object {
            fun decode(dayOfWeek: DayOfWeek): DayActivities = DayActivities.entries[dayOfWeek.ordinal]
        }
    }

    private fun testPerson() = SmallestSurveyPerson<MinimumPersonAttributes>(
        1,
        attributes = Attrs(10, Sex.MALE),
    )

    @Test
    fun composerSchedule() {
        val prefabs = DayOfWeek.entries.associateWith {
            listOf(validActivityPlanForDay(it, type = DayActivities.decode(it)))
        }

        val generator = DayComposingScheduleGenerator(
            Random(42),
            dailyScheduleCandidates = prefabs,
        )
        val person = SmallestSurveyPerson<MinimumPersonAttributes>(
            1,
            attributes = Attrs(10, Sex.MALE),
        )
        val output = generator.generate(person)
        assertTrue(output.isConsistent())
        assertEquals(DayActivities.MO, output[0].type)
        assertEquals(DayActivities.SO, output[6].type)
    }

    private fun validActivityPlanForDay(
        dayOfWeek: DayOfWeek,
        size: Int = 1,
        type: ActivityType,
    ): ReadonlyActivitySchedule = (0 until size).map {
        RawActivity(
            location = StandardLocation.LOCATIONUNKNOWN,
            startTime = (dayOfWeek.ordinal.days + it.hours).sinceStart,
            endTime = (dayOfWeek.ordinal.days + it.hours + 30.minutes).sinceStart,
            type = type,
        )
    }

    @Test
    fun `selects the only available schedule for a day`() {
        val mondaySchedule = validActivityPlanForDay(
            DayOfWeek.MONDAY,
            size = 3,
            type = DayActivities.MO,
        )

        val generator = DayComposingScheduleGenerator(
            Random(42),
            mapOf(DayOfWeek.MONDAY to listOf(mondaySchedule)),
        )

        val selected = generator.getRandomActivityPlanFor(DayOfWeek.MONDAY)

        assertSame(mondaySchedule, selected)
    }

    @Test
    fun `selected schedule always belongs to the candidate pool`() {
        val candidates = (1..5).map { size ->
            validActivityPlanForDay(
                DayOfWeek.MONDAY,
                size = size,
                type = DayActivities.MO,
            )
        }

        val generator = DayComposingScheduleGenerator(
            Random(42),
            mapOf(DayOfWeek.MONDAY to candidates),
        )

        repeat(100) {
            val selected = generator.getRandomActivityPlanFor(DayOfWeek.MONDAY)

            assertTrue(
                candidates.any { it === selected },
                "Selected schedule must be one of the registered candidates",
            )
        }
    }

    @Test
    fun `random selection is reproducible with the same seed`() {
        val candidates = (1..5).map { size ->
            validActivityPlanForDay(
                DayOfWeek.MONDAY,
                size = size,
                type = DayActivities.MO,
            )
        }

        fun generator() = DayComposingScheduleGenerator(
            Random(42),
            mapOf(DayOfWeek.MONDAY to candidates),
        )

        val first = generator()
        val second = generator()

        val firstSelections = List(100) {
            candidates.indexOfFirst {
                it === first.getRandomActivityPlanFor(DayOfWeek.MONDAY)
            }
        }

        val secondSelections = List(100) {
            candidates.indexOfFirst {
                it === second.getRandomActivityPlanFor(DayOfWeek.MONDAY)
            }
        }

        assertEquals(firstSelections, secondSelections)
        assertTrue(firstSelections.toSet().size > 1)
    }

    @Test
    fun `returns empty schedule when candidate list is empty`() {
        val generator = DayComposingScheduleGenerator(
            Random(42),
            mapOf(DayOfWeek.MONDAY to emptyList()),
        )

        val selected = generator.getRandomActivityPlanFor(DayOfWeek.MONDAY)

        assertTrue(selected.isEmpty())
    }

    @Test
    fun `throws when no candidates are registered for a day`() {
        val generator = DayComposingScheduleGenerator(
            Random(42),
            emptyMap(),
        )
        assertThrows<NoSuchElementException> {
            generator.getRandomActivityPlanFor(DayOfWeek.MONDAY)
        }
    }

    @Test
    fun `generation fails when a weekday is missing`() {
        val prefabs = DayOfWeek.entries
            .filterNot { it == DayOfWeek.WEDNESDAY }
            .associateWith { day ->
                listOf(validActivityPlanForDay(day, type = DayActivities.decode(day)))
            }

        val generator = DayComposingScheduleGenerator(Random(42), prefabs)

        assertThrows<NoSuchElementException> {
            generator.generate(testPerson())
        }
    }

    @Test
    fun `generates stay at home schedule when all candidate lists are empty`() {
        val prefabs = DayOfWeek.entries.associateWith {
            emptyList<ReadonlyActivitySchedule>()
        }

        val generator = DayComposingScheduleGenerator(Random(42), prefabs)

        val result = generator.generate(testPerson())

        assertEquals(PreliminaryActivitySchedule.STAY_AT_HOME, result)
    }

    @Test
    fun `empty candidate list for one day does not prevent other days from being generated`() {
        val prefabs = DayOfWeek.entries.associateWith { day ->
            if (day == DayOfWeek.WEDNESDAY) {
                emptyList()
            } else {
                listOf(validActivityPlanForDay(day, type = DayActivities.decode(day)))
            }
        }

        val generator = DayComposingScheduleGenerator(Random(42), prefabs)

        val result = generator.generate(testPerson())

        assertEquals(6, result.size)
        assertTrue(result.isConsistent())
        assertEquals(
            listOf(
                DayActivities.MO,
                DayActivities.DI,
                DayActivities.DO,
                DayActivities.FR,
                DayActivities.SA,
                DayActivities.SO,
            ),
            result.map { it.type },
        )
    }

    @Test
    fun `preserves all activities and their chronological order`() {
        val activitiesPerDay = 4

        val prefabs = DayOfWeek.entries.associateWith { day ->
            listOf(
                validActivityPlanForDay(
                    day,
                    size = activitiesPerDay,
                    type = DayActivities.decode(day),
                ),
            )
        }

        val generator = DayComposingScheduleGenerator(Random(42), prefabs)

        val result = generator.generate(testPerson())

        assertEquals(7 * activitiesPerDay, result.size)
        assertTrue(result.isConsistent())

        DayOfWeek.entries.forEach { day ->
            repeat(activitiesPerDay) { activityIndex ->
                val resultIndex = day.ordinal * activitiesPerDay + activityIndex
                val activity = result[resultIndex]

                assertEquals(DayActivities.decode(day), activity.type)
                assertEquals(
                    (day.ordinal.days + activityIndex.hours).sinceStart,
                    activity.startTime,
                )
                assertEquals(
                    (day.ordinal.days + activityIndex.hours + 30.minutes).sinceStart,
                    activity.endTime,
                )
            }
        }
    }

    @Test
    fun `generated activities preserve the attributes of their source activities`() {
        val prefabs = DayOfWeek.entries.associateWith { day ->
            listOf(
                validActivityPlanForDay(
                    day,
                    size = 2,
                    type = DayActivities.decode(day),
                ),
            )
        }

        val generator = DayComposingScheduleGenerator(Random(42), prefabs)
        val result = generator.generate(testPerson())

        val sourceActivities = DayOfWeek.entries.flatMap { day ->
            prefabs.getValue(day).single()
        }

        assertEquals(sourceActivities.size, result.size)

        sourceActivities.zip(result).forEach { (source, generated) ->
            assertEquals(source.type, generated.type)
            assertEquals(source.location, generated.location)
            assertEquals(source.startTime, generated.startTime)
            assertEquals(source.endTime, generated.endTime)
            assertEquals(source.earliestStartTime, generated.earliestStartTime)
            assertEquals(source.latestEndTime, generated.latestEndTime)
        }
    }

    @Test
    fun `generated activities are independent copies of the prefabs`() {
        val prefabs = DayOfWeek.entries.associateWith { day ->
            listOf(validActivityPlanForDay(day, type = DayActivities.decode(day)))
        }

        val generator = DayComposingScheduleGenerator(Random(42), prefabs)

        val first = generator.generate(testPerson())
        val second = generator.generate(testPerson())

        DayOfWeek.entries.forEachIndexed { index, day ->
            val source = prefabs.getValue(day).single().single()

            assertNotSame(source, first[index])
            assertNotSame(source, second[index])
            assertNotSame(first[index], second[index])
        }
    }
}
