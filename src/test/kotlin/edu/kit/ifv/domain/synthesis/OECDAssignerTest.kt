package edu.kit.ifv.domain.synthesis
import edu.kit.ifv.domain.shared.enums.household.EconomicStatus
import edu.kit.ifv.domain.shared.enums.household.HouseholdType
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributesImpl
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyHousehold
import edu.kit.ifv.domain.synthesis.behavior.economicstatus.OECDAssigner
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
