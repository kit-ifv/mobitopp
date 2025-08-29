package domain.shared.datastructure.matrix

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SegmentTest {
    @Test
    fun splicing() {
        val segment = Segment(0, 10, Unit)

        val output = segment.splice(2, 4)
        assertEquals(output.size, 2)
        assertEquals(output[0], Segment(0, 2, Unit))
        assertEquals(output[1], Segment(4, 10, Unit))
    }

    @Test
    fun intersecting() {
        val segment = Segment(0, 10, Unit)
        val a = Segment(-2, 2, Unit)
        val b = Segment(8, 12, Unit)
        val c = Segment(4, 3, Unit)
        val d = Segment(-10, -2 ,Unit)
        val e = Segment(10, 12 ,Unit)
        assertTrue(segment.intersects(a))
        assertTrue(segment.intersects(b))
        assertTrue(segment.intersects(c))
        assertFalse(segment.intersects(d))
        assertFalse(segment.intersects(e))
    }

}