
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import kotlin.test.assertEquals

class GeneratorTest {


    @OptIn(ExperimentalCompilerApi::class)
    fun `test simple compilation`() {
        val source = SourceFile.kotlin("Hello.kt", TEST1)
//        lateinit var output: File
        val result = KotlinCompilation().apply {
            sources = listOf(source)
            symbolProcessorProviders = listOf(ProcessorProvider())
            inheritClassPath = true
//            output = kspSourcesDir
            messageOutputStream = System.out // See compiler messages in the console
            kspWithCompilation = true
        }.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }
}

const val TEST1 = """
@Buildable
data class Temp<T>(
    val t: T,
    val i: Int = 0,
    val j: Int = 0,
    val k: Int = 0,
)
"""
