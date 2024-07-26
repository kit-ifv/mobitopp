package utils.collections

fun <M, K, V> M.append(key: K, value: V) where M : MutableMap<K, MutableList<V>> {
    if (key !in this) {
        this[key] = mutableListOf(value)
    } else {
        this[key]!!.add(value)
    }
}

fun Collection<Number>.cumulativeSum(): List<Double> {
    val result = mutableListOf<Double>()
    var sum = 0.0

    for (number in this) {
        sum += number.toDouble()
        result.add(sum)
    }

    return result
}
