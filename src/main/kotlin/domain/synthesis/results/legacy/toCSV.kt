package domain.synthesis.results.legacy

fun toCSV(vararg elements: Any): String = elements.joinToString(";") { it.toString() }
