package domain.synthesis.behavior.householdgeneration

class CyclicIterator<T>(private val list: MutableList<T>) : Iterator<T> {
    private var index = 0

    override fun hasNext(): Boolean = list.isNotEmpty()

    override fun next(): T {
        if (list.isEmpty()) throw NoSuchElementException("List is empty")
        return list[index++ % list.size]
    }

    fun remove(target: T) = list.remove(target)
}
