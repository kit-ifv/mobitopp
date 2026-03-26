package domain.synthesis

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.household.MinimumHouseholdAttributesImpl
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.economicstatus.OECDAssigner
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.HouseholdType
import edu.kit.ifv.units.euros
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.test.assertEquals

class OECDAssignerTest {
    @Test
    fun properTableReadin() {
        val assigner = OECDAssigner.fromPath(
            Path("src/main/resources/economical-status-oecd2017.csv")
        )
        val testCandidate = SynthesisHousehold<MinimumHouseholdAttributes, MinimumPersonAttributes>(
            attributes = MinimumHouseholdAttributesImpl(
                income = 1.euros,
                type = HouseholdType.UNDEFINED,
            ),
            surveyHouseholdId = -1,
        )
        testCandidate.economicStatus = assigner.determineStatus(testCandidate)
        assertEquals(testCandidate.economicStatus, EconomicStatus.VERY_LOW)
    }
}
