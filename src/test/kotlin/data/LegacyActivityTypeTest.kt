package data

import domain.data.LegacyActivityType
import kotlin.test.Test
import kotlin.test.assertEquals
class LegacyActivityTypeTest {
    @Test
    fun codingActivityType() {
        LegacyActivityType.values().forEach { assertEquals(it, LegacyActivityType.decode(it.encode()))}
    }
}
