package domain.synthesis.householdgeneration

import TestZone
import core.location.Location
import domain.synthesis.behavior.SmallestSurveyPerson
import domain.synthesis.behavior.SurveyHousehold
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.householdgeneration.CheckRule
import domain.synthesis.behavior.householdgeneration.CountRule
import domain.synthesis.behavior.householdgeneration.Rule
import domain.synthesis.behavior.householdgeneration.ZoneCheckRule
import domain.synthesis.behavior.householdgeneration.ZoneRule
import domain.synthesis.data.Sex
import domain.synthesis.data.Zone
import domain.synthesis.data.ZoneId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import units.Coordinate
import units.Distance
import units.Radians
import units.euros
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
class ToolTest : SynthesisTest() {
    val zone1 = TestZone(id = ZoneId(1))
    private val zones = listOf(zone1)

    private fun <T> createRules(lambda: ZoneBuilder<T>.() -> Unit): Map<Zone, List<Rule<T>>> {
        return zones.createRules(lambda)
    }

    @Test
    fun checkHouseholdCreation() {
        val firstAge = 10
        val firstSex = Sex.MALE
        val secondAge = 42
        val secondSex = Sex.FEMALE
        val household = createHousehold<Any> {
            person(firstAge, firstSex) {
            }
            person(secondAge, secondSex) {
            }
        }
        assertEquals(household.size, 2)
        val firstPerson = household.members[0]
        assertEquals(firstPerson.age, firstAge)
        assertEquals(firstPerson.sex, firstSex)

        val secondPerson = household.members[1]
        assertEquals(secondPerson.age, secondAge)
        assertEquals(secondPerson.sex, secondSex)
    }

    @Test
    fun checkCreation() {
        val ruleDescription = "Test Description"
        val ruleTarget = 42
        val ruleSet = createRules<Any> {
            zone(1) {
                rule {
                    description = ruleDescription
                    desiredAmount = ruleTarget
                    condition = CheckRule { it.size == 1 }
                }
            }
        }
        assertContentEquals(listOf(zone1), ruleSet.keys)
        assertNotNull(ruleSet[zone1])
        assertEquals(ruleSet[zone1]!!.size, 1)
        val rule = ruleSet[zone1]!!.first()
        assertEquals(rule.description, ruleDescription)
        assertEquals(rule.target, ruleTarget)

        val fittingHousehold = createHousehold<Any> {
            person(10, Sex.MALE) {
            }
        }
        val mismatchingHousehold = createHousehold<Any> {
            person(10, Sex.MALE) {
            }

            person(10, Sex.MALE) {
            }
        }
        assertTrue(rule.appliesTo(fittingHousehold))
        assertFalse(rule.appliesTo(mismatchingHousehold))
    }
}
open class SynthesisTest {

    protected fun <T> createHousehold(lambda: HouseholdBuilder<T>.() -> Unit): SurveyHousehold<T> {
        val builder = HouseholdBuilder<T>()
        builder.apply(lambda)
        return builder.createHousehold()
    }

    /**
     * Spawn in a synthesis household, if you happen to have a location at hand where the household should be.
     */
    protected fun <T> Location.createHousehold(lambda: HouseholdBuilder<T>.() -> Unit): SynthesisHousehold<T> {
        val builder = HouseholdBuilder<T>()
        builder.apply(lambda)
        val synthesisHousehold = builder.createHousehold().toSynthesisHousehold()
        synthesisHousehold.location = this
        return synthesisHousehold
    }
    protected fun <T> Collection<Zone>.createRules(lambda: ZoneBuilder<T>.() -> Unit): Map<Zone, List<Rule<T>>> {
        val builder = ZoneBuilder<T>(this)
        builder.apply(lambda)
        val idMap = builder.createRules()

        return associateWith { idMap[it.id] ?: emptyList() }
    }

    protected inner class ZoneBuilder<T>(zones: Collection<Zone>) {

        private val associatedRules: MutableMap<ZoneId, List<Rule<T>>> =
            zones.associate { it.id to listOf<Rule<T>>() }.toMutableMap()

        inner class RulesForZoneBuilder {
            private val rules: MutableList<Rule<T>> = mutableListOf()

            inner class ZoneRuleBuilder {
                lateinit var description: String
                var desiredAmount: Int = 0
                lateinit var condition: CountRule<T>
                fun toRule(): ZoneRule<T> {
                    return ZoneRule(description, desiredAmount, condition)
                }
            }

            fun numericRule(lambda: ZoneRuleBuilder.() -> Unit) {
                val builder = ZoneRuleBuilder()
                builder.apply(lambda)
                rules.add(builder.toRule())
            }

            fun createRules(): List<Rule<T>> {
                return rules
            }

            inner class ZoneCheckRuleBuilder {
                lateinit var description: String
                var desiredAmount: Int = 0
                lateinit var condition: CheckRule<T>
                fun toRule(): ZoneCheckRule<T> {
                    return ZoneCheckRule(description, desiredAmount, condition)
                }
            }

            fun rule(lambda: ZoneCheckRuleBuilder.() -> Unit) {
                val builder = ZoneCheckRuleBuilder()
                builder.apply(lambda)
                rules.add(builder.toRule())
            }
        }

        fun zone(id: Int, lambda: RulesForZoneBuilder.() -> Unit) {
            val builder = RulesForZoneBuilder()
            builder.apply(lambda)
            associatedRules[ZoneId(id.toLong())] = builder.createRules()
        }

        fun createRules(): Map<ZoneId, List<Rule<T>>> = associatedRules
    }
    protected fun fakeLocation() = Location(FakeCoord(), null, null)
    protected fun Zone.spawnFakeLoc(): Location {
        return Location(FakeCoord(), this, null)
    }

    protected fun Zone.spawnLocation(coordinate: Coordinate): Location = Location(coordinate, this, null)

    protected class FakeCoord : Coordinate {
        val id = counter
        override val latitudeRadians: Radians = Radians(0.0)
        override val longitudeRadians: Radians = Radians(0.0)
        override fun distance(other: Coordinate): Distance {
            error("This method should never be called for this test to work")
        }

        override fun toString(): String {
            return "FakeLoc($id)"
        }

        companion object {
            var counter: Int = 0
                get() = field++
                private set
        }
    }
    protected class HouseholdBuilder<T> {
        var id: Int = 0
        var income = 0.euros
        val members: MutableList<SurveyPerson<out T>> = mutableListOf()

        inner class PersonBuilder {
            var sex: Sex = Sex.MALE
            var age: Int = 0
            lateinit var information: () -> T
            fun toPerson(): SurveyPerson<T> {
                return SmallestSurveyPerson(-1, age = age, sex = sex, information = information())
            }
        }

        fun person(age: Int, sex: Sex, lambda: () -> T): SmallestSurveyPerson<T> {
            val smallestSurveyPerson =
                SmallestSurveyPerson(personId = members.size + 1, age = age, sex = sex, information = lambda())
            members.add(smallestSurveyPerson)
            return smallestSurveyPerson
        }

        fun createHousehold(): SurveyHousehold<T> {
            return SurveyHousehold(id, income, members)
        }
    }

    protected class TrivialSurveyPerson(
        override val personId: Int,
        override val information: Any = Unit,
        override val age: Int,
        override val sex: Sex
    ) : SurveyPerson<Any>
}
