package edu.kit.ifv.utils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PerpetualCacheTest {
    @Test
    fun cacheTest() {
        val target = PerpetualCache<Int, Int>()
        assertNull(target[42])
        assertEquals(target.size, 0)
        target.getOrPut(42) { 0 }
        assertEquals(target.size, 1)
        assertEquals(target[42], 0)
    }
}
