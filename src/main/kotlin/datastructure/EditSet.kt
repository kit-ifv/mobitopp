package datastructure
//
//
//import java.util.*
//
//
//
//
//
//
//class ObservableSet(private val original: NavigableSet<Action>,
//                       val listeners: MutableList<NavigableSet<in Action>> = mutableListOf()
//): NavigableSet<Action> {
//    /**
//     * Adds the specified element to the set.
//     *
//     * @return `true` if the element has been added, `false` if the element is already contained in the set.
//     */
//    override fun add(element: Action): Boolean {
//        val inserted = original.add(element)
//        if(inserted) listeners.forEach { it.add(element) }
//        return inserted
//    }
//
//    override fun addAll(elements: Collection<Action>): Boolean {
//        val inserted = original.addAll(elements)
//        if(inserted) listeners.forEach { it.addAll(elements) }
//        return inserted
//    }
//    fun add(leg: Leg) {
//        original.add(leg)
//        listeners.forEach { it.add(leg) }
//    }
//    override fun clear() {
//        original.clear()
//        listeners.forEach { it.clear() }
//    }
//
//    /**
//     * Returns an iterator over the elements in this set, in ascending order.
//     *
//     * @return an iterator over the elements in this set, in ascending order
//     */
//    override fun iterator(): MutableIterator<Action> {
//        //TODO this iterator probably won't reflect changes in views
//        return original.iterator()
//    }
//
//    override fun isEmpty(): Boolean {
//        return original.isEmpty()
//    }
//
//    /**
//     * Returns the comparator used to order the elements in this set,
//     * or `null` if this set uses the [ natural ordering][Comparable] of its elements.
//     *
//     * @return the comparator used to order the elements in this set,
//     * or `null` if this set uses the natural ordering
//     * of its elements
//     */
//    override fun comparator(): Comparator<in Action>? {
//        return original.comparator()
//    }
//
//    /**
//     * Returns the first (lowest) element currently in this set.
//     *
//     * @return the first (lowest) element currently in this set
//     * @throws NoSuchElementException if this set is empty
//     */
//    override fun first(): Action {
//        return original.first()
//    }
//
//    /**
//     * Returns the last (highest) element currently in this set.
//     *
//     * @return the last (highest) element currently in this set
//     * @throws NoSuchElementException if this set is empty
//     */
//    override fun last(): Action {
//        return original.last()
//    }
//
//    /**
//     * Retrieves and removes the first (lowest) element,
//     * or returns `null` if this set is empty.
//     *
//     * @return the first element, or `null` if this set is empty
//     */
//    override fun pollFirst(): Action? {
//        val poll = original.pollFirst()
//        poll?.let{listeners.forEach { l -> l.remove(it) }}
//        return poll
//    }
//
//    /**
//     * Retrieves and removes the last (highest) element,
//     * or returns `null` if this set is empty.
//     *
//     * @return the last element, or `null` if this set is empty
//     */
//    override fun pollLast(): Action? {
//        val poll = original.pollLast()
//        poll?.let{listeners.forEach { l -> l.remove(it) }}
//        return poll
//    }
//
//    /**
//     * Returns a reverse order view of the elements contained in this set.
//     * The descending set is backed by this set, so changes to the set are
//     * reflected in the descending set, and vice-versa.  If either set is
//     * modified while an iteration over either set is in progress (except
//     * through the iterator's own `remove` operation), the results of
//     * the iteration are undefined.
//     *
//     *
//     * The returned set has an ordering equivalent to
//     * [Collections.reverseOrder]`(comparator())`.
//     * The expression `s.descendingSet().descendingSet()` returns a
//     * view of `s` essentially equivalent to `s`.
//     *
//     * @return a reverse order view of this set
//     */
//    override fun descendingSet(): NavigableSet<Action> {
//        //TODO this might inflict pain and suffering
//        return ObservableSet(original.descendingSet(), listeners.map{it.descendingSet()}.toMutableList())
//    }
//
//    /**
//     * Returns an iterator over the elements in this set, in descending order.
//     * Equivalent in effect to `descendingSet().iterator()`.
//     *
//     * @return an iterator over the elements in this set, in descending order
//     */
//    override fun descendingIterator(): MutableIterator<Action> {
//        // TODO this may cause pain and suffering
//        return original.descendingIterator()
//    }
//
//    override val size: Int
//        get() = original.size
//
//    /**
//     * Returns the least element in this set strictly greater than the
//     * given element, or `null` if there is no such element.
//     *
//     * @param e the value to match
//     * @return the least element greater than `e`,
//     * or `null` if there is no such element
//     * @throws ClassCastException if the specified element cannot be
//     * compared with the elements currently in the set
//     * @throws NullPointerException if the specified element is null
//     * and this set does not permit null elements
//     */
//    override fun higher(e: Action): Action? {
//        return original.higher(e)
//    }
//
//    /**
//     * Returns the least element in this set greater than or equal to
//     * the given element, or `null` if there is no such element.
//     *
//     * @param e the value to match
//     * @return the least element greater than or equal to `e`,
//     * or `null` if there is no such element
//     * @throws ClassCastException if the specified element cannot be
//     * compared with the elements currently in the set
//     * @throws NullPointerException if the specified element is null
//     * and this set does not permit null elements
//     */
//    override fun ceiling(e: Action): Action? {
//        return original.ceiling(e)
//    }
//
//    /**
//     * Returns the greatest element in this set less than or equal to
//     * the given element, or `null` if there is no such element.
//     *
//     * @param e the value to match
//     * @return the greatest element less than or equal to `e`,
//     * or `null` if there is no such element
//     * @throws ClassCastException if the specified element cannot be
//     * compared with the elements currently in the set
//     * @throws NullPointerException if the specified element is null
//     * and this set does not permit null elements
//     */
//    override fun floor(e: Action): Action? {
//        return original.floor(e)
//    }
//
//    /**
//     * Returns the greatest element in this set strictly less than the
//     * given element, or `null` if there is no such element.
//     *
//     * @param e the value to match
//     * @return the greatest element less than `e`,
//     * or `null` if there is no such element
//     * @throws ClassCastException if the specified element cannot be
//     * compared with the elements currently in the set
//     * @throws NullPointerException if the specified element is null
//     * and this set does not permit null elements
//     */
//    override fun lower(e: Action): Action? {
//        return original.lower(e)
//    }
//
//    /**
//     * {@inheritDoc}
//     *
//     *
//     * Equivalent to `tailSet(fromElement, true)`.
//     *
//     * @throws ClassCastException       {@inheritDoc}
//     * @throws NullPointerException     {@inheritDoc}
//     * @throws IllegalArgumentException {@inheritDoc}
//     */
//    override fun tailSet(fromElement: Action): SortedSet<Action> {
//        return ObservableSet(original.tailSet(fromElement, true), listeners)
//    }
//
//    /**
//     * Returns a view of the portion of this set whose elements are greater
//     * than (or equal to, if `inclusive` is true) `fromElement`.
//     * The returned set is backed by this set, so changes in the returned set
//     * are reflected in this set, and vice-versa.  The returned set supports
//     * all optional set operations that this set supports.
//     *
//     *
//     * The returned set will throw an `IllegalArgumentException`
//     * on an attempt to insert an element outside its range.
//     *
//     * @param fromElement low endpoint of the returned set
//     * @param inclusive `true` if the low endpoint
//     * is to be included in the returned view
//     * @return a view of the portion of this set whose elements are greater
//     * than or equal to `fromElement`
//     * @throws ClassCastException if `fromElement` is not compatible
//     * with this set's comparator (or, if the set has no comparator,
//     * if `fromElement` does not implement [Comparable]).
//     * Implementations may, but are not required to, throw this
//     * exception if `fromElement` cannot be compared to elements
//     * currently in the set.
//     * @throws NullPointerException if `fromElement` is null
//     * and this set does not permit null elements
//     * @throws IllegalArgumentException if this set itself has a
//     * restricted range, and `fromElement` lies outside the
//     * bounds of the range
//     */
//    override fun tailSet(fromElement: Action, inclusive: Boolean): NavigableSet<Action> {
//        return ObservableSet(original.tailSet(fromElement, inclusive), listeners)
//    }
//
//    /**
//     * {@inheritDoc}
//     *
//     *
//     * Equivalent to `headSet(toElement, false)`.
//     *
//     * @throws ClassCastException       {@inheritDoc}
//     * @throws NullPointerException     {@inheritDoc}
//     * @throws IllegalArgumentException {@inheritDoc}
//     */
//    override fun headSet(toElement: Action): SortedSet<Action> {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Returns a view of the portion of this set whose elements are less than
//     * (or equal to, if `inclusive` is true) `toElement`.  The
//     * returned set is backed by this set, so changes in the returned set are
//     * reflected in this set, and vice-versa.  The returned set supports all
//     * optional set operations that this set supports.
//     *
//     *
//     * The returned set will throw an `IllegalArgumentException`
//     * on an attempt to insert an element outside its range.
//     *
//     * @param toElement high endpoint of the returned set
//     * @param inclusive `true` if the high endpoint
//     * is to be included in the returned view
//     * @return a view of the portion of this set whose elements are less than
//     * (or equal to, if `inclusive` is true) `toElement`
//     * @throws ClassCastException if `toElement` is not compatible
//     * with this set's comparator (or, if the set has no comparator,
//     * if `toElement` does not implement [Comparable]).
//     * Implementations may, but are not required to, throw this
//     * exception if `toElement` cannot be compared to elements
//     * currently in the set.
//     * @throws NullPointerException if `toElement` is null and
//     * this set does not permit null elements
//     * @throws IllegalArgumentException if this set itself has a
//     * restricted range, and `toElement` lies outside the
//     * bounds of the range
//     */
//    override fun headSet(toElement: Action, inclusive: Boolean): NavigableSet<Action> {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * {@inheritDoc}
//     *
//     *
//     * Equivalent to `subSet(fromElement, true, toElement, false)`.
//     *
//     * @throws ClassCastException       {@inheritDoc}
//     * @throws NullPointerException     {@inheritDoc}
//     * @throws IllegalArgumentException {@inheritDoc}
//     */
//    override fun subSet(fromElement: Action, toElement: Action): SortedSet<Action> {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Returns a view of the portion of this set whose elements range from
//     * `fromElement` to `toElement`.  If `fromElement` and
//     * `toElement` are equal, the returned set is empty unless `fromInclusive` and `toInclusive` are both true.  The returned set
//     * is backed by this set, so changes in the returned set are reflected in
//     * this set, and vice-versa.  The returned set supports all optional set
//     * operations that this set supports.
//     *
//     *
//     * The returned set will throw an `IllegalArgumentException`
//     * on an attempt to insert an element outside its range.
//     *
//     * @param fromElement low endpoint of the returned set
//     * @param fromInclusive `true` if the low endpoint
//     * is to be included in the returned view
//     * @param toElement high endpoint of the returned set
//     * @param toInclusive `true` if the high endpoint
//     * is to be included in the returned view
//     * @return a view of the portion of this set whose elements range from
//     * `fromElement`, inclusive, to `toElement`, exclusive
//     * @throws ClassCastException if `fromElement` and
//     * `toElement` cannot be compared to one another using this
//     * set's comparator (or, if the set has no comparator, using
//     * natural ordering).  Implementations may, but are not required
//     * to, throw this exception if `fromElement` or
//     * `toElement` cannot be compared to elements currently in
//     * the set.
//     * @throws NullPointerException if `fromElement` or
//     * `toElement` is null and this set does
//     * not permit null elements
//     * @throws IllegalArgumentException if `fromElement` is
//     * greater than `toElement`; or if this set itself
//     * has a restricted range, and `fromElement` or
//     * `toElement` lies outside the bounds of the range.
//     */
//    override fun subSet(fromElement: Action, fromInclusive: Boolean, toElement: Action, toInclusive: Boolean): NavigableSet<Action> {
//        TODO("Not yet implemented")
//    }
//
//    override fun containsAll(elements: Collection<Action>): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun contains(element: Action): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun retainAll(elements: Collection<Action>): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun removeAll(elements: Collection<Action>): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun remove(element: Action): Boolean {
//        TODO("Not yet implemented")
//    }
//
//}
//
//
//class EditSetImpl(elements: Collection<Action>) : WrappedSet<Action>() {
//    constructor(action: Action): this(setOf(action))
//
//
////    override val wrapped: NavigableSet<Action>
//
//    override val init: (NavigableSet<Action>) -> WrappedSet<Action>
//        get() = { EditSetImpl(it) }
//
//    override val wrapped: NavigableSet<Action> = sortedSetOf()
//    val initial: ActivityBlock = ActivityBlock(sortedSetOf(), wrapped)
//    val iterator: Iterable<ActivityBlock> = initial.blockIterator()
//    val genericIterator: Iterable<ActionBlock<*>> = initial.blockIterator()
////    val legIterator get() = initial.next.blockIterator()
//
//    init {
//        elements.forEach { add(it) }
//    }
//
//
//    fun add(activity: Activity): Boolean {
//        if (wrapped.contains(activity)) return false
//        val test = tempIterator().first { !it.rejects(activity) }
//
//        test.insert(activity)
//        return true
//    }
//
//    fun add(leg: Leg): Boolean {
//        if (wrapped.contains(leg)) return false
//        val test = tempIterator().first { !it.rejects(leg) }
//        test.insert(leg)
//        return true
//    }
//
//    private fun tempIterator(): Iterable<ActionBlock<*>> {
//        return Iterable {
//            object : Iterator<ActionBlock<*>> {
//                var current: ActionBlock<*>? = null
//                var next: ActionBlock<*>? = initial
//
//                /**
//                 * Returns `true` if the iteration has more elements.
//                 */
//                override fun hasNext(): Boolean {
//                    return current?.let { next != null } ?: true
//
//                }
//
//                /**
//                 * Returns the next element in the iteration.
//                 */
//                override fun next(): ActionBlock<*> {
//                    current = next
//                    next = current?.next
//                    return current ?: throw NoSuchElementException()
//                }
//
//            }
//        }
//
//    }
//
//    private fun legBlockIterator(): Iterable<LegBlock> {
//        return Iterable {
//            object : Iterator<LegBlock> {
//                var current: LegBlock? = null
//                var nextinternal: LegBlock? = initial.next
//
//                /**
//                 * Returns `true` if the iteration has more elements.
//                 */
//                override fun hasNext(): Boolean {
//                    return nextinternal != null
//
//                }
//
//                /**
//                 * Returns the next element in the iteration.
//                 */
//                override fun next(): LegBlock {
//                    current = nextinternal
//                    nextinternal = current?.next?.next
//                    return current ?: throw NoSuchElementException()
//                }
//
//            }
//        }
//    }
//
//    private fun activityBlockIterator(): Iterable<ActivityBlock> {
//        return Iterable {
//            object : Iterator<ActivityBlock> {
//                var current: ActivityBlock? = null
//                var nextinternal: ActivityBlock? = initial
//
//                /**
//                 * Returns `true` if the iteration has more elements.
//                 */
//                override fun hasNext(): Boolean {
//                    return nextinternal != null
//
//                }
//
//                /**
//                 * Returns the next element in the iteration.
//                 */
//                override fun next(): ActivityBlock {
//                    current = nextinternal
//                    nextinternal = current?.next?.next
//                    return current ?: throw NoSuchElementException()
//                }
//
//            }
//        }
//    }
//    /**
//     * Adds the specified element to the set.
//     *
//     * @return `true` if the element has been added, `false` if the element is already contained in the set.
//     */
//    override fun add(element: Action): Boolean {
//        return when(element) {
//            is Activity -> add(element)
//            is Leg -> add(element)
//        }
//    }
//
//    fun remove(leg: Leg): Boolean {
//        val target = legBlockIterator().first { it.contains(leg) }
//        return target.remove(leg)
//    }
//
//
//    fun remove(activity: Activity): Boolean {
//        val target = activityBlockIterator().first { it.contains(activity) }
//        return target.remove(activity)
//    }
//
//    override fun remove(element: Action): Boolean {
//        return when(element) {
//            is Activity -> remove(element)
//            is Leg -> remove(element)
//        }
//    }
//
//    override fun addAll(elements: Collection<Action>): Boolean {
//        return elements.map { add(it) }.all { it }
//    }
//
//
//    override fun clear() {
//        tempIterator().forEach { it.clear() }
//    }
//
//    /**
//     * Returns an iterator over the elements in this set, in ascending order.
//     *
//     * @return an iterator over the elements in this set, in ascending order
//     */
//    override fun iterator(): MutableIterator<Action> {
//        return object : MutableIterator<Action> {
//            val originalIterator = wrapped.iterator()
//            var element: Action? = null
//            /**
//             * Returns `true` if the iteration has more elements.
//             */
//            override fun hasNext(): Boolean {
//                return originalIterator.hasNext()
//            }
//
//            /**
//             * Returns the next element in the iteration.
//             */
//            override fun next(): Action {
//                element = originalIterator.next()
//                return element?: throw NoSuchElementException()
//            }
//
//            /**
//             * Removes from the underlying collection the last element returned by this iterator.
//             */
//            override fun remove() {
//                this@EditSetImpl.remove(element)
//            }
//
//        }
//    }
//
//    override fun removeAll(elements: Collection<Action>): Boolean {
//        return elements.map { remove(it) }.all{it}
//    }
//
//    override fun retainAll(elements: Collection<Action>): Boolean {
//        val difference = wrapped - elements.toSet()
//        return removeAll(difference)
//
//    }
//
//
//
//
//    /**
//     * Retrieves and removes the first (lowest) element,
//     * or returns `null` if this set is empty.
//     *
//     * @return the first element, or `null` if this set is empty
//     */
//    override fun pollFirst(): Action? {
//        val target = wrapped.pollFirst()
//        target?.let { remove(it) }
//        return target
//    }
//
//    /**
//     * Retrieves and removes the last (highest) element,
//     * or returns `null` if this set is empty.
//     *
//     * @return the last element, or `null` if this set is empty
//     */
//    override fun pollLast(): Action? {
//        val target = wrapped.pollLast()
//        target?.let { remove(it) }
//        return target
//    }
//
//
//    /**
//     * Returns an iterator over the elements in this set, in descending order.
//     * Equivalent in effect to `descendingSet().iterator()`.
//     *
//     * @return an iterator over the elements in this set, in descending order
//     */
//    override fun descendingIterator(): MutableIterator<Action> {
//        return descendingSet().iterator()
//    }
//
//    override fun contains(element: Action?): Boolean {
//        return wrapped.contains(element)
//    }
//
//    override fun containsAll(elements: Collection<Action>): Boolean {
//        return wrapped.containsAll(elements)
//    }
//
//    override fun isEmpty(): Boolean {
//        return wrapped.isEmpty()
//    }
//    override val size: Int
//        get() = wrapped.size
//
//
//    override fun first(): Action {
//        return wrapped.first()
//    }
//
//    override fun last(): Action {
//        return wrapped.last()
//    }
//
//    override fun lower(e: Action?): Action? {
//        return wrapped.lower(e)
//    }
//
//    override fun floor(e: Action?): Action? {
//        return wrapped.floor(e)
//    }
//
//
//    override fun ceiling(e: Action?): Action? {
//        return wrapped.ceiling(e)
//    }
//
//    override fun higher(e: Action?): Action? {
//        return wrapped.higher(e)
//    }
//    override fun comparator(): Comparator<in Action>? {
//        return wrapped.comparator()
//    }
//}
//
///**
// * Wrapped Set prevents escape from the type using the subset method calls. Essentially a Wrapped Set stays a wrapped
// * set. This is needed when the internal set logic (add, remove) is altered and should remain the same logic even when
// * called upon instantiations of subsets.
// */
//abstract class WrappedSet<T>: NavigableSet<T> {
//    protected abstract val wrapped: NavigableSet<T>
//    protected abstract val init: (NavigableSet<T>) -> WrappedSet<T>
//    override fun headSet(toElement: T): WrappedSet<T> {
//        return init(headSet(toElement, false))
//    }
//
//    override fun headSet(toElement: T, inclusive: Boolean): WrappedSet<T> {
//        return init(wrapped.headSet(toElement, inclusive))
//    }
//
//    override fun subSet(fromElement: T, fromInclusive: Boolean, toElement: T, toInclusive: Boolean): WrappedSet<T> {
//        return init(wrapped.subSet(fromElement, fromInclusive, toElement, toInclusive))
//    }
//    /**
//     * {@inheritDoc}
//     *
//     *
//     * Equivalent to `subSet(fromElement, true, toElement, false)`.
//     *
//     * @throws ClassCastException       {@inheritDoc}
//     * @throws NullPointerException     {@inheritDoc}
//     * @throws IllegalArgumentException {@inheritDoc}
//     */
//
//    override fun subSet(fromElement: T, toElement: T): WrappedSet<T> {
//        return subSet(fromElement, true, toElement, false)
//    }
//
//    override fun tailSet(fromElement: T, inclusive: Boolean): WrappedSet<T> {
//        return init(wrapped.tailSet(fromElement, inclusive))
//    }
//
///**
// * {@inheritDoc}
// *
// *
// * Equivalent to `tailSet(fromElement, true)`.
// * **/
//
//    override fun tailSet(fromElement: T): WrappedSet<T> {
//        return tailSet(fromElement, true)
//    }
//
//    override fun descendingSet(): WrappedSet<T> {
//        return init(wrapped.descendingSet())
//    }
//}