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

fun <K, V> MutableMap<K, V>.replaceOrRemoveAll(mapping: (K, V) -> V?) {
    val iterator = this.entries.iterator()
    while (iterator.hasNext()) {
        val entry = iterator.next()
        val newValue = mapping(entry.key, entry.value)

        // Remove the entry if the new value is null, update otherwise
        if (newValue == null) {
            iterator.remove()
        } else {
            entry.setValue(newValue)
        }
    }
}
