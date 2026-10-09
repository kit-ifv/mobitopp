package edu.kit.ifv.utils.binary

import edu.kit.ifv.binary.writeString
import java.io.DataOutputStream

/**
 * Functional interface for writing a string encoded element onto a [DataOutputStream].
 */
fun interface WriteStrategy {
    fun writeToStream(dataStream: DataOutputStream, element: String, stringLength: Int)
}

/**
 * Default WriteStrategies for basic datatypes.
 */
@Suppress("ConstructorParameterNaming")
data class DataType(
    val INT: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, _: Int ->
        dataStream.writeInt(element.toInt())
    },
    val LONG: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, _: Int ->
        dataStream.writeLong(element.toLong())
    },
    val BOOLEAN: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, _: Int ->
        dataStream.writeBoolean(element.toBoolean())
    },
    val STRING: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, stringLength: Int ->
        dataStream.writeString(element, stringLength)
    },
    val FLOAT: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, _: Int ->
        dataStream.writeFloat(element.toFloat())
    },
    val DOUBLE: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, _: Int ->
        dataStream.writeDouble(element.toDouble())
    },
    val CHAR: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, _: Int ->
        dataStream.writeChar(element.toCharArray().first().code)
    },
    val SHORT: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, _: Int ->
        dataStream.writeShort(element.toInt())
    },
    val BYTE: WriteStrategy = WriteStrategy { dataStream: DataOutputStream, element: String, _: Int ->
        dataStream.writeByte(element.toInt())
    },
)
