package domain.synthesis.householdgeneration

import TEST_ZONE
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.behavior.householdgeneration.GenerateHouseholdsFromVector
import domain.synthesis.behavior.householdgeneration.IPU
import domain.synthesis.behavior.householdgeneration.ScalableVector
import domain.synthesis.behavior.householdgeneration.ZoneRule
import domain.synthesis.data.Employment
import domain.synthesis.data.Sex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IPUTest : SynthesisTest() {

    private val employmentRule =
        ZoneRule<Employment>(description = "None Employment", 42) { it.count { it.information == Employment.NONE } }
    private val ageRule = ZoneRule<Any>(description = "Age == 10", 42) { it.count { it.age == 10 } }
    private val genderRule = ZoneRule<Any>(description = "Males", 42) { it.count { it.sex == Sex.MALE } }

    private val employmentHousehold = createHousehold {
        person(10, Sex.FEMALE) {
            Employment.NONE
        }
        person(10, Sex.MALE) {
            Employment.NONE
        }
    }
    private val unitHousehold = createHousehold {
        person(10, Sex.MALE) {}
        person(10, Sex.MALE) {}
    }

    private val employmentIPU = IPU<Any, Employment>(
        GenerateHouseholdsFromVector.coerceMaintainingOrder()
    ) { vectors, _ ->
        vectors.forEach { it *= 2 }
    }

    private val unitIPU = IPU<Any, Any>(GenerateHouseholdsFromVector.coerceMaintainingOrder()) { vectors, _ ->
        vectors.forEach { it *= 3 }
    }
    private val testZone = TEST_ZONE

    @Test
    fun testConfiguration() {
        assertTrue(employmentRule.appliesTo(employmentHousehold))
        assertEquals(employmentRule.evaluate(employmentHousehold), 2)

        assertTrue(ageRule.appliesTo(employmentHousehold))
        assertTrue(ageRule.appliesTo(unitHousehold))
        assertEquals(ageRule.evaluate(employmentHousehold), 2)
        assertEquals(ageRule.evaluate(unitHousehold), 2)

        assertTrue(genderRule.appliesTo(employmentHousehold))
        assertTrue(genderRule.appliesTo(unitHousehold))
        assertEquals(genderRule.evaluate(employmentHousehold), 1)
        assertEquals(genderRule.evaluate(unitHousehold), 2)
    }

    @Test
    fun noRuleProducesNoOutput() {
        val output = employmentIPU.synthesize(listOf(employmentHousehold), emptyMap())
        assertTrue(output.isEmpty())
    }

    @Test
    fun employmentIPUOutput() {
        val ruleSet = listOf(employmentRule)
        val output = employmentIPU.synthesize(listOf(employmentHousehold), mapOf(TEST_ZONE to ruleSet))
        val result = output[testZone]
        assertNotNull(result)
        assertEquals(result.size, 2)
        matchesEmploymentHousehold(result[0])
        matchesEmploymentHousehold(result[1])
    }

    @Test
    fun unitIPUAge() {
        val ruleSet = listOf(ageRule)
        assertEquals(
            ScalableVector.createFrom(employmentHousehold, ruleSet),
            ScalableVector.createFrom(unitHousehold, ruleSet)
        )
        val output = unitIPU.synthesize(listOf(employmentHousehold, unitHousehold), mapOf(TEST_ZONE to ruleSet))
        val result = output[testZone]
        assertNotNull(result)
        assertEquals(result.size, 3)
        matchesEmploymentHousehold(result[0])
        matchesUnitHousehold(result[1])
        matchesEmploymentHousehold(result[2])
    }

    @Test
    fun unitIPUGender() {
        val ruleSet = listOf(genderRule)
        assertNotEquals(
            ScalableVector.createFrom(employmentHousehold, ruleSet),
            ScalableVector.createFrom(unitHousehold, ruleSet)
        )
        val output = unitIPU.synthesize(listOf(employmentHousehold, unitHousehold), mapOf(TEST_ZONE to ruleSet))
        val result = output[testZone]
        assertNotNull(result)
        assertEquals(result.size, 6)
        matchesEmploymentHousehold(result[0])
        matchesEmploymentHousehold(result[1])
        matchesEmploymentHousehold(result[2])
        matchesUnitHousehold(result[3])
        matchesUnitHousehold(result[4])
        matchesUnitHousehold(result[5])
    }

    private fun matchesEmploymentHousehold(target: SynthesisHousehold<*>) {
        target.members[0].run {
            assertIs<SynthesisPerson<Employment>>(this)
            assertEquals(age, 10)
            assertEquals(sex, Sex.FEMALE)
            assertEquals(info, Employment.NONE)
        }
        target.members[1].run {
            assertIs<SynthesisPerson<Employment>>(this)
            assertEquals(age, 10)
            assertEquals(sex, Sex.MALE)
            assertEquals(info, Employment.NONE)
        }
    }

    private fun matchesUnitHousehold(target: SynthesisHousehold<*>) {
        target.members[0].run {
            assertIs<SynthesisPerson<Unit>>(this)
            assertEquals(age, 10)
            assertEquals(sex, Sex.MALE)
        }
        target.members[1].run {
            assertIs<SynthesisPerson<Unit>>(this)
            assertEquals(age, 10)
            assertEquals(sex, Sex.MALE)
        }
    }
}
