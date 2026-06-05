package edu.kit.ifv.utils.binary
import edu.kit.ifv.utils.files.PathChecksum
import java.io.DataOutputStream
import java.nio.file.Path

/**
 * A functional interface for writing read-only objects to a binary file.
 *
 * The [BinaryWriter] interface is designed to write data from read-only objects to a binary file. The generic parameter
 * [READONLY] represents the type of objects that will be written, typically read-only types. This is because mutable
 * objects generally implement read-only interfaces, and you typically don’t need the full object to perform the binary
 * writing operation.
 *
 * The [toBinary] method manages the file output stream, buffering, and delegates the actual writing logic to
 * the [operateStream] function. This separation allows for customization of how data is written to the binary file.
 *
 * @param READONLY The type of objects to be written to the binary file. The convention is that this type is read-only,
 *                 but it could be any type based on your application needs.
 */
fun interface BinaryWriter<in READONLY> {
    /**
     * Writes a collection of [READONLY] objects to a binary file.
     *
     * This method handles the file stream setup, buffering, and delegating the writing of elements to [operateStream].
     * It opens the output file stream, wraps it in a buffered stream, and then passes the [java.io.DataOutputStream] to
     * [operateStream] to perform the actual data writing.
     *
     * @param path The path to the binary file where the data will be written.
     * @param elements The collection of read-only objects to be written to the binary file.
     */
    fun toBinary(path: Path, elements: Collection<READONLY>, checksum: PathChecksum = PathChecksum.Companion.INVALID) {
        path.bufferedDataOutputStream { outputStream ->
            header(outputStream, path, elements, checksum)
            operateStream(outputStream, elements)
        }
    }

    /**
     * Define what information should be written to the binary file before the elements are written. The default
     * implementation writes the checksum and the size of the elements
     */
    fun header(outStream: DataOutputStream, path: Path, elements: Collection<READONLY>, checksum: PathChecksum) {
        outStream.writeLong(checksum.value)
        outStream.writeInt(elements.size)
        outStream.writeInt(getMaxStringSize(elements))
    }

    fun getMaxStringSize(elements: Collection<READONLY>): Int = 0

    /**
     * Defines the custom logic for writing a collection of [READONLY] objects to a [DataOutputStream].
     *
     * This function is intended to be implemented by concrete classes to define how the individual objects are written
     * to the binary stream. The implementation should focus on writing the object data to the provided
     * [DataOutputStream].
     *
     * @param outStream The [DataOutputStream] to which the objects should be written.
     * @param elements The collection of read-only objects to be written.
     */
    fun operateStream(outStream: DataOutputStream, elements: Collection<READONLY>)
}
