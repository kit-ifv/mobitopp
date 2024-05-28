
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Test
import utils.Builder
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CompileTest {

    @Test
    fun `test simple compilation`() {
        val source = SourceFile.kotlin("Hello.kt", TEST1)
        lateinit var output: File
        val result = KotlinCompilation().apply {
            sources = listOf(source)
            symbolProcessorProviders = listOf(ProcessorProvider())
            inheritClassPath = true
            output = kspSourcesDir
            messageOutputStream = System.out // See compiler messages in the console
            kspWithCompilation = true
        }.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val klazzz = result.classLoader.loadClass("IHaveDefaultBuilder")
        val klazzz2 = result.classLoader.loadClass("IHaveDefault")
        val fields = klazzz.declaredFields
        val test = klazzz.declaredMethods
        val constructors = klazzz.declaredConstructors
        val t = klazzz.constructors.first()
        val expect = t.newInstance()

        assertIs<Builder<*>>(expect)
        val e = expect.build()
        println(expect)



    }
}

const val TEST1 = """
@Buildable
data class Temp<T>(
    val t: T,
    val i: Int = 0,
    val j: Int = 0,
    val k: Int = 0,
) {
    constructor(string: String): this()
}
"""

const val TEST2 = """
@Buildable
interface IHaveDefault<T: Number> {
    val int: Int
        get() = 0
}"""
interface Teemp {
    val i
        get() = 0
}
@Buildable
data class Temp(
    val i: Int? = 0,
    val j: Int = 0,
    val k: String = "0",
) {

    fun temp() {
        val tm = mapOf("i" to i, "j" to j, "k" to k).filter {
            (_, v) -> v == null }.keys.toList()
    }
}