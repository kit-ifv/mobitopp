package utils.binary

import java.io.DataOutputStream

abstract class DefaultBinaryWriter<in READONLY : Simplifiable<*>> : RepresentativeBinaryWriter<READONLY> {
    override fun operateSimplifiedStream(outStream: DataOutputStream, elements: Collection<BinaryWritable>) {
        elements.forEach {
            it.writeTo(outStream)
        }
    }
}
