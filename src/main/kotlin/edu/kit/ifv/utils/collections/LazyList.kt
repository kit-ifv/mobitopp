package edu.kit.ifv.utils.collections
/**
 * A [List] based on a (lazy) [Iterator] data stream. Values at given index
 * are only obtained from the data stream upon request.
 * It provides the full set of [List] operations.
 *  - [List.get] will read elements up to the given index
 *
 * @param E generic type of the contained elements
 * @property dataStream a lazy data stream to obtain list values
 */
class LazyList<E>(private val dataStream: Iterator<E>, expectedSize: Int = 10) : List<E> {
    private val elements: MutableList<E> = ArrayList(expectedSize)

    override operator fun get(index: Int) = when {
        index < elements.size -> elements[index]

        else -> {
            readValuesUntil(index)
            elements[index]
        }
    }

    override val size: Int
        get() = readRest().size

    override fun containsAll(elements: Collection<E>): Boolean = readRest().containsAll(elements)

    override fun contains(element: E): Boolean = readRest().contains(element)

    override fun isEmpty(): Boolean = readRest().isEmpty()

    override fun iterator(): Iterator<E> = readRest().iterator()

    override fun listIterator(): ListIterator<E> = readRest().listIterator()

    override fun listIterator(index: Int): ListIterator<E> = readRest().listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<E> = readRest().subList(fromIndex, toIndex)

    override fun lastIndexOf(element: E): Int = readRest().lastIndexOf(element)

    override fun indexOf(element: E): Int = readRest().indexOf(element)

    private fun readValuesUntil(index: Int) {
        while ((index >= elements.size) and (dataStream.hasNext())) {
            elements.add(dataStream.next())
        }
    }

    override fun toString(): String {
        readRest()
        return elements.toString()
    }

    private fun readRest(): List<E> {
        while (dataStream.hasNext()) {
            elements.add(dataStream.next())
        }
        return elements
    }
}

fun <I, E> I.toLazyList(expectedSize: Int = 10) where I : Iterator<E> = LazyList(this, expectedSize)

fun <S, E> S.toLazyList(expectedSize: Int = 10) where S : Sequence<E> = LazyList(this.iterator(), expectedSize)
