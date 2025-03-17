package usecases.steps.binary

import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * A functional interface for reading binary files.
 *
 * This interface defines a method to read data from a binary file and return it as a list of objects.
 * The generic parameter [MUTABLE] represents the type of objects that will be read from the binary file.
 * Although it is common for the type to be a mutable type (e.g., [MutableHousehold], [MutablePerson]),
 * the interface is flexible and can work with any type, depending on the specific application.
 *
 * @param MUTABLE The type of the objects to be read from the binary file. It is generally recommended to use a mutable type,
 *                but this is not a strict requirement, and any type can be used based on the needs of the application.
 */
fun interface BinaryReader<out MUTABLE> {
    /**
     * Reads data from a binary file at [path] and returns it as a list of objects of type [MUTABLE].
     */
    fun fromBinary(path: Path): List<MUTABLE>
}

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
     * It opens the output file stream, wraps it in a buffered stream, and then passes the [DataOutputStream] to
     * [operateStream] to perform the actual data writing.
     *
     * @param path The path to the binary file where the data will be written.
     * @param elements The collection of read-only objects to be written to the binary file.
     */
    fun toBinary(path: Path, elements: Collection<READONLY>) {
        Files.newOutputStream(path).use { fileStream ->
            BufferedOutputStream(fileStream).use { bufferedStream ->
                DataOutputStream(bufferedStream).use { outputStream ->
                    operateStream(outputStream, elements)
                }
            }
        }
    }

    /**
     * Defines the custom logic for writing a collection of [READONLY] objects to a [DataOutputStream].
     *
     * This function is intended to be implemented by concrete classes to define how the individual objects are written
     * to the binary stream. The implementation should focus on writing the object data to the provided [DataOutputStream].
     *
     * @param outStream The [DataOutputStream] to which the objects should be written.
     * @param elements The collection of read-only objects to be written.
     */
    fun operateStream(outStream: DataOutputStream, elements: Collection<READONLY>)
}

