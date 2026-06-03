package utils.numbers

import kotlin.math.round

/**
 * If the binary search reveals anything, then return the index number, but if not, then the preceding index is - 2 of
 * the result. Since the standard behavior of binary search returns the insertion point, the converted index of
 * [indexOfSearch] would return the higher element index, as this would be the normal insertion point in a list.
 *
 * To get the smaller index, we need to return the smaller number when the element is not present in the array.
 * Binary search indicates such a case with a negative sign. In the case that we found the element we return the index
 * as usual
 */
fun Int.smallerIndex(): Int = if (this < 0) -this - 2 else this

fun Int.indexOfSearch(): Int = if (this < 0) -this - 1 else this

private const val ORDER_OF_MAGNITUDE = 10.0

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= ORDER_OF_MAGNITUDE }
    return round(this * multiplier) / multiplier
}

@Deprecated("Is this used anywhere?")
fun Collection<Double>.scaleToInts(amount: Int): List<Int> {
    val scaled = map { it * amount }
    val floored = scaled.map { it.toInt() }.toMutableList()
    val leftOver = amount - floored.sum()

    val remainders = scaled.mapIndexed { i, v -> i to (v - floored[i]) }

    remainders.sortedByDescending { it.second }
        .take(leftOver)
        .forEach { (i, _) -> floored[i]++ }

    return floored
}
