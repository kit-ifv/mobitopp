package edu.kit.ifv.utils.codes
import kotlin.reflect.KClass

abstract class EnumDecodable<T>(private val clazz: KClass<T>) : Decodable<T> where T : Encodable, T : Enum<T> {
    private val constants: Array<T> = clazz.java.enumConstants
        ?: error("No enum constants for ${clazz.simpleName}")
    private val byCode: Map<Int, T> = constants.associateBy { it.code }
    private val internalSet = constants.toSet()
    override fun values(): Set<T> = internalSet

    override fun decode(i: Int): T = byCode.getValue(i)
}
