package choicemodels

import domain.enums.LegacyActivityType
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import syntheticsim.OneHouseholdTwoPersons
import syntheticsim.loadActivityPlan
import usecases.choicemodels.LegacyDestinationChoice
import utils.units.sinceStart
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours

class DestinationChoiceTest {

    private lateinit var scenario: OneHouseholdTwoPersons
    private lateinit var original: LegacyDestinationChoice

    @BeforeEach
    fun setup() {
        scenario = OneHouseholdTwoPersons()
        original = scenario.destinationChoice.original
    }

    /**
     * The scenario where a person has no activity plans, but still calls destination choice should not occur,
     * but it might be interesting to see what happens
     */
    @Test
    fun personWithoutAnyActivities() {
        val exception = assertThrows<NoSuchElementException> { original.choose(scenario.first, 0.hours.sinceStart) }
        assertContains(exception.message!!, scenario.first.id.toString())
    }

    @Test
    fun personWithNoFollowupActivity() {
        val exception = assertThrows<NoSuchElementException> {
            scenario.run {
                first.loadActivityPlan {
                    +Triple(LegacyActivityType.HOME, 0, 4)
                }
                val stepper = first.stepper()
                assertTrue(first.schedule.pastActivities().isEmpty())
                stepper.take(3)
                assertFalse(first.schedule.pastActivities().isEmpty())

                original.choose(first, 0.hours.sinceStart)
            }
        }
        assertContains(exception.message!!, scenario.first.id.toString())
    }
}
