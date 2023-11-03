package utils.units

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class CurrencyTest : GenericUnitTest<CurrencyUnits, Currency>(
    CurrencyUnits.values(),
    Int::toCurrency,
    Long::toCurrency,
    Double::toCurrency
) {
    @Test
    fun addingMoney() {
        val money = 1.toCurrency(CurrencyUnits.EUROS)
        assertEquals(2.toCurrency(CurrencyUnits.EUROS), money + money)
    }
}
