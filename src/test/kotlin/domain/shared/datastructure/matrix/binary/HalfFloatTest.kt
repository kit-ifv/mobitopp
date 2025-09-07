package domain.shared.datastructure.matrix.binary

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class HalfFloatTest {
    @Test
    fun specialConversions() {
        assertEquals(Double.NaN, Double.NaN.toHalfFloat().fromHalfFloat())
        assertEquals(Double.POSITIVE_INFINITY, 8000000.0.toHalfFloat().fromHalfFloat())
        assertEquals(0.0, 0.0.toHalfFloat().fromHalfFloat())
    }
}