package synthesis.householdgeneration

import domain.data.Employment
import domain.data.Sex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import synthesis.DefaultSurveyPerson
import synthesis.SurveyHousehold
import synthesis.SurveyInfo
import units.Currency
import units.euros
import kotlin.test.assertContentEquals

class RuleTest : SynthesisTest() {

    @Test
    fun sizeAsRule() {
        val household1 = createHousehold {
            person(10, Sex.MALE) {

            }
            person(10, Sex.MALE) {

            }

            person(10, Sex.MALE) {
                Employment.NONE
            }
        }
        val rule = ZoneCheckRule<Any>("Fake description", 10) { it.size == 3 }
        assertTrue(rule.appliesTo(household1))
        assertEquals(rule.check(household1), 1)

        val complexHousehold = SurveyHousehold<SurveyInfo>(
            1,
            1.euros,
            members = listOf(
                DefaultSurveyPerson(1, DefaultSurveyInfo.create())
            )
        )
        assertFalse(rule.appliesTo(complexHousehold))
        assertEquals(rule.check(complexHousehold), 0)


        rule.check(complexHousehold)

        assertContentEquals(rule.filter(listOf(household1, complexHousehold)), listOf(household1))

    }

    @Test
    fun countRule() {
        val household = createHousehold {
            person(10, Sex.MALE) {

            }
            person(9, Sex.FEMALE) {

            }
            person(25, Sex.MALE) {

            }
        }
        val rule = ZoneRule<Any>(description = "Test description", 42) {
            it.members.count { it.age in 5..10 }
        }
        assertEquals(rule.check(household), 2)
    }

    @Test
    fun advancedRules() {
        val household1 = createHousehold {

            person(10, Sex.MALE) {
                DifferentExampleInstantiation(
                    Employment.RETIRED
                )

            }

            person(10, Sex.MALE) {
                ExampleInstantiation(
                    Employment.RETIRED
                )

            }


            person(10, Sex.MALE) {
                DifferentExampleInstantiation(
                    Employment.NONE
                )

            }
        }

        val household2 = SurveyHousehold<ExampleInstantiation>(1, 1.euros, emptyList())
        val household3 = createHousehold {
            person(10, Sex.MALE) {
                ExampleInstantiation(Employment.NONE)
            }
        }
        val rule = ZoneCheckRule<ExampleInterface>("Attempt", 10) { household ->
            household.members.any { it.information.employment == Employment.NONE }
        }

        assertEquals(rule.check(household1), 1)
        assertEquals(rule.check(household2), 0)
        assertEquals(rule.check(household3), 1)

    }
}

private interface ExampleInterface {
    val employment: Employment
}

private class ExampleInstantiation(
    override val employment: Employment
) : ExampleInterface

private class DifferentExampleInstantiation(override val employment: Employment, val randomNumber: Int = 42) :
    ExampleInterface

private class DefaultSurveyInfo(
    override val householdId: Int,
    override val sex: Sex,
    override val age: Int,
    override val householdIncome: Currency,
    override val hasLicence: Boolean,
    override val employment: Employment
) : SurveyInfo {
    companion object {
        fun create(): DefaultSurveyInfo {
            return DefaultSurveyInfo(
                1,
                Sex.MALE,
                10,
                1.euros,
                false,
                Employment.RETIRED
            )
        }
    }
}