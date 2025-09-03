package utils.files

import java.nio.file.Path
import java.util.zip.CRC32
import kotlin.io.path.inputStream
import kotlin.io.path.readBytes

fun Path.bufferedCRC32(): Long {
    val crc = CRC32()
    inputStream().use { input ->
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            val read = input.read(buffer)
            if (read <= 0) break
            crc.update(buffer, 0, read)
        }
    }
    return crc.value
}

fun Path.crc32(): Long {
    val crc = CRC32()
    crc.update(this.readBytes())
    return crc.value
}
