package utils.collections

import kotlin.random.Random

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



fun <T> Collection<T>.pickWithReplacement(
    amount: Int,
    random: Random = Random(1)
): List<T> {
    val inputList = toList()
    return List(amount) { inputList[random.nextInt(inputList.size)] }
}

/**
 * Select an exact amount from a list, if the list is not sufficiently long enough, it will be artificially filled by
 * repeating the elements. each list or list repetition element is shuffled. In order to return a random list of exactly
 * [amount] elements, the shuffled list will be truncated to the length.
 */
fun <T> Collection<T>.selectExact(amount: Int, random: Random = Random(1)): List<T> {
    val repeatedList = repeatExact(amount)
    return repeatedList.shuffled(random)
}

/**
 * Same as [selectExact] only that no shuffle is performed
 */
fun <T> Collection<T>.repeatExact(amount: Int): List<T> {
    if (amount == 0) return emptyList()
    require(isNotEmpty()) {
        "Cannot select an exact amount of elements from an empty collection"
    }
    val inputList = toList()
    return List(amount) { inputList[it % size] }
}

