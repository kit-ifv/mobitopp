package utils

const val INDENT = "    "

/**
 * Filters out newlines from the string and returns only the newlines if present.
 *
 * @return A string containing only the newlines if the original string contains any.
 */
fun String.filterNewlines(): String {
    return if (this.contains("\n")) "\n".repeat(this.count { it == '\n' }) else ""
}

/**
 * Indents the string if it is not blank.
 *
 * @return The indented string if the original string is not blank.
 */
fun String.indentIfText(): String {
    return if (this.isNotBlank()) this.indent() else ""
}

/**
 * A builder class for creating text with a specified separator.
 *
 * @property sep The separator to use between text elements.
 */
class TextBuilder(private val sep: String = "\n") {
    private val result = mutableListOf<String>()

    /**
     * Adds the string to the result list.
     *
     * @receiver The string to add.
     */
    operator fun String.unaryPlus() {
        result.add(this)
    }

    /**
     * Creates the text by joining the result list with the specified separator.
     *
     * @return The constructed text.
     */
    fun makeText(): String {
        return result.joinToString(separator = sep, prefix = sep.filterNewlines())
    }
}

/**
 * Invokes a [TextBuilder] with the specified separator and builds the text.
 *
 * @receiver The initial string.
 * @param sep The separator to use.
 * @param s The lambda function to execute within the [TextBuilder].
 * @return The constructed string with the text from the [TextBuilder].
 */
operator fun String.invoke(sep: String = "\n", s: TextBuilder.() -> Unit): String {
    val t = TextBuilder(sep)
    t.s()
    val trimmedSep: String = sep.filterNewlines()
    val text = t.makeText()
    return this + " {${text.indentIfText()}$trimmedSep}"
}

/**
 * Invokes the string with a vararg of strings and joins them with a separator.
 *
 * @receiver The initial string.
 * @param x The vararg of strings to join.
 * @return The constructed string with the joined vararg of strings.
 */
operator fun String.invoke(vararg x: String): String {
    val joinToString = x.joinToString(separator = ",\n", prefix = "\n")
    return "$this(${joinToString.indentIfText()}\n)"
}

/**
 * Joins the string with a vararg of strings inline with a separator.
 *
 * @receiver The initial string.
 * @param x The vararg of strings to join.
 * @return The constructed string with the joined vararg of strings inline.
 */
fun String.inline(vararg x: String): String {
    val joinToString = x.joinToString(separator = ", ", prefix = "")
    return "$this(${joinToString.replace("\n", "\n$INDENT")})"
}

/**
 * Indents each line of the string with a predefined indent.
 *
 * @receiver The string to indent.
 * @return The indented string.
 */
fun String.indent(): String {
    return this.replace("\n", "\n$INDENT")
}
