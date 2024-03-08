package utils.collections

/**
 * A [List] based on a (lazy) [Iterator] data stream. Values at given index
 * are only obtained from the data stream upon request. However, it has a
 * reduced set of operations and only provides [List.get].
 *
 * @param E generic type of the contained elements
 * @property dataStream a lazy data stream to obtain list values
 */
class LazyList<E>(
    private val dataStream: Iterator<E>,
    expectedSize: Int = 10
) : List<E> {

    private val elements: MutableList<E> = ArrayList(expectedSize)

    override operator fun get(index: Int) = when {
        index < elements.size -> elements[index]
        else -> readValuesUntil(index).let { elements[index] }
    }

    private fun readValuesUntil(index: Int) {
        while ((index >= elements.size) and (dataStream.hasNext())) {
            elements.add(dataStream.next())
        }
    }

    override fun toString(): String {
        readRest()
        return elements.toString()
    }

    private fun readRest() {
        while (dataStream.hasNext()) {
            elements.add(dataStream.next())
        }
    }

    override val size: Int
        get() = throw UnsupportedOperationException()

    override fun containsAll(elements: Collection<E>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun contains(element: E): Boolean {
        throw UnsupportedOperationException()
    }

    override fun isEmpty(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun iterator(): Iterator<E> {
        throw UnsupportedOperationException()
    }

    override fun listIterator(): ListIterator<E> {
        throw UnsupportedOperationException()
    }

    override fun listIterator(index: Int): ListIterator<E> {
        throw UnsupportedOperationException()
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<E> {
        throw UnsupportedOperationException()
    }

    override fun lastIndexOf(element: E): Int {
        throw UnsupportedOperationException()
    }

    override fun indexOf(element: E): Int {
        throw UnsupportedOperationException()
    }
}


fun <I, E> I.toLazyList(expectedSize: Int = 10) where I : Iterator<E> = LazyList(this, expectedSize)

fun <S, E> S.toLazyList(expectedSize: Int = 10) where S : Sequence<E> = LazyList(this.iterator(), expectedSize)
