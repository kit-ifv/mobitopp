package domain.synthesis.rules

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.household.MinimumHouseholdAttributesImpl
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SmallestSurveyPerson
import domain.synthesis.behavior.SurveyHousehold
import domain.synthesis.data.household.HouseholdType
import domain.synthesis.data.person.Sex
import domain.synthesis.rules.measurements.PersonAgeSexDefinition
import edu.kit.ifv.units.euros
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class PersonAgeSexFactoryTest {

    // Projects all definitions to the target of 42.
    private class CheatyDefiner(private val elements: Collection<PersonAgeSexDefinition>) :
        PersonAgeSexFactory<Unit>({
            elements.map { it to 42.0 }
        }) {
        constructor(vararg elements: Pair<IntRange, Sex>) : this(
            elements.map { PersonAgeSexDefinition(it.first, it.second) },
        )
    }

    private val m = Sex.MALE
    private val f = Sex.FEMALE

    private fun buildPerson(age: Int, sex: Sex): ISurveyHousehold<MinimumHouseholdAttributes, MinimumPersonAttributes> {
        val person: SmallestSurveyPerson<MinimumPersonAttributes> =
            SmallestSurveyPerson<MinimumPersonAttributes>(
                personId = 1337,
                attributes = object : MinimumPersonAttributes {
                    override val age: Int = age
                    override val sex: Sex = sex
                },
            )
        val household = SurveyHousehold<MinimumHouseholdAttributes, MinimumPersonAttributes>(
            1L,
            listOf(person),
            MinimumHouseholdAttributesImpl(1.euros, HouseholdType.OTHER_MULTI_PERSON_HH),
        )
        return household
    }

    @Test
    fun properHoleCheck() {
        val withHole = CheatyDefiner(
            1..12 to m,
            14..20 to m,
        )

        assertThrows<IllegalArgumentException> {
            withHole.buildRuleSet(Unit)
        }
    }

    @Test
    fun happyPath() {
        val sane = CheatyDefiner(
            1..12 to m,
            13..20 to m,
            5..20 to f,
            21..29 to f,
        )

        val ruleSet = sane.buildRuleSet(Unit).toList()
        var person = buildPerson(0, m)

        assertEquals(ruleSet[0].measure(person), 1.0)
        assertEquals(ruleSet[1].measure(person), 0.0)

        person = buildPerson(0, f)
        assertEquals(ruleSet[2].measure(person), 1.0)
        assertEquals(ruleSet[3].measure(person), 0.0)

        person = buildPerson(13, m)
        assertEquals(ruleSet[0].measure(person), 0.0)
        assertEquals(ruleSet[1].measure(person), 1.0)

        person = buildPerson(21, f)
        assertEquals(ruleSet[2].measure(person), 0.0)
        assertEquals(ruleSet[3].measure(person), 1.0)

        person = buildPerson(900, m)
        assertEquals(ruleSet[0].measure(person), 0.0)
        assertEquals(ruleSet[1].measure(person), 1.0)

        person = buildPerson(900, f)
        assertEquals(ruleSet[2].measure(person), 0.0)
        assertEquals(ruleSet[3].measure(person), 1.0)
    }
}
