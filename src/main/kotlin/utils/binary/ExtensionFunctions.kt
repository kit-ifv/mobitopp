package utils.binary

import domain.location.Location
import java.io.DataOutputStream
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path

fun <T> Path.operateOnMemoryFile(run: MappedByteBuffer.() -> T): T {
    return RandomAccessFile(toFile(), "r").use { file ->
        val channel = file.channel
        val size = channel.size()
        val buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size)
        buffer.run(run)
    }
}

/**
 * Extension function for `DataOutputStream` that writes a `Location` object to the output stream.
 * The method serializes the properties of the `Location` object (zone, coordinate, and road access)
 * into the output stream in a specific format:
 * - The `zone.id` is written as a `Long` (or `Long.MIN_VALUE` if `zone.id` is `null`).
 * - The latitude and longitude of the `coordinate` are written as `Double` values.
 * - The `roadAccess.roadId` is written as a `Long` (or `Long.MIN_VALUE` if `roadAccess.roadId` is `null`).
 * - The position of the `roadAccess` is written as a `Double` (with a default value of `0.5` if `roadAccess.position` is `null`).
 *
 * @param location The `Location` object to write to the `DataOutputStream`.
 */
fun DataOutputStream.writeLocation(location: Location) {
    LocationUtils.writeLocation(location, this)
}

/**
 * Extension function for `MappedByteBuffer` that retrieves a boolean value at a specified index.
 *
 * This function interprets the integer stored at the given `index` as a bit mask and extracts the boolean
 * value based on the specific bit position (the first byte of the integer). The integer is read from the buffer
 * and bitwise AND-ed with a mask (0x01000000) to determine whether the corresponding boolean flag is set.
 *
 * The reasoning behind using an integer and bitwise masking is that `MappedByteBuffer` does not have a convenient
 * way to directly read a boolean, and the boolean value is often stored as part of a larger integer structure.
 * The mask checks whether the first byte (0x01) in the integer block is set, which indicates a `true` value.
 * If it's not set, the boolean is considered `false`.
 *
 * **Note:** The mask (0x01000000) specifically checks the first byte in the 4-byte integer, which is assumed
 * to store the boolean flag (1 byte). This method is useful when working with binary data or file formats where
 * a boolean flag is embedded in an integer value.
 *
 * @param index The position (index) in the [MappedByteBuffer] where the integer representing the boolean is stored.
 * @return The boolean value extracted from the 4-byte integer at the specified index. Returns `true` if the first byte is set (0x01), otherwise `false`.
 */

@Suppress("MagicNumber")
fun MappedByteBuffer.getBoolean(index: Int): Boolean {
    val b = getInt(index)
    /* This magic number is the hexadecimal representation of an integer block in memory, such as 01 XX XX XX
    since the boolean flag is written as a byte of 01, the quickest way to check whether the integer at that location
    matches this mask 01-XX-XX-XX, because there is no convenient way to get a boolean from a bytebuffer, and no
    convenient way to apply mask operations on anything but an int.
     * */
    return (b and 0x01000000) != 0
}
