package utils.files

import java.nio.file.Path
import java.util.zip.CRC32
import kotlin.io.path.inputStream
import kotlin.io.path.readBytes

fun Path.bufferedCRC32(): PathChecksum {
    val crc = CRC32()
    inputStream().use { input ->
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            val read = input.read(buffer)
            if (read <= 0) break
            crc.update(buffer, 0, read)
        }
    }
    return PathChecksum.from(crc.value)
}

fun Path.crc32(): PathChecksum {
    val crc = CRC32()
    crc.update(this.readBytes())
    return PathChecksum.from(crc.value)
}

/**
 * Wrapper class to combine Checksum in combination with Invalid Element.
 */
@JvmInline
value class PathChecksum private constructor(val value: Long) {

    fun isValid() = value >= 0
    companion object {
        fun from(value: Long): PathChecksum {
            require(value >= 0) {
                "PathChecksum cannot be negative, because negative values are used to represent invalid hashes"
            }
            return PathChecksum(value)
        }

        val INVALID = PathChecksum(-1L)
    }

}