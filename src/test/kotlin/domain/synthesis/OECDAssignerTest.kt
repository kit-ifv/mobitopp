package domain.synthesis

import domain.synthesis.behavior.OECDAssigner
import domain.synthesis.behavior.SurveyInfo
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.data.EconomicStatus
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.test.assertEquals

class OECDAssignerTest {
    @Test
    fun properTableReadin() {
        val assigner = OECDAssigner.fromPath<SurveyInfo>(
            Path("src/main/resources/economical-status-oecd2017.csv")
        )
        val testCandidate = SynthesisHousehold<SurveyInfo>()
        testCandidate.economicStatus = assigner.determineStatus(testCandidate)
        assertEquals(testCandidate.economicStatus, EconomicStatus.VERY_LOW)
    }
}
