package datastructure

import utils.collections.add
import utils.collections.nextOrNull
import utils.collections.previousOrNull
import java.util.*
import kotlin.properties.Delegates

fun interface EntryListener<T : Comparable<T>, F : T, S : T> {

    fun update(entries: List<DualSetContainer<T, F, S>>)
}

class DualSetList<T : Comparable<T>, L : T, R : T> {
    val persistentSet = TreeSet<T>()
    var entries: List<DualSetContainer<T, L, R>> by Delegates.observable(emptyList()) { _, _, new ->
        listeners.forEach {
            it.update(new)
        }
    }
    val listeners: MutableList<EntryListener<T, L, R>> = mutableListOf()
    override fun toString(): String {
        return entries.toString()
    }

    fun isConsistent(): Boolean {
        return entries.all { it.isConsistent() } && entries.none { it.isEmpty() } &&
            persistentSet == entries.flatMap { it.left + it.right }.toSet()
    }

    private fun position(entry: T): Int {
        val test = entries.binarySearch {
            it.accepts(entry)
        }
        return test
    }

    fun removeLeft(t: L): Boolean {
        val index = getRemovalIndex(t)
        if (index < 0) return false
        val block = entries[index]
        val isSemiEmpty = block.removeLeft(t)
        if (isSemiEmpty) {
            fuse(block, entries.previousOrNull(index))
        }

        return true
    }

    fun removeRight(t: R): Boolean {
        val index = getRemovalIndex(t)
        if (index < 0) return false
        val block = entries[index]
        val isSemiEmpty = block.removeRight(t)
        if (isSemiEmpty) {
            fuse(block, entries.nextOrNull(index))
        }

        return true
    }

    private fun DualSetContainer<T, L, R>.remove(index: Int, lambda: DualSetContainer<T, L, R>.(i: Int) -> Boolean) {
        val isSemiEmpty = lambda(index)
        if (isSemiEmpty) {
            fuse(this,)
        }
    }

    private fun getRemovalIndex(t: T): Int {
        if (!persistentSet.contains(t)) return -1
        persistentSet.remove(t)
        return position(t)
    }

    private fun fuse(
        dualSetContainer: DualSetContainer<T, L, R>,
        targetDualSetContainer: DualSetContainer<T, L, R>?
    ) {
        val hasFused = targetDualSetContainer?.let {
            it.fuse(dualSetContainer)
            true
        } ?: false
        if (hasFused || dualSetContainer.isEmpty()) {
            entries = entries - dualSetContainer
        }
    }

    fun addLeft(t: L): Boolean {
        if (persistentSet.contains(t)) return false
        persistentSet.add(t)
        if (entries.isEmpty()) {
            entries = entries.add(0, DualSetContainer.singleLeftElement(t))
            return true
        }
        val (index, correspondingBlock) = correspondingBlock(t, false)
        if (correspondingBlock.acceptLeft(t)) return correspondingBlock.addLeft(t)
        val largerElements = correspondingBlock.cutOffRight(t)

        entries = entries.add(index + 1, DualSetContainer(left = sortedSetOf(t), right = largerElements))
        return true
    }

    fun addRight(t: R): Boolean {
        if (persistentSet.contains(t)) return false
        persistentSet.add(t)
        if (entries.isEmpty()) {
            entries = entries.add(0, DualSetContainer.singleRightElement(t))
            return true
        }
        val (index, correspondingBlock) = correspondingBlock(t, true)
        if (correspondingBlock.acceptRight(t)) return correspondingBlock.addRight(t)

        val smallerElements = correspondingBlock.cutOffLeft(t)
        entries = entries.add(index, DualSetContainer(left = smallerElements, right = sortedSetOf(t)))
        return true
    }

    private fun correspondingBlock(
        element: T,
        selectSucceedingBlock: Boolean
    ): IndexedValue<DualSetContainer<T, L, R>> {
        val index = position(element)
        val offset = if (selectSucceedingBlock) -2 else -1
        var correspondingIndex = if (index >= 0) index else (-index + offset)
        correspondingIndex = correspondingIndex.coerceIn(entries.indices)

        val correspondingBlock = entries[correspondingIndex]
        return IndexedValue(correspondingIndex, correspondingBlock)
    }
}
