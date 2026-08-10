package edu.kit.ifv.utils

/**
 * Separate a string at a target delimiter, exactly once.
 */
fun String.splitOnce(delimiter: String): Pair<String, String> {
    val index = this.indexOf(delimiter)
    return if (index == -1) {
        Pair(this, "")
    } else {
        Pair(this.substring(0, index), this.substring(index + delimiter.length))
    }
}
