package usecases.steps.binary

import domain.data.Zone
import domain.data.ZoneId
import domain.location.Location
import java.nio.MappedByteBuffer

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
@Suppress("MagicNumber")
class TrackingBuffer(private val buffer: MappedByteBuffer, initialOffset: Int) {
    var currentPosition = initialOffset

    val nextInt get() = buffer.getInt(currentPosition).also { currentPosition += 4 }
    val nextDouble get() = buffer.getDouble(currentPosition).also { currentPosition += 8 }
    val nextLong get() = buffer.getLong(currentPosition).also { currentPosition += 8 }
    val nextBoolean get() = buffer.getBoolean(currentPosition).also { currentPosition += 1 }
    fun readString(length: Int): String {
        val charArray = CharArray(length)
        for (i in 0..<length) {
            charArray[i] = buffer.getChar(currentPosition)
            currentPosition += 2
        }
        return String(charArray)
    }

    fun nextLocation(converter: (ZoneId) -> Zone?): Location {
        return LocationUtils.readLocation(this, converter)
    }
}