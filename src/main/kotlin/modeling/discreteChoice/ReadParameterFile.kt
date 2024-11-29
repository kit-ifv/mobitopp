package modeling.discreteChoice

import net.objecthunter.exp4j.ExpressionBuilder
import splitOnce
import java.io.File
import java.nio.charset.Charset

/**
 * Read a text or a text file as a parameter file. "PARAMETERNAME = 49.42" etc.
 */
object ReadParameterFile {
    fun read(string: String): Map<String, Double> {
        val map = string.split("\n").filter { it.contains("=") }.map { it.splitOnce("=") }
            .map { it.first.trim() to evaluateExpression(it.second) }.associate { it }
        return map
    }
    fun read(file: File, charset: Charset = Charsets.UTF_8): Map<String, Double> {
        return read(file.readText(charset = charset))
    }
}

/**
 * Use Exp4j to parse the mathematical expression found in the legacy parameter files
 */
private fun evaluateExpression(expression: String): Double {
    return try {
        val exp = ExpressionBuilder(expression).build()
        exp.evaluate()
    } catch (e: Exception) {
        throw IllegalArgumentException("Error evaluating expression: $expression", e)
    }
}