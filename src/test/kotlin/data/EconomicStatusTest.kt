package data
import kotlin.test.Test
import kotlin.test.assertEquals

class EconomicStatusTest {
    @Test
    fun codeEconomicStatus() {
        EconomicStatus.values().forEach { assertEquals(it, DEFAULT_ECONOMIC_PARSER.decode(it.encode())) }
    }
}
