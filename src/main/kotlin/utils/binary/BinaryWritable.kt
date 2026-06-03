package utils.binary

import java.io.DataOutputStream

interface BinaryWritable {
    fun writeTo(outStream: DataOutputStream)
}
