package data

import domain.enums.LegacyActivityType
import kotlin.test.Test
import kotlin.test.assertEquals
class LegacyPlannedActivityTypeTest {
    @Test
    fun codingActivityType() {
        LegacyActivityType.entries.forEach { assertEquals(it, LegacyActivityType.decode(it.encode())) }
    }
}
