package utils.collections

class EquivalenceClass<T>(
    val equivalence: (T, T) -> Boolean = { a, b ->
        a == b
    },
    val map: MutableMap<T, MutableSet<T>> = mutableMapOf(),
) : Map<T, Set<T>> by map {

    /**
     * Convenience function to abbreviate comparisons on the equivalence relation allowing to write a ~ b instead of
     * equivalence(a, b). Though since `~` is not really readable the function got renamed to eqv instead.
     *
     * @param other the other element to compare against
     * @return true if the elements are equal under [equivalence], false otherwise.
     */
    private infix fun T.eqv(other: T): Boolean = equivalence(this, other)

    fun add(element: T) {
        /* Determine whether the target equivalence class exists by comparing against the first element in the sets of
        existing values. By mathematical definition of equivalence matching any element is sufficient to locate the
        equivalence class. */

        val existingEntry = map.entries.firstOrNull {
            it.value.size >= 1 && it.value.first() eqv element
        }
        /* Either insert the element into the corresponding equivalence class if one is found, or add a new one. As all
        other classes did not match it has to be a new equivalence class.  */
        existingEntry?.value?.add(element) ?: map.put(element, mutableSetOf(element))
    }

    /**
     * Access to the equivalence class can be either provided by the element being the representative (key) or matching
     * to a key via the equivalence relation.
     *
     * @param key the element to access the group
     * @return the equivalence class of the element, should one exist.
     */
    override operator fun get(key: T): Set<T>? = map[key] ?: map.entries.firstOrNull { it.key eqv key }?.value

    /**
     * Creates a new map where the key set is changed by the [converter] function.
     *
     * @param R
     * @param converter
     * @receiver
     * @return
     */
    fun <R> toRepresentative(converter: (T) -> R): Map<R, Set<T>> = map.mapKeys {
        converter(it.key)
    }

    /*Technically not entirely correct as the map of two equivalence groups could still be equal even when the equivalence
      is not the same, however as it is a function and very often an anonymous one I have no idea how to incorporate it
      into this equality */
    override fun equals(other: Any?): Boolean = if (other !is EquivalenceClass<*>) {
        false
    } else {
        map == other.map
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()
}

@Suppress("FunctionNameMaxLength")
fun <R, T> Set<T>.equivalenceClassByRepresentative(converter: (T) -> R): Map<R, Set<T>> {
    val groups = EquivalenceClass(equivalence = { a: T, b: T ->
        converter(a) == converter(b)
    })
    forEach {
        groups.add(it)
    }

    return groups.toRepresentative(converter)
}

fun <T> Set<T>.equivalenceClasses(equivalence: (T, T) -> Boolean): EquivalenceClass<T> {
    val groups = EquivalenceClass(equivalence = equivalence)
    forEach {
        groups.add(it)
    }

    return groups
}
