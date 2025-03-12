package utils.binary


import domain.data.Zone
import domain.data.ZoneId

import domain.location.Location
import domain.location.RoadAccess

import units.GPSCoordinate

import units.share

import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.nio.MappedByteBuffer
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
/**
 * A class that facilitates sequential reading of primitive data types and strings from a mapped byte buffer.
 * It keeps track of the current position in the buffer to prevent errors in manual index tracking.
 *
 * The main goal of this class is to provide an abstraction for reading data from a buffer while automatically
 * updating the position after each read operation, so that the user doesn't need to manually manage offsets.
 * This can help prevent counting errors that might occur if the index is miscalculated.
 *
 * The class provides methods for reading different types of data:
 * - `nextInt`: Reads a 4-byte integer from the current position in the buffer.
 * - `nextDouble`: Reads an 8-byte double from the current position in the buffer.
 * - `nextLong`: Reads an 8-byte long from the current position in the buffer.
 * - `nextBoolean`: Reads a 1-byte boolean from the current position in the buffer.
 * - `readString`: Reads a string of the specified length from the current position in the buffer.
 *
 * The position in the buffer is automatically updated after each read operation, and each read operation
 * takes care of the correct byte offsets for the respective data type. This ensures that subsequent read operations
 * work correctly without the need for manual offset management.
 *
 */
class TrackingBuffer(private val buffer: MappedByteBuffer, initialOffset: Int) {
    var currentPosition = initialOffset

    val nextInt get() = buffer.getInt(currentPosition).also { currentPosition += 4 }
    val nextDouble get() = buffer.getDouble(currentPosition).also { currentPosition += 8 }
    val nextLong get() = buffer.getLong(currentPosition).also { currentPosition += 8 }
    val nextBoolean get() = buffer.getBoolean(currentPosition).also { currentPosition += 1 }
    fun readString(length: Int): String {
        val charArray = CharArray(length)
        (0..<length).forEach {
            charArray[it] = buffer.getChar(currentPosition)
            currentPosition += 2
        }
        return String(charArray)
    }

    fun nextLocation(converter: (ZoneId) -> Zone?): Location {
        return LocationUtils.readLocation(this, converter)
    }
}

/**
 * Since writing and reading are heavily intertwined, they are encapsulated in this object, so that they will always
 * be at the same location, and if someone changes the logic, that they may see that they need to adapt the other
 * method as well. the write calls should be in the same order as the read calls. WriteLong -> WriteDouble -> etc.
 * should meet nextLong -> nextDouble -> etc.
 */
object LocationUtils {
    fun writeLocation(location: Location, outputStream: DataOutputStream) {
        outputStream.writeLong(location.zone?.id?.value ?: Long.MIN_VALUE)
        outputStream.writeDouble(location.coordinate.latitudeDegrees)
        outputStream.writeDouble(location.coordinate.longitudeDegrees)
        outputStream.writeLong(location.roadAccess?.roadId ?: Long.MIN_VALUE)
        outputStream.writeDouble(location.roadAccess?.position?.toDouble() ?: 0.5)
    }

    fun readLocation(buffer: TrackingBuffer, converter: (ZoneId) -> Zone?): Location {
        val zoneId = ZoneId(buffer.nextLong)  // Reading zone ID
        val coordinate = GPSCoordinate.decimalDegree(buffer.nextDouble, buffer.nextDouble)  // Reading latitude and longitude
        val roadAccess = RoadAccess(buffer.nextLong, buffer.nextDouble.share())  // Reading roadId and position
        return Location(coordinate, converter(zoneId), roadAccess)  // Returning a Location object
    }
}






