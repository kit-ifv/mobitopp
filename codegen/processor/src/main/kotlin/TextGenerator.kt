
const val INDENT = "    "

fun String.filterNewlines(): String {
    return if (this.contains("\n")) "\n".repeat(this.count{it == '\n'}) else ""
}
fun String.indentIfText(): String {
    return if (this.isNotBlank()) this.indent() else ""
}
class TextBuilder(private val sep: String = "\n") {
    private val result = mutableListOf<String>()
    operator fun String.unaryPlus() {
        result.add(this)
    }
    fun makeText(): String {
        return result.joinToString(separator = "$sep", prefix = sep.filterNewlines())
    }
}
operator fun String.invoke(sep: String = "\n", s: TextBuilder.() -> Unit): String {
    val t = TextBuilder(sep)
    t.s()
    val trimmedSep: String = sep.filterNewlines()
    val text = t.makeText()
    return this + " {${text.indentIfText()}$trimmedSep}"
}

operator fun String.invoke(vararg x: String): String {
    val joinToString = x.joinToString(separator = ",\n", prefix = "\n")

    return "$this(${joinToString.indentIfText()}\n)"
}
fun String.inline(vararg x: String): String {
    val joinToString = x.joinToString(separator = ", ", prefix = "")

    return "$this(${joinToString.replace("\n", "\n${INDENT}")})"
}

fun String.indent(): String {
    return this.replace("\n", "\n${INDENT}")
}
