package utils

data class WithMetric<T, M : Comparable<M>>(val item: T, val metric: M)

fun <T> Collection<WithMetric<T, *>>.discardMetric(): List<T> = map { it.item }
