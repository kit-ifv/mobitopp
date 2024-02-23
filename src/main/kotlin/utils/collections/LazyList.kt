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
    private val dataStream: Iterator<E>
) : List<E> {

    private val elements: MutableList<E> = mutableListOf()

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
        get() = TODO("Not yet implemented")

    override fun containsAll(elements: Collection<E>): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(element: E): Boolean {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun iterator(): Iterator<E> {
        TODO("Not yet implemented")
    }

    override fun listIterator(): ListIterator<E> {
        TODO("Not yet implemented")
    }

    override fun listIterator(index: Int): ListIterator<E> {
        TODO("Not yet implemented")
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<E> {
        TODO("Not yet implemented")
    }

    override fun lastIndexOf(element: E): Int {
        TODO("Not yet implemented")
    }

    override fun indexOf(element: E): Int {
        TODO("Not yet implemented")
    }
}

/**
 * To lazy list
 *
 * @param I
 * @param E
 */
fun <I, E> I.toLazyList() where I : Iterator<E> = LazyList(this)

/**
 * To lazy list
 *
 * @param S
 * @param E
 */
fun <S, E> S.toLazyList() where S : Sequence<E> = LazyList(this.iterator())
