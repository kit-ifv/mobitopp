package edu.kit.ifv.domain.synthesis.behavior.activitygeneration

import edu.kit.ifv.domain.shared.enums.LegacyActivityType
import edu.kit.ifv.domain.shared.enums.household.HouseholdType
import edu.kit.ifv.domain.shared.enums.person.Sex
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold
import edu.kit.ifv.domain.synthesis.behavior.SmallestSurveyPerson
import edu.kit.ifv.domain.synthesis.behavior.SurveyHousehold
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson
import edu.kit.ifv.domain.synthesis.behavior.activitygeneration.children.ChildActivityScheduleCsvReader
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.euros
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class OverridingHouseholdActivityGeneratorTest {
    private class TestHouseholdGenerator :
        GenerateHouseholdActivitySchedule<MinimumHouseholdAttributes, MinimumPersonAttributes> {
        override fun generate(
            household: ISurveyHousehold<MinimumHouseholdAttributes, MinimumPersonAttributes>,
        ): List<PreliminaryActivitySchedule> = (0 until household.size).map {
            PreliminaryActivitySchedule.STAY_AT_HOME
        }
    }
    private class HouseholdBuilder {
        private val members: MutableList<SurveyPerson<MinimumPersonAttributes>> = mutableListOf()
        fun member(age: Int, sex: Sex = Sex.MALE) {
            members.add(
                SmallestSurveyPerson<MinimumPersonAttributes>(
                    1,
                    object : MinimumPersonAttributes {
                        override val age: Int = age
                        override val sex: Sex = sex
                    },
                ),
            )
        }

        fun build(): List<SurveyPerson<MinimumPersonAttributes>> = members
    }
    private fun household(
        lambda: HouseholdBuilder.() -> Unit,
    ): ISurveyHousehold<
        MinimumHouseholdAttributes,
        MinimumPersonAttributes,
        > {
        val builder = HouseholdBuilder()

        lambda(builder)

        return SurveyHousehold(
            -1,
            members = builder.build(),
            attributes = object : MinimumHouseholdAttributes {
                override val income: Currency
                    get() = 1.euros
                override val type: HouseholdType
                    get() = HouseholdType.UNDEFINED
                override var location: StandardLocation = StandardLocation.LOCATIONUNKNOWN
            },
        )
    }

    @Test
    fun overrideLoadsProper() {
        val activityGenerator =
            OverridingHouseholdActivityGenerator(
                TestHouseholdGenerator(),
                ChildActivityScheduleCsvReader().readBundled(
                    random = Random(
                    42,
                ),
                    decoder = {
                    LegacyActivityType.decode(it)
                }
                ),
            )
        val household = household {
            member(20)
            member(60)
            member(2)
            member(4)
            member(6)
        }
        val plans = activityGenerator.generate(household)
        assertEquals(0, plans[0].size)
        assertEquals(0, plans[1].size)
        assertNotEquals(0, plans[2].size)
        assertNotEquals(0, plans[3].size)
        assertNotEquals(0, plans[4].size)
    }
}
