package utils.units


import kotlin.test.Test
import kotlin.test.assertEquals


class AreaTest {
    @Test
    fun creation() {
        val area = Area.of(1.meters, 1.meters)

        val test = area.toDouble(AreaUnits.SQUARE_KILOMETERS)
        assertEquals(0.000001, test)

    }
}