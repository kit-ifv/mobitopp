package edu.kit.ifv.domain.synthesis.results.fastcsv
import assertNotContains
import java.io.StringWriter
import kotlin.test.Test
import kotlin.test.assertContains

class WriteCsvWithAttributeProjectionTest {
    internal class Output(
        @CsvRename("good")
        val bad: Unit = Unit,
        val e: Unit = Unit,
        @CsvIgnore
        val ignore: Unit = Unit,
    )

    internal class Wrapper<T>(val t: T)

    @Test
    fun writeStandardOutputCSV() {
        val writer = StringWriter()
        listOf(Wrapper<Output>(Output())).writeCsvWithGenericAttributes(
            writer,
            attributeExtractor =
            { it.t },
            headerPrefix = emptyList(),
            outputPrefix = { emptyList() },
        )
        val split = writer.toString().split("\r\n").first()
        assertContains(split, "e")
        assertContains(split, "good")
        assertNotContains(split, "ignore")
    }
}
