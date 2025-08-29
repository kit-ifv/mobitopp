package utils

import utils.units.AbsoluteTime

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