package edu.kit.ifv.utils.binary
import edu.kit.ifv.utils.files.PathChecksum
import java.nio.ByteBuffer
import java.nio.file.Path

/**
 * A functional interface for reading binary files.
 *
 * This interface defines a method to read data from a binary file and return it as a list of objects.
 * The generic parameter [MUTABLE] represents the type of objects that will be read from the binary file.
 * Although it is common for the type to be a mutable type (e.g., [domain.synthesis.data.MutableHousehold], [MutablePerson]),
 * the interface is flexible and can work with any type, depending on the specific application.
 *
 * @param MUTABLE The type of the objects to be read from the binary file. It is generally recommended to use a mutable
 * type, but this is not a strict requirement, and any type can be used based on the needs of the application.
 */
fun interface BinaryReader<out MUTABLE> {
    /**
     * Reads data from a binary file at [path] and returns it as a list of objects of type [MUTABLE].
     */
    fun fromBinary(path: Path): List<MUTABLE> {
        val byteBuffer = path.readAsByteBuffer()
        byteBuffer.long // Consume hash code at pos 0 - then ignore it
        val size = byteBuffer.int
        val stringLength = byteBuffer.int

        val elements = ArrayList<MUTABLE>(size)
        repeat(size) {
            byteBuffer.decode(stringLength)?.let {
                elements.add(it)
            }
        }
        elements.trimToSize()
        return elements
    }

    fun ByteBuffer.getBoolean(): Boolean = get().toInt() != 0

    /**
     * Read the first entry to represent what file the binary entry comes from.
     */
    fun checksum(path: Path): PathChecksum = path.bufferedDataInputStream { PathChecksum.Companion.from(it.readLong()) }

    /**
     * Reads one [MUTABLE] object from the [java.io.DataInputStream] and returns an instance of that object.
     * The [java.io.DataInputStream] is at the exact location of a new object. All parameters of the object to be created
     * lie sequentially on the [java.io.DataInputStream].
     * The exact order of the parameters is given by a matching [BinaryWriter] or [CSVBinaryConverter], depending on how
     * the binary file was created.
     * @param stringLength is the expected size of strings, if strings are read. Each string should have the same length
     * specified at writing the element.
     */
    fun ByteBuffer.decode(stringLength: Int): MUTABLE?
}
