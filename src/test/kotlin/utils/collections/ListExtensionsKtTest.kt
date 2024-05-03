package utils.collections

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListExtensionsKtTest {
    @Test
    fun addByOrderWorks() {
        val list = mutableListOf(1, 2, 4, 5)
        assertTrue(list.addByOrder(3))
        assertEquals(list, listOf(1, 2, 3, 4, 5))
    }
    @Test
    fun cumulativeSum() {
        val list = listOf(1, 2, 3, 4, 5)
        val cumulative = list.cumulativeSum(Int::plus)
        assertEquals(cumulative, listOf(1, 3, 6, 10, 15))
    }
}
