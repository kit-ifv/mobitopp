package utils

/**
 * An object is encodable if it can provide an integer based on the attributes present. In the future this could be
 * abstracted to a template type in case that the encoding might return a different type (such as Strings)
 */
fun interface Encodable {
    fun encode(): Int
}

/**
 * Similarly to an encoding a decoding interface allows to construct the underlying type by the provided integer code.
 * As the decoding logic can not be realistically dependent on an explicit object instantiation the logic must be bound
 * to a different object and cannot be added to a shared interface providing both encoding and decoding. This turns out
 * to be beneficial as both interfaces can be functional.
 *
 * This could also be abstracted to generic decoding types if the need arises
 */
interface Decodable<out T : Encodable> {
    fun decode(i: Int): T

    fun decode(s: String): T

    fun values(): Set<T>
}

typealias CodePlan<R> = Decodable<R>
