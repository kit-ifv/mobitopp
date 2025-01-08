package utils.collections

fun String.enforceIndent(spaces: Int = 2): String {
    val indent = " ".repeat(spaces)
    return this.lines().joinToString("\n") { indent + it.trimIndent() }
}

fun String.indentSubsequentLines(indentMarker: String = ":", prefix: String = ""): String {

    val indentSize = (this.indexOf(indentMarker) + 2 - prefix.length).let {
        if (it < 0) 1 else it
    }

    val lines = this.lines()
    if (lines.size <= 1) {
        return this
    }

    return lines.let {
        lines.first() + "\n" +
        lines.drop(1).joinToString("\n") { line ->
            prefix + " ".repeat(indentSize) + line
        }
    }

}
