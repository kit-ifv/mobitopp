package utils

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
