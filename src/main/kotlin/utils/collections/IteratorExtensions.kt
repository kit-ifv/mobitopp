package utils.collections

/**
 * Almost identical behaviour to [takeWhile] only that the condition is inverted, and that the first element
 * that matches the predicate is returned as well.
 */

fun <T> Iterable<T>.takeUntil(predicate: (T) -> Boolean): List<T> {
    val list = ArrayList<T>()
    for (item in this) {
        list.add(item)
        if (predicate(item)) {
            break
        }
    }
    return list
}

/**
 * and here the condition trigger element is returned seperately, if it exists
 */
fun <T> Iterable<T>.takeUntilSpliced(predicate: (T) -> Boolean): Pair<List<T>, T?> {
    val list = ArrayList<T>()
    for (item in this) {
        if (predicate(item)) {
            return list to item
        }
        list.add(item)
    }
    return list to null
}

public inline fun <T> Iterable<T>.dropUntil(predicate: (T) -> Boolean): List<T> {
    var yielding = false
    val list = ArrayList<T>()
    for (item in this) {
        if (yielding) {
            list.add(item)
        } else if (!predicate(item)) {
            list.add(item)
            yielding = true
        }
    }
    return list
}
