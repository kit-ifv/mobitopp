package domain.synthesis

import domain.synthesis.data.household.EconomicStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class EconomicStatusTest {
    @Test
    fun codeEconomicStatus() {
        EconomicStatus.entries.forEach { assertEquals(it, EconomicStatus.Companion.decode(it.code)) }
    }
}
