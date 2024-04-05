package datastructure

import kotlin.test.Test
import kotlin.test.assertEquals

class DualSetContainerTest {
    @Test
    fun testSetListAcceptance() {
        val test = SetListBuilder().apply {
            container {
                f[1]
                s[2]
            }
            container {
                f[3]
                s[4, 6]
            }
            container {
                f[7]
                s[99]
            }
        }.elements
        assertEquals(-1, test[0].accepts((C(5))))
        assertEquals(0, test[1].accepts((C(5))))
        assertEquals(1, test[1].accepts((C(5))))
    }
}
