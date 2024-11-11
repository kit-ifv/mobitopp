package utils.collections

import kotlin.math.sign

/**
 * Picks the key from the map which corresponds to the representative value of the random number.
 */
fun <K> Map<K, Double>.select(random: Double): K {
    require(isNotEmpty()) {
        "Cannot pick a value from an empty map"
    }
    require(random in 0.0..1.0) {
        "Need a random value between 0 and 1"
    }
    val target = normalize().cumulativeSum()
    val ins = target.binarySearch { it.first.compareTo(random) }
    val choice = target[ins.toIndex()].second
    return choice
}

fun <K> Map<K, Double>.normalize(): Map<K, Double> {
    val sum = values.sum()
    val copy = this.toMutableMap()
    copy.forEach {
        copy[it.key] = it.value / sum
    }
    return copy
}

fun <K> Map<K, Double>.cumulativeSum(): List<Pair<Double, K>> {
    val cumSum = values.cumulativeSum()
    return cumSum.zip(keys)
}

fun <K, V> Map<K, V>.sortByValues(comparator: Comparator<V>): Map<K, V> {
    return this.toList()
        .sortedWith(compareBy(comparator) { it.second })
        .toMap()
}

/**
 * Converts the return value of [binarySearch] to the index position
 */
fun Int.toIndex(): Int {
    return if (sign == -1) {
        -this - 1
    } else {
        this
    }
}
