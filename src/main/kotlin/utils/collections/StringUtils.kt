package utils.collections

fun String.enforceIndent(spaces: Int = 2): String {
    val indent = " ".repeat(spaces)
    return this.lines().joinToString("\n") { indent + it.trimIndent() }
}
