package utils.collections

import java.util.function.IntFunction

interface ClearableList<T> : List<T> {
    fun clear()
}

@JvmInline
value class MutableClearableList<T>(val delegate: MutableList<T>) :
    MutableList<T> by delegate,
    ClearableList<T> {

    @Deprecated("This operation is ambiguous for an empty list.", level = DeprecationLevel.ERROR)
    override fun <T : Any?> toArray(generator: IntFunction<Array<out T?>?>): Array<out T?>? {
        error("")
    }
}

fun <T> mutableClearableListOf(vararg elements: T): MutableClearableList<T> =
    MutableClearableList(elements.toMutableList())
