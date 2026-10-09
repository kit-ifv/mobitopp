package edu.kit.ifv.utils.files

import edu.kit.ifv.utils.PathChecksum
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.zip.CRC32
import kotlin.io.path.fileSize
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.inputStream
import kotlin.io.path.readBytes

@Suppress("MagicNumber")
fun Path.sampledCrc32(sampleSize: Int = 1 shl 20, chunks: Int = 4): PathChecksum {
    val crc = CRC32()
    val fileSize = this.fileSize()
    val channel = FileChannel.open(this, StandardOpenOption.READ)

    val step = fileSize / chunks
    repeat(chunks) { i ->
        val pos = i * step
        val bb = ByteBuffer.allocate(sampleSize)
        channel.read(bb, pos)
        crc.update(bb.array(), 0, bb.position())
    }
    channel.close()
    val lastModified = getLastModifiedTime().toMillis()
    val timeStampBuffer = ByteBuffer.allocate(Long.SIZE_BYTES).putLong(lastModified).array()
    crc.update(timeStampBuffer)
    return PathChecksum.from(crc.value)
}
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
fun Path.crc32() = sampledCrc32()
fun Path.crc32direct(): PathChecksum {
    val crc = CRC32()
    crc.update(this.readBytes())
    return PathChecksum.from(crc.value)
}
