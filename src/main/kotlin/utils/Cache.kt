package utils

interface Cache<K, V> {
    val size: Int

    operator fun set(key: K, value: V)

    operator fun get(key: K): V?

    fun getOrPut(key: K, default: () -> V): V

    fun remove(key: K): V?

    fun clear()
}

class PerpetualCache<K, V> : Cache<K, V> {
    private val cache = HashMap<K, V>()

    override val size: Int
        get() = cache.size

    override fun remove(key: K): V? {
        return cache.remove(key)
    }

    override fun get(key: K): V? {
        return cache[key]
    }

    override fun getOrPut(key: K, default: () -> V): V {
        return cache.getOrPut(key, default)
    }

    override fun set(key: K, value: V) {
        this.cache[key] = value
    }

    override fun clear() = this.cache.clear()
}
