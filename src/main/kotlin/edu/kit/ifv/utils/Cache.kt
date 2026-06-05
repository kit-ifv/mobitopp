package edu.kit.ifv.utils
@Deprecated("This is a really weird wrapper around map, do we need that?")
interface Cache<K, V> {
    val size: Int

    operator fun set(key: K, value: V)

    operator fun get(key: K): V?

    fun getOrPut(key: K, default: () -> V): V

    fun remove(key: K): V?

    fun clear()
}

@Deprecated("The only place where this is implemented is in the tests.")
class PerpetualCache<K, V> : Cache<K, V> {
    private val cache = HashMap<K, V>()

    override val size: Int
        get() = cache.size

    override fun remove(key: K): V? = cache.remove(key)

    override fun get(key: K): V? = cache[key]

    override fun getOrPut(key: K, default: () -> V): V = cache.getOrPut(key, default)

    override fun set(key: K, value: V) {
        this.cache[key] = value
    }

    override fun clear() = this.cache.clear()
}
