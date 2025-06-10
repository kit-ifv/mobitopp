package domain.data

import domain.simulation.behavior.LegacyActivityType
import kotlin.test.Test
import kotlin.test.assertEquals
class LegacyPlannedActivityTypeTest {
    @Test
    fun codingActivityType() {
        LegacyActivityType.entries.forEach { assertEquals(it, LegacyActivityType.decode(it.code)) }
    }
}
