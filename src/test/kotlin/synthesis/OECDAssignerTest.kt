package synthesis

import domain.data.EconomicStatus
import org.junit.jupiter.api.Test
import synthesis.domain.SynthesisHousehold
import kotlin.io.path.Path
import kotlin.test.assertEquals

class OECDAssignerTest {
    @Test
    fun properTableReadin() {
        val assigner = OECDAssigner.fromPath<SurveyInfo>(
            Path("src/test/resources/synthesis/economical-status-oecd2017.csv")
        )
        val testCandidate = SynthesisHousehold<SurveyInfo>()
        testCandidate.economicStatus = assigner.determineStatus(testCandidate)
        assertEquals(testCandidate.economicStatus, EconomicStatus.VERY_LOW)
    }
}
