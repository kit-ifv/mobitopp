
import kotlin.test.Test
import kotlin.test.assertEquals


class TextBuilderTest {
    @Test
    fun curly() {
        val result = "test"(sep= "\n" ) {
            +"internalString"
        }
        val expected = "test {\n${INDENT}internalString\n}"
        assertEquals(result, expected)
        print(result)
    }

    @Test
    fun curlyWithoutSeparator() {
        val result = "test"(sep= " ") {
            +"internalString"
        }
        val expected = "test {internalString}"

        assertEquals(result, expected)
        print(result)
    }

    @Test
    fun curlyTwoElementsInline() {
        val result = "test"(sep= ", ") {
            +"A"
            +"B"
        }
        val expected = "test {A, B}"
        assertEquals(result, expected)
        print(result)
    }

    @Test
    fun curlyTwoElements() {
        val result = "test"(sep= "\n") {
            +"A"
            +"B"
        }
        val expected = "test {\n${INDENT}A\n${INDENT}B\n}"
        assertEquals(result, expected)
        print(result)
    }
    @Test
    fun curlyNested() {

        val result = "test" {
            + "A" {
                +"B"
            }
        }
        val expected = "test {\n${INDENT}A {\n${INDENT}${INDENT}B\n${INDENT}}\n}"
        assertEquals(expected, result)
        print(result)
    }
    @Test
    fun emptyCurly() {
        val result = "test" {

        }
        val expected = "test {\n}"

        assertEquals(expected, result)
        print(result)
    }

    @Test
    fun emptyCurlyInline() {
        val result = "test"(sep = " ") {

        }
        val expected = "test {}"

        assertEquals(expected, result)
        print(result)
    }

    @Test
    fun bracketsInline() {
        val result = "test".inline("A")
        val expected = "test(A)"

        print(result)
        assertEquals(expected, result)
    }
    @Test
    fun bracketsInlineTwoElements() {
        val result = "test".inline("A", "B")
        val expected = "test(A, B)"

        print(result)
        assertEquals(expected, result)
    }

    @Test
    fun bracketsTwoElements() {
        val result = "test"("A", "B")
        val expected = "test(\n${INDENT}A,\n${INDENT}B\n)"

        print(result)
        assertEquals(expected, result)
    }
    @Test
    fun brackets() {
        val result = "test"("A")
        val expected = "test(\n${INDENT}A\n)"

        print(result)
        assertEquals(expected, result)
    }

    @Test
    fun emptyBracketsInline() {
        val result = "test".inline()
        val expected = "test()"

        print(result)
        assertEquals(expected, result)
    }

    @Test
    fun emptyBrackets() {
        val result = "test"()
        val expected = "test(\n)"

        print(result)
        assertEquals(expected, result)
    }

    @Test
    fun inlineWithInternalLineBreak() {
        val result = "test".inline("A\nB")
        val expected = "test(A\n${INDENT}B)"

        print(result)
        assertEquals(expected, result)
    }

    @Test
    fun bracesWithInternalLineBreak() {
        val result = "test"("A\nB")
        val expected = "test(\n${INDENT}A\n${INDENT}B\n)"

        print(result)
        assertEquals(expected, result)
    }

    @Test
    fun bracketsInternalLinebreak() {
        val result = "test"{+"A\nB"}
        val expected = "test {\n${INDENT}A\n${INDENT}B\n}"

        print(result)
        assertEquals(expected, result)
    }

    @Test
    fun bracketsInlineInternalLinebreak() {
        val result = "test"(sep= " "){+"A\nB"}
        val expected = "test {A\n${INDENT}B}"

        print(result)
        assertEquals(expected, result)
    }


}