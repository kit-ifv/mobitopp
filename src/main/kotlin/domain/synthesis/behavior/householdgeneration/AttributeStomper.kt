package domain.synthesis.behavior.householdgeneration

import kotlin.math.min
import kotlin.math.sign
import kotlin.random.Random


class CyclicIterator<T>(private val list: MutableList<T>) : Iterator<T> {
    private var index = 0

    override fun hasNext(): Boolean = list.isNotEmpty()

    override fun next(): T {
        if (list.isEmpty()) throw NoSuchElementException("List is empty")
        return list[index++ % list.size]
    }

    fun remove(target: T) = list.remove(target)
}

fun <T> Collection<T>.cyclicIterator(): CyclicIterator<T> {
    return CyclicIterator(this.toMutableList())
}
