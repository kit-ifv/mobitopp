package synthesis

import domain.data.EconomicStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import units.euros
import kotlin.io.path.Path

class OECDAssignerTest {
    @Test
    fun properTableReadin() {
        val assigner = OECDAssigner.fromFile(Path("src/test/resources/synthesis/economical-status-oecd2017.csv"))
        val testCandidate = LocatedHousehold(
            SurveyHousehold(-1, 499.euros, emptyList()),
            location =
        )
        val result = assigner.assign(testCandidate)
        assertEquals(result.economicStatus, EconomicStatus.VERY_LOW)
    }
}