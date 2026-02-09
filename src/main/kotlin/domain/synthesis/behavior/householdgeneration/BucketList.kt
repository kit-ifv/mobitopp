package domain.synthesis.behavior.householdgeneration

import kotlin.random.Random

class BucketList<T>(
    private val maxGain: Int
) {
    private val positionTracker: MutableMap<T, Int> = mutableMapOf()
    private var bestBucketIndex: Int = -1
    private val buckets: Array<Bucket<T>> = Array(2 * maxGain + 1) {
        mutableSetOf()
    }

    private fun offsetGain(gain: Int): Int {
        return gain + maxGain
    }

    fun insert(element: T, gain: Int) {
        val idx = offsetGain(gain)
        if (idx !in buckets.indices) {
            throw IndexOutOfBoundsException("No Bucket with that index")
        }
        buckets[idx].add(element)
        positionTracker[element] = idx
        if (idx > bestBucketIndex) bestBucketIndex = idx
    }

    fun remove(element: T) {
        positionTracker[element]?.let {
            val targetBucket = buckets[it]
            targetBucket.remove(element)
            positionTracker.remove(element)

            if (it == bestBucketIndex && targetBucket.isEmpty()) {
                updateBestBucketIndex()
            }
        }
    }

    @Deprecated("This method is slow and should only be used for debugging")
    operator fun contains(element: T): Boolean {
        return buckets.any { element in it }
    }
    private fun updateBestBucketIndex() {
        var idx = bestBucketIndex
        while (idx >= 0 && buckets[idx].isEmpty()) {
            idx--
        }
        bestBucketIndex = idx // Will set idx -1 if the entire datastructure is empty.
    }

    fun update(element: T, newGain: Int) {
        remove(element)
        insert(element, newGain)
    }
    fun pollBest(): T {
        return buckets[bestBucketIndex].first()
    }

    fun randomBest(random: Random): T {
        return buckets[bestBucketIndex].random(random)
    }

    fun isEmpty(): Boolean = bestBucketIndex <= -1

    fun popBest(): Pair<T, Int>? {
        if (isEmpty()) return null
        val element = buckets[bestBucketIndex].first()
        val gain = currentGain()
        remove(element)
        return element to gain
    }

    fun currentGain() = bestBucketIndex - maxGain

    fun validateBuckets(predicate: (Set<T>, Int) -> Boolean): Boolean {
        return buckets.withIndex().all { predicate(it.value, it.index - maxGain) }
    }

    fun <X> operateOnBuckets(predicate: (Set<T>, Int) -> Collection<X>): Set<X> {
        return buckets.withIndex().flatMap { predicate(it.value, it.index - maxGain) }.toSet()
    }

    fun elements() = buckets.flatMap { it }

    fun validateElements(predicate: (T) -> Boolean): Boolean {
        return elements().all(predicate)
    }
    fun isBest(element: T): Boolean {
        return element in buckets[bestBucketIndex]
    }
    fun clear() {
        buckets.forEach { it.clear() }
    }
}
