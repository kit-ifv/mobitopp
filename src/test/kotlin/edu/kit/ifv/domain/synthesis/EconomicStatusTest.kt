package edu.kit.ifv.domain.synthesis
import edu.kit.ifv.domain.shared.enums.household.EconomicStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class EconomicStatusTest {
    @Test
    fun codeEconomicStatus() {
        EconomicStatus.entries.forEach { assertEquals(it, EconomicStatus.Companion.decode(it.code)) }
    }
}
