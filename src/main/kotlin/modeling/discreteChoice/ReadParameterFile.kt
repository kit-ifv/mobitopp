package modeling.discreteChoice

import net.objecthunter.exp4j.ExpressionBuilder
import processor.builder.splitOnce
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.io.path.readText

/**
 * Read a text or a text file as a parameter file. "PARAMETERNAME = 49.42" etc.
 */
object ReadParameterFile {
    fun read(string: String): Map<String, Double> {
        val map = string.split("\n").filter { it.contains("=") }.map { it.splitOnce("=") }
            .map { it.first.trim() to evaluateExpression(it.second) }.associate { it }
        return map
    }

    fun read(file: Path, charset: Charset = Charsets.UTF_8): Map<String, Double> {
        return read(file.readText(charset = charset))
    }
}

/**
 * Use Exp4j to parse the mathematical expression found in the legacy parameter files
 */
@Suppress("TooGenericExceptionCaught") // This is a exp4j problem throwing a wild assortment of exceptions
private fun evaluateExpression(expression: String): Double {
    return try {
        val exp = ExpressionBuilder(expression).build()
        exp.evaluate()
    } catch (e: Exception) {
        throw IllegalArgumentException("Error evaluating expression: $expression", e)
    }
}
