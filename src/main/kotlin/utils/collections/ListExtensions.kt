@file:Suppress("TooManyFunctions")

package utils.collections

import java.util.*

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

fun <T> T?.iterate(followup: Iterable<T>): Iterable<T> {
    return if (this != null) {
        object : Iterable<T> {
            /**
             * Returns an iterator over the elements of this object.
             */
            override fun iterator(): Iterator<T> {
                @Suppress("IteratorNotThrowingNoSuchElementException") // The iterator will throw the exception
                return object : Iterator<T> {
                    var elementWasReturned = false
                    val originalIterator = followup.iterator()

                    /**
                     * Returns `true` if the iteration has more elements.
                     */
                    override fun hasNext(): Boolean {
                        return !elementWasReturned || originalIterator.hasNext()
                    }

                    /**
                     * Returns the next element in the iteration.
                     */
                    override fun next(): T {
                        return if (!elementWasReturned) {
                            elementWasReturned = true
                            this@iterate
                        } else {
                            originalIterator.next()
                        }
                    }
                }
            }
        }
    } else {
        followup
    }
}

fun <T : Comparable<T>> MutableList<T>.addByOrder(element: T): Boolean {
    val position = binarySearch { it.compareTo(element) }
    if (position < 0) {
        add((-position - 1), element)
        return true
    }
    return false
}

fun <T : Any> Iterable<T>.isSorted(comparator: Comparator<T>): Boolean {
    val target = zipWithNext { a, b -> comparator.compare(a, b) <= 0 }
    return target.all { it }
}

fun <T : Comparable<T>> Iterable<T>.isSorted(): Boolean {
    return zipWithNext { a, b -> a <= b }.all { it }
}

fun <T : Any> Iterable<T>.isStrictlySorted(comparator: Comparator<T>): Boolean {
    val target = zipWithNext { a: T, b: T -> comparator.compare(a, b) == -1 }
    return target.all { it }
}

fun <T : Comparable<T>> Iterable<T>.isStrictlySorted(): Boolean {
    return zipWithNext { a, b -> a < b }.all { it }
}

/**
 * Returns the element of an iterable if the size is exactly one, null otherwise
 */
fun <T> Iterable<T>.exactlyOneOrNull(predicate: (T) -> Boolean): T? {
    val target = filter(predicate)
    return if (target.size == 1) target.first() else null
}

fun <T> List<T>.cumulativeSum(plusOperator: (T, T) -> T): List<T> {
    if (isEmpty()) return emptyList()
    var first = first()
    return listOf(first) + drop(1).map {
        first = plusOperator(first, it)
        first
    }
}

/**
 * Filter a list by the position of the indicies using a bit set.
 */
fun <T> List<T>.filterBy(bitSet: BitSet): List<T> {
    require(bitSet.size() >= size)
    return withIndex().filter {
        bitSet[it.index]
    }.map { it.value }
}
