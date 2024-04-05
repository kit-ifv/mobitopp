package datastructure

import java.util.*

/**
 * A container that holds two separate sorted sets of elements, where all elements in the left set are less than
 * the elements in the right set.
 *
 * @param T The type of elements contained in the sets, must be comparable.
 * @param L The type of elements in the left set, must be a subtype of T.
 * @param R The type of elements in the right set, must be a subtype of T.
 * @property left The sorted set containing elements of type [L].
 * @property right The sorted set containing elements of type [R].
 */
class DualSetContainer<T : Comparable<T>, L : T, R : T>(
    val left: NavigableSet<L> = TreeSet(),
    val right: NavigableSet<R> = TreeSet(),
) {

    init {
        require(isConsistent())
    }


    /**
     * Checks if both the left and right sets are empty.
     *
     * @return `true` if both sets are empty, `false` otherwise.
     */
    fun isEmpty() = left.isEmpty() && right.isEmpty()

    /**
     * Checks if all elements in the left set are smaller than the elements in the right set.
     *
     * @return `true` if the container is consistent, `false` otherwise.
     */
    fun isConsistent(): Boolean = left.all { first -> right.all { last -> first < last } }

    /**
     * Copies all entries from the target container to this container, ensuring consistency.
     *
     * @param other The container to fuse with.
     * @throws IllegalArgumentException if the fusion causes an inconsistent state.
     */
    fun fuse(other: DualSetContainer<T, L, R>) {
        left.addAll(other.left)
        right.addAll(other.right)

        require(isConsistent()) {
            "Merging $this with $other caused an inconsistent state"
        }
    }


    /**
     * Tries to add an element to the left sorted set. Returns whether the element was successfully inserted
     * or `false` if the element is already present in the set.
     *
     * @param element The element to add to the left set.
     * @return `true` if the element was successfully added, `false` if the element is already present.
     */
    fun addLeft(element: L) = left.add(element)

    /**
     * Tries to add an element to the right sorted set. Returns whether the element was successfully inserted
     * or `false` if the element is already present in the set.
     *
     * @param element The element to add to the right set.
     * @return `true` if the element was successfully added, `false` if the element is already present.
     */
    fun addRight(element: R) = right.add(element)

    /**
     * Determines whether a target element would fit in the container.
     *
     * Returns -1 if all elements of the container are smaller than the target element, +1 if all elements are
     * larger than the target element.
     * This method is typically used to determine the fitting container using binary search.
     *
     * Note: Although this method technically fulfills the Comparable interface requirements, it's not explicitly
     * declared in the class signature.
     *
     * @param other The target element whose fit in the container needs to be determined.
     * @return -1 if all elements are smaller than the target, +1 if all elements are larger, or 0 if the target
     * is within the bounds of the elements of this container.
     */
    fun accepts(other: T): Int {
        val completeSet = left + right
        return (completeSet.first().compareTo(other) + completeSet.last().compareTo(other)) / 2
    }

    /**
     * Determines whether a left type element should be accepted by this container. An element is accepted
     * if there are no smaller elements present in the right set.
     *
     * @param element The left type element to be checked for acceptance.
     * @return `true` if the element should be accepted, `false` otherwise.
     */
    fun acceptLeft(element: L) = right.firstOrNull()?.let { it > element } ?: true

    /**
     * Determines whether a right type element should be accepted by this container. An element is accepted
     * if there are no larger elements present in the left set.
     *
     * @param element The right type element to be checked for acceptance.
     * @return `true` if the element should be accepted, `false` otherwise.
     */
    fun acceptRight(element: R) = left.lastOrNull()?.let { it < element } ?: true


    /**
     * Removes the specified element from the left set.
     *
     * @param element The element to be removed from the left set.
     * @return `true` if the left set is empty after removal, `false` otherwise.
     */
    fun removeLeft(element: L): Boolean {
        left.remove(element)
        return left.isEmpty()
    }

    /**
     * Removes the specified element from the right set.
     *
     * @param element The element to be removed from the right set.
     * @return `true` if the right set is empty after removal, `false` otherwise.
     */
    fun removeRight(element: R): Boolean {
        right.remove(element)
        return right.isEmpty()
    }

    /**
     * Removes all elements from the left [L] collection larger than the given right [element]. Utilizes the fact that
     * the sets are sorted. All elements that are larger than [element] should be retained in this block. This function
     * is used for block construction
     *
     * @return the removed elements from the [left] block
     */
    fun cutOffLeft(element: R): NavigableSet<L> {
        val pivotElement = left.lastOrNull {
            it < element
        }
        val test = pivotElement?.let { TreeSet(left.headSet(it, true)) } ?: TreeSet()
        left.removeAll(test)
        return test
    }

    /**
     * Removes all elements from the right [R] collection larger than the given left [element]. Utilizes the fact that
     * the sets are sorted. All elements that are less than [element] should be retained in this block. This function
     * is used for block construction
     *
     * @return the removed elements from the [right] block
     */
    fun cutOffRight(element: L): NavigableSet<R> {
        // The first element in the second set that is larger than the target element
        val pivotElement = right.firstOrNull {
            it > element
        }
        /* Only retain elements from the second set that are smaller than the target, the rest should be moved to a new
         block */
        val test = pivotElement?.let { TreeSet(right.tailSet(it, true)) } ?: TreeSet()
        right.removeAll(test)
        return test
    }

    companion object {
        fun <T : Comparable<T>, L : T, R : T> singleLeftElement(element: L): DualSetContainer<T, L, R> {
            return DualSetContainer(left = TreeSet(setOf(element)))
        }

        fun <T : Comparable<T>, L : T, R : T> singleRightElement(element: R): DualSetContainer<T, L, R> {
            return DualSetContainer(right = TreeSet(setOf(element)))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DualSetContainer<*, *, *>) return false
        return left == other.left && right == other.right
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + right.hashCode()
        return result
    }

    override fun toString(): String {
        return ">$left $right<"
    }
}
