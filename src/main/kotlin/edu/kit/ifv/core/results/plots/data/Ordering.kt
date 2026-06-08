package edu.kit.ifv.core.results.plots.data
/**
 * Ordering strategy for arranging elements.
 *
 * @param T the generic type of the elements to be arranged
 */
sealed class Ordering<T> {

    /**
     * Arbitrary ordering without any specific criteria.
     *
     * @param X the generic type of the elements
     */
    class Arbitrary<X> : Ordering<X>() {

        override fun <S> arrangeBy(elements: Collection<S>, by: (S) -> X): List<S> = elements.toList()
    }

    /**
     * Ascending ordering for comparable elements.
     *
     * @param T the generic type of the elements
     */
    class Ascending<T> : Ordering<T>() where T : Comparable<T> {

        override fun <S> arrangeBy(elements: Collection<S>, by: (S) -> T): List<S> = elements.sortedBy(by)
    }

    /**
     * Descending ordering for comparable elements.
     *
     * @param T the generic type of the elements
     */
    class Descending<T> : Ordering<T>() where T : Comparable<T> {

        override fun <S> arrangeBy(elements: Collection<S>, by: (S) -> T): List<S> = elements.sortedByDescending(by)
    }

    /**
     * Ascending ordering by a specified key.
     *
     * @param T the generic type of the elements to be ordered
     * @param R the generic type of the key used for ordering
     * @property key a function that extracts the key from an element of type T
     */
    class AscendingBy<T, R>(private val key: (T) -> R) : Ordering<T>() where R : Comparable<R> {

        override fun <S> arrangeBy(elements: Collection<S>, by: (S) -> T): List<S> = elements.sortedBy { s ->
            key(
                by(s),
            )
        }
    }

    /**
     * Descending ordering by a specified key.
     *
     * @param T the generic type of the elements to be ordered
     * @param R the generic type of the key used for ordering
     * @property key a function that extracts the key from an element of type T
     */
    class DescendingBy<T, R>(private val key: (T) -> R) : Ordering<T>() where R : Comparable<R> {

        override fun <S> arrangeBy(elements: Collection<S>, by: (S) -> T): List<S> = elements.sortedByDescending { s ->
            key(
                by(s),
            )
        }
    }

    /**
     * Arrange other elements based on this ordering strategy
     * based on the transformation of other elements to this strategies type.
     *
     * @param S the generic type of the other elements
     * @param elements the other elements to be arranged
     * @param by the function to extract the value by which the elements are arranged
     * @return the arranged list of other elements
     */
    abstract fun <S> arrangeBy(elements: Collection<S>, by: (S) -> T): List<S>

    /**
     * Arrange elements based on this ordering strategy.
     *
     * @param elements the elements to be arranged
     * @return the arranged list of elements
     */
    fun arrange(elements: Collection<T>): List<T> = arrangeBy(elements) { it }
}
