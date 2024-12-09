package synthesis

import domain.data.EconomicStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class OECDAssignerTest {
    @Test
    fun properTableReadin() {
        val assigner = OECDAssigner.fromPath(Path("src/test/resources/synthesis/economical-status-oecd2017.csv"))
        val testCandidate = SynthesisHouseholdBuilder()
        assigner.assign(testCandidate)
        assertEquals(testCandidate.economicStatus, EconomicStatus.VERY_LOW)
    }
}