package edu.kit.ifv.utils.files
val illegalChars = Regex("""[\\/:*?"<>|\u0000-\u001F]""")

fun String.toValidFileName(replacement: String = "_", maxLength: Int = 255): String = this
    .replace(illegalChars, replacement)
    .replace(" ", replacement)
    .trim()
    .trimEnd('.')
    .ifBlank { "unnamed" }
    .take(maxLength)
