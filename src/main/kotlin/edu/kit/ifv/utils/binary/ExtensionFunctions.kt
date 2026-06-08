package edu.kit.ifv.utils.binary
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

fun <T> Path.operateOnMemoryFile(run: MappedByteBuffer.() -> T): T = RandomAccessFile(toFile(), "r").use { file ->
    val channel = file.channel
    val size = channel.size()
    val buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size)
    buffer.run(run)
}

/**
 * A dot character . is considered an empty entry
 */
val regex = Regex("\\.+$")

/**
 * Reads [stringLength] many characters from the DataInputStream and converts them to a string.
 * Padding of '.' at the end is removed.
 */
@Deprecated("Should be used with a bytebuffer instead.")
fun DataInputStream.readString(stringLength: Int): String {
    var output = ""
    repeat(stringLength) {
        output += readChar()
    }
    return output.replace(regex, "")
}

fun ByteBuffer.readString(stringLength: Int): String {
    var output = ""
    repeat(stringLength) {
        output += char
    }

    return output.replace(regex, "")
}

/**
 * Writes [stringLength] many characters of the string to the DataOutputStream. If length doesn't match, '.' padding is
 * added.
 */
fun DataOutputStream.writeString(element: String, stringLength: Int) {
    val scaledString = element.take(stringLength).padEnd(stringLength, '.')
    writeChars(scaledString)
}

/**
 * Creates and closes a DataOutputStream on the file at the path. While open, executes [write] on it.
 */
fun Path.bufferedDataOutputStream(write: (dataStream: DataOutputStream) -> Unit) {
    toFile().outputStream().use { fileStream ->
        BufferedOutputStream(fileStream).use { bufferedStream ->
            DataOutputStream(bufferedStream).use { outputStream ->
                outputStream.run(write)
            }
        }
    }
}

/**
 * Creates and closes a DataInputStream on the path. While open, executes [read] on it.
 */
fun <R> Path.bufferedDataInputStream(read: (dataStream: DataInputStream) -> R): R =
    toFile().inputStream().use { fileStream ->
        BufferedInputStream(fileStream).use { bufferedStream ->
            DataInputStream(bufferedStream).use { inputStream ->
                inputStream.run(read)
            }
        }
    }

/**
 * Parse directly into a bulk byte buffer for speedup.
 */
fun Path.readAsByteBuffer(): ByteBuffer {
    val channel = FileChannel.open(this, StandardOpenOption.READ)
    val bb = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
    return bb.order(ByteOrder.BIG_ENDIAN)
}
