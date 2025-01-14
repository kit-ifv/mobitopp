package synthesis.householdgeneration

import TestZone
import domain.data.Sex
import domain.data.Zone
import domain.data.ZoneId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import synthesis.SmallestSurveyPerson
import synthesis.SurveyHousehold
import synthesis.SurveyPerson
import units.euros
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


open class SynthesisTest {

    val zone1 = TestZone(id = ZoneId(1))
    private val zones = listOf(zone1)
    protected fun <T> createRules(lambda: ZoneBuilder<T>.() -> Unit): Map<Zone, List<Rule<T>>> {
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

    protected fun <T> createHousehold(lambda: HouseholdBuilder<T>.() -> Unit): SurveyHousehold<T> {
        val builder = HouseholdBuilder<T>()
        builder.apply(lambda)
        return builder.createHousehold()
    }

    protected fun <T> Collection<Zone>.createRules(lambda: ZoneBuilder<T>.() -> Unit): Map<Zone, List<Rule<T>>> {
        val builder = ZoneBuilder<T>(this)
        builder.apply(lambda)
        val idMap = builder.createRules()

        return associateWith { idMap[it.id] ?: emptyList() }

    }

    protected inner class ZoneBuilder<T>(zones: Collection<Zone>) {

        protected val associatedRules: MutableMap<ZoneId, List<Rule<T>>> =
            zones.associate { it.id to listOf<Rule<T>>() }.toMutableMap()

        inner class RulesForZoneBuilder {
            protected val rules: MutableList<Rule<T>> = mutableListOf()

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

            val smallestSurveyPerson = SmallestSurveyPerson(personId = members.size + 1, age = age, sex = sex, information = lambda())
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

