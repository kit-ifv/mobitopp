package utils

import kotlin.reflect.KClass

/**
 * An object is encodable if it can provide an integer based on the attributes present. In the future this could be
 * abstracted to a template type in case that the encoding might return a different type (such as Strings)
 */
interface Encodable {
    val code: Int
    val description: String
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

    fun decode(i: Int): T = requireNotNull(
        decodeOrNull(i),
    ) {
        errorMessage(i)
    }
    fun decodeOrNull(i: Int): T? = values().find { it.code == i }
    fun decode(s: String): T = requireNotNull(
        decodeOrNull(s),
    ) {
        errorMessage(s)
    }
    fun decodeOrNull(s: String) = values().find { it.description == s }
    fun errorMessage(value: Any): String =
        "The given code '$value' is not a valid ${this::class.simpleName} encoding!\n" +
            "Available encodings:\n" +
            values().joinToString("\n") { type ->
                "  ${type.code} = ${type.description}"
            }

    fun values(): Set<T>
}

typealias CodePlan<R> = Decodable<R>

abstract class EnumDecodable<T>(private val clazz: KClass<T>) : Decodable<T> where T : Encodable, T : Enum<T> {
    private val constants: Array<T> = clazz.java.enumConstants
        ?: error("No enum constants for ${clazz.simpleName}")
    private val byCode: Map<Int, T> = constants.associateBy { it.code }
    private val internalSet = constants.toSet()
    override fun values(): Set<T> = internalSet

    override fun decode(i: Int): T = byCode.getValue(i)
}
