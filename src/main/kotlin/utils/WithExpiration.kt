package utils

import utils.units.AbsoluteTime

/**
 * An expiring lookup takes a key, and returns the mapped value in addition to an expiration time.
 */
fun interface ExpiringLookup<K, V> {
    operator fun get(mode: K, time: AbsoluteTime): WithExpiration<V>
}
/**
 * Wraps around an element, tracking the expiration time.
 */
data class WithExpiration<T>(
    val element: T,
    val expiration: AbsoluteTime,
)

/**
 * Convenience function to add an expiration date to an object. 
 */
fun <T> T.withExpiration(expiration: AbsoluteTime): WithExpiration<T> {
    return WithExpiration(this, expiration)
}