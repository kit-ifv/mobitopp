package data
import domain.data.EconomicStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class EconomicStatusTest {
    @Test
    fun codeEconomicStatus() {
        EconomicStatus.values().forEach { assertEquals(it, EconomicStatus.decode(it.encode())) }
    }
}
