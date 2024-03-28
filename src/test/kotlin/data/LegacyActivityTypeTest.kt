package data

import domain.enums.LegacyActivityType
import kotlin.test.Test
import kotlin.test.assertEquals
class LegacyActivityTypeTest {
    @Test
    fun codingActivityType() {
        LegacyActivityType.entries.forEach { assertEquals(it, LegacyActivityType.decode(it.encode())) }
    }
}
