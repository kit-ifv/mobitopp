package domain.data
import domain.synthesis.data.EconomicStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class EconomicStatusTest {
    @Test
    fun codeEconomicStatus() {
        EconomicStatus.entries.forEach { assertEquals(it, EconomicStatus.decode(it.code)) }
    }
}
