import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotEquals

class ExampleTest {
    @Test
    fun creatorTest() {
        assertEquals("Robin", creator())
    }

    @Test
    fun nonCreatorTest() {
        assertNotEquals("PETER", creator())
    }
}
