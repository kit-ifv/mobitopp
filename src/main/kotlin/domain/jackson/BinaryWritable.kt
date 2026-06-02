package domain.jackson

import java.io.DataOutputStream

interface BinaryWritable {
    fun writeTo(outStream: DataOutputStream)
}