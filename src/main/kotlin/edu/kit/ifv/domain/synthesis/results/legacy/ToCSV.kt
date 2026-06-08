package edu.kit.ifv.domain.synthesis.results.legacy
fun toCSV(vararg elements: Any): String = elements.joinToString(";") { it.toString() }
