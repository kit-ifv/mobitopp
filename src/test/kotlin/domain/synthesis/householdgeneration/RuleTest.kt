package domain.synthesis.householdgeneration

import domain.synthesis.behavior.DefaultSurveyPerson
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.RawSurveyInfo
import domain.synthesis.behavior.SurveyHousehold
import domain.synthesis.behavior.SurveyInfo
import domain.synthesis.behavior.householdgeneration.CountRule
import domain.synthesis.behavior.householdgeneration.ZoneCheckRule
import domain.synthesis.behavior.householdgeneration.ZoneRule
import domain.synthesis.data.Employment
import domain.synthesis.data.Sex
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.euros
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class RuleTest : SynthesisTest() {

    @Test
    fun sizeAsRule() {
        val household1 = createHousehold {
            person(10, Sex.MALE) {}
            person(10, Sex.MALE) {}

            person(10, Sex.MALE) {
                Employment.NONE
            }
        }
        val rule = ZoneCheckRule< ISurveyHousehold<out Any>>("Fake description", 10) { it.size == 3 }
        assertTrue(rule.appliesTo(household1))
        assertEquals(rule.evaluate(household1), 1)

        val complexHousehold = SurveyHousehold<SurveyInfo>(
            1,
            1.euros,
            members = listOf(
                DefaultSurveyPerson(1, DefaultSurveyInfo.create())
            )
        )
        assertFalse(rule.appliesTo(complexHousehold))
        assertEquals(rule.evaluate(complexHousehold), 0)

        rule.evaluate(complexHousehold)

        assertContentEquals(rule.filter(listOf(household1, complexHousehold)), listOf(household1))
    }

    @Test
    fun countRule() {
        val household = createHousehold {
            person(10, Sex.MALE) {}
            person(9, Sex.FEMALE) {}
            person(25, Sex.MALE) {}
        }
        val rule = ZoneRule< ISurveyHousehold<out Any>>(description = "Test description", 42) {
            it.members.count { it.age in 5..10 }
        }
        assertEquals(rule.evaluate(household), 2)
    }

    @Test
    fun advancedRules() {
        val (household1, household2, household3) = generateHouseholds()
        val rule = ZoneCheckRule< ISurveyHousehold<out ExampleInterface>>("Attempt", 10) { household ->
            household.members.any { it.information.employment == Employment.NONE }
        }

        assertEquals(rule.evaluate(household1), 1)
        assertEquals(rule.evaluate(household2), 0)
        assertEquals(rule.evaluate(household3), 1)
    }

    private fun generateHouseholds(): Triple<
        SurveyHousehold<ExampleInterface>,
        SurveyHousehold<ExampleInstantiation>,
        SurveyHousehold<ExampleInstantiation>
        > {
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
        return Triple(household1, household2, household3)
    }

    @Test
    fun checkCountRule() {
        val household = createHousehold {
            person(10, Sex.MALE) {
            }

            person(12, Sex.FEMALE) {
            }
        }

        val rule1 = CountRule< ISurveyHousehold<out Any>> { h -> h.count { it.age in 9..12 } }
        assertEquals(rule1.matches(household), 2)

        val rule2 = CountRule< ISurveyHousehold<out Any>> { h -> h.count { it.age in 11..12 } }
        assertEquals(rule2.matches(household), 1)

        val rule3 = CountRule< ISurveyHousehold<out Any>> { h -> h.count { it.age in 9..12 && it.sex == Sex.FEMALE } }
        assertEquals(rule3.matches(household), 1)
    }

    @Test
    fun testFilter() {
        val (household1, household2, household3) = generateHouseholds()
        val households = listOf(household1, household2, household3)
        val rule = ZoneCheckRule< ISurveyHousehold<out ExampleInterface>>("Attempt", 10) { household ->
            household.members.any { it.information.employment == Employment.NONE }
        }
        assertContentEquals(rule.filter(households), listOf(household1, household3))
    }

    @Test
    fun testVerification() {
        val household = createHousehold {
            person(10, Sex.MALE) {}
            person(10, Sex.MALE) {}
            person(10, Sex.MALE) {}
            person(10, Sex.MALE) {}
            person(10, Sex.MALE) {}
        }
        val household2 = createHousehold {
            person(10, Sex.MALE) {}
            person(10, Sex.MALE) {}
        }
        val rule = ZoneRule< ISurveyHousehold<out Any>>("Test Rule", 10) { it.size }
        assertEquals(rule.verify(listOf(household)), 5.0)
        assertEquals(rule.verify(listOf(household, household2)), 3.0)
        assertEquals(rule.verify(listOf(household, household, household2)), -2.0)
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
