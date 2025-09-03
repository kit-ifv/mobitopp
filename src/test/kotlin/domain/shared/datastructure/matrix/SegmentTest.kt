package domain.shared.datastructure.matrix

import core.datastructure.calendarLookup.TimeSegment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SegmentTest {
    @Test
    fun splicing() {
        val segment = TimeSegment(0, 10, Unit)

        val output = segment.subtract(2, 4)
        assertEquals(output.size, 2)
        assertEquals(output[0], TimeSegment(0, 2, Unit))
        assertEquals(output[1], TimeSegment(4, 10, Unit))
    }

    @Test
    fun intersecting() {
        val segment = TimeSegment(0, 10, Unit)
        val a = TimeSegment(-2, 2, Unit)
        val b = TimeSegment(8, 12, Unit)
        val c = TimeSegment(4, 3, Unit)
        val d = TimeSegment(-10, -2, Unit)
        val e = TimeSegment(10, 12, Unit)
        assertTrue(segment.intersects(a))
        assertTrue(segment.intersects(b))
        assertTrue(segment.intersects(c))
        assertFalse(segment.intersects(d))
        assertFalse(segment.intersects(e))
    }
}
