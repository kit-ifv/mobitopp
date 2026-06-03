package utils.binary

import java.io.DataOutputStream

fun interface RepresentativeBinaryWriter<in READONLY : Simplifiable<*>> : BinaryWriter<READONLY> {
    fun operateSimplifiedStream(outStream: DataOutputStream, elements: Collection<BinaryWritable>)
    override fun operateStream(outStream: DataOutputStream, elements: Collection<READONLY>) {
        operateSimplifiedStream(outStream, elements.map { it.simplify() })
    }
}