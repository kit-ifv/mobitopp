package utils.collections

/**
 * Creates a copy of a list by adding an element at the corresponding location
 */
fun <T> List<T>.add(index: Int, element: T): List<T> {
    return this.subList(0, index) + element + subList(index, size)
}

/**
 * returns the previous element or null in the list regarding a target index.
 */
fun <T> List<T>.previousOrNull(index: Int): T? {
    return getOrNull(index - 1)
}

/**
 * returns the previous element or null in the list regarding a target index.
 */
fun <T> List<T>.nextOrNull(index: Int): T? {
    return getOrNull(index + 1)
}

fun <T : Any> Iterable<T>.isSorted(comparator: Comparator<T>): Boolean {
    val target = zipWithNext { a: T, b: T -> comparator.compare(a, b) <= 0}
    return target.all { it }
}

fun <T: Comparable<T>> Iterable<T>.isSorted(): Boolean {
    return zipWithNext { a, b -> a <= b }.all{it}
}
fun <T : Any> Iterable<T>.isStrictlySorted(comparator: Comparator<T>): Boolean {
    val target = zipWithNext { a: T, b: T -> comparator.compare(a, b) == -1}
    return target.all { it }
}

fun <T: Comparable<T>> Iterable<T>.isStrictlySorted(): Boolean {
    return zipWithNext { a, b -> a < b }.all{it}
}