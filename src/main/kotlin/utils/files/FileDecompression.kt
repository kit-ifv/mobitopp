package utils.files

import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import utils.ErrorHandling
import utils.errorScope
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.Path

fun String.decompressedBufferedReader(charset: Charset = Charsets.UTF_8) =
    this.decompressedInputStream().bufferedReader(charset)

fun Path.decompressedBufferedReader(charset: Charset = Charsets.UTF_8) =
    this.decompressedInputStream().bufferedReader(charset)

fun File.decompressedBufferedReader(charset: Charset = Charsets.UTF_8) =
    this.decompressedInputStream().bufferedReader(charset)

fun String.decompressedInputStream() = File(this).decompressedInputStream()

fun Path.decompressedInputStream() = this.toFile().decompressedInputStream()

fun File.decompressedInputStream(): InputStream {
    var inputStream: InputStream = this.inputStream()
    var currentFileName = name

    while (true) {
        val extension = currentFileName.substringAfterLast(".")
        val fileExtension = Compression.fromExtension(extension)

        if (fileExtension != null) {
            errorScope(
                ErrorHandling.THROW,
                message = "Error while decompressing [$fileExtension] file $currentFileName; source: $path"
            ) {
                inputStream = fileExtension.decompress(inputStream)
            }

            currentFileName = currentFileName.substringBeforeLast(".")
        } else {
            break
        }
    }

    return inputStream
}

enum class Compression(val extension: String, val decompress: (InputStream) -> InputStream) {
    BZIP2("bz2", ::BZip2CompressorInputStream),
    GZIP("gz", ::GzipCompressorInputStream),
    XZ("xz", ::XZCompressorInputStream),

    @Deprecated("Not tested!")
    LZMA("lzma", ::LZMACompressorInputStream),

    @Deprecated("Not tested!")
    DEFLATE("deflate", ::DeflateCompressorInputStream),

    @Deprecated("Not tested!")
    LZ4("lz4", ::FramedLZ4CompressorInputStream),

    @Deprecated("Not tested!")
    SNAPPY("snappy", ::FramedSnappyCompressorInputStream),

    ZIP("zip", firstEntryOf(::ZipArchiveInputStream)),
    TAR("tar", firstEntryOf(::TarArchiveInputStream)),

    @Deprecated("Not tested!")
    JAR("jar", firstEntryOf(::JarArchiveInputStream)),

    @Deprecated("Not tested!")
    AR("ar", firstEntryOf(::ArArchiveInputStream)),

    @Deprecated("Not tested!")
    CPIO("cpio", firstEntryOf(::CpioArchiveInputStream)),

    @Deprecated("Not tested!")
    ARJ("arj", firstEntryOf(::ArjArchiveInputStream)),
    ;

    companion object {
        fun fromExtension(ext: String): Compression? {
            return entries.find { it.extension == ext }
        }
    }
}

private fun firstEntryOf(
    decompress: (InputStream) -> ArchiveInputStream<*>
): (InputStream) -> InputStream = { inputStream ->
    decompress(inputStream).checkEntry()
}

private fun ArchiveInputStream<*>.checkEntry(): InputStream { // TODO can this be done lazy?
    requireNotNull(this.nextEntry) {
        "Cannot decompress, at least one entry in zip archive is expected."
    }

    return this
}
