package edu.kit.ifv.utils

fun <T, K> Iterable<T>.groupByIgnoringNullKey(selector: (T) -> K?): Map<K, List<T>> {
    return this.mapNotNull { item ->
        selector(item)?.let { key -> key to item }
    }.groupBy({ it.first }, { it.second })
}