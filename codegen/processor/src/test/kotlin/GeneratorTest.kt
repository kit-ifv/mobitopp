

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Test
import kotlin.test.assertEquals

class CompileTest {

    @Test
    fun `test simple compilation`() {
        val source = SourceFile.kotlin("Hello.kt", """
            @Buildable
data class IHaveADefault(val i: Int = 0)
        """)

        val result = KotlinCompilation().apply {
            sources = listOf(source)
            symbolProcessorProviders = listOf(ProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out // See compiler messages in the console
        }.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }
}

data class Temp()