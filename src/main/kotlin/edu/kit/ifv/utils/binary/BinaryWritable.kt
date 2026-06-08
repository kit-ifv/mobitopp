package edu.kit.ifv.utils.binary
import java.io.DataOutputStream

interface BinaryWritable {
    fun writeTo(outStream: DataOutputStream)
}
