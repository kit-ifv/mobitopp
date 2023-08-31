import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotEquals

class ExampleTest {
    @Test
    fun creatorTest() {
        assertEquals("Robiin", creator())
    }

    @Test
    fun nonCreatorTest() {
        assertNotEquals("PETER", creator())
    }
}
