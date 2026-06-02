package domain.synthesis

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.household.MinimumHouseholdAttributesImpl
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyHousehold
import domain.synthesis.behavior.economicstatus.OECDAssigner
import domain.synthesis.data.household.EconomicStatus
import domain.synthesis.data.household.HouseholdType
import edu.kit.ifv.units.euros
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OECDAssignerTest {

    @Test
    fun defaultWorks() {
        val assigner = OECDAssigner.default()
        val status = assigner.determineStatus(
            SurveyHousehold<MinimumHouseholdAttributes, MinimumPersonAttributes>(
                1,
                emptyList(),
                MinimumHouseholdAttributesImpl(income = 300.euros, type = HouseholdType.UNDEFINED),
            ),
        )

        assertEquals(status, EconomicStatus.VERY_LOW)
    }
}
