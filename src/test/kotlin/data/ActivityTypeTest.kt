package data

import kotlin.test.Test
import kotlin.test.assertEquals
class ActivityTypeTest {
    @Test
    fun codingActivityType() {
        ActivityType.values().forEach { assertEquals(it, DEFAULT_ACTIVITY_PARSER.decode(it.encode()))}
    }
}
