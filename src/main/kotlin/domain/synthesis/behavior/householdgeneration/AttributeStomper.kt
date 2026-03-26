package domain.synthesis.behavior.householdgeneration

fun <T> Collection<T>.cyclicIterator(): CyclicIterator<T> {
    return CyclicIterator(this.toMutableList())
}
