package domain.synthesis.behavior.householdgeneration

fun <T> Collection<T>.cyclicIterator(): CyclicIterator<T> = CyclicIterator(this.toMutableList())
