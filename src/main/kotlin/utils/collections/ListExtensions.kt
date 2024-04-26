package utils.collections


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

/**
 * Returns the element of an iterable if the size is exactly one, null otherwise
 */
fun <T> Iterable<T>.exactlyOneOrNull(predicate: (T)-> Boolean): T? {
    val target = filter(predicate)
    return if(target.size == 1) target.first() else null
}