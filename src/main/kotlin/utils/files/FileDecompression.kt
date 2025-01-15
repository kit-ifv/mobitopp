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
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.Path

private val ISO_8859_1 = charset("ISO-8859-1")
private val UTF_8 = Charsets.UTF_8
private val DEFAULT_CHARSET = ISO_8859_1

/**
 * Create a buffered reader with (potential) file decompression assuming this String to be a file path.
 *
 * @param charset the charset to be used to read the file content
 * @return a [BufferedReader] of the given [Path]'s content, decompression all [Compression] file extensions.
 */
fun String.decompressedBufferedReader(charset: Charset = DEFAULT_CHARSET) =
    this.decompressedInputStream().bufferedReader(charset)

/**
 * Create a buffered reader with (potential) file decompression for the receiver [Path].
 *
 * @param charset the charset to be used to read the file content
 * @return a [BufferedReader] of the given [Path]'s content, decompression all [Compression] file extensions.
 */
fun Path.decompressedBufferedReader(charset: Charset = DEFAULT_CHARSET) =
    this.decompressedInputStream().bufferedReader(charset)

/**
 * Create a buffered reader with (potential) file decompression for the receiver [File].
 *
 * @param charset the charset to be used to read the file content
 * @return a [BufferedReader] of the given [File]'s content, decompression all [Compression] file extensions.
 */
fun File.decompressedBufferedReader(charset: Charset = DEFAULT_CHARSET): BufferedReader =
    this.decompressedInputStream().bufferedReader(charset)

/**
 * Create an input stream with (potential) file decompression assuming this String to be a file path.
 */
fun String.decompressedInputStream() = File(this).decompressedInputStream()

/**
 * Create an input stream with (potential) file decompression for the receiver [Path].
 *
 * @return an [InputStream] of the given [Path]'s content, decompression all [Compression] file extensions.
 */
fun Path.decompressedInputStream() = this.toFile().decompressedInputStream()

/**
 * Create an input stream with (potential) file decompression for the receiver [Path].
 * Iteratively applies decompression algorithms if the file extension match one of the [Compression] file extensions.
 * Decompression starts with the outermost file extension (right)
 * and moves left until a non-matching file extension is found.
 *
 * @return an [InputStream] of the given [File]'s content, decompression all [Compression] file extensions.
 */
fun File.decompressedInputStream(): InputStream {
    var inputStream: InputStream = BufferedInputStream(this.inputStream())
    var currentFileName = name

    if (compressionExtension(currentFileName) == null) {
        return inputStream
    }

    while (true) {
        val fileExtension = compressionExtension(currentFileName)

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

    // This decompresses all compression layers into one buffer
    val array = errorScope(
        ErrorHandling.THROW,
        message = "Error while executing decompression of file content: $name",
    ) {
        val out = ByteArrayOutputStream()
        inputStream.copyTo(out)
        inputStream.close()
        out.toByteArray()
    }

    return ByteArrayInputStream(array)
}

private fun compressionExtension(currentFileName: String): Compression? {
    val extension = currentFileName.substringAfterLast(".", missingDelimiterValue = "")
    return Compression.fromExtension(extension)
}

/**
 * Compression file extensions and the corresponding [decompress]ion algorithms.
 *
 * @property extension the string of the file extension.
 * @property decompress the decompression algorithm used to decompress files of the represented extension
 */
enum class Compression(val extension: String, val decompress: (InputStream) -> InputStream) {
    /**
     * Bzip2 compression file extension.
     */
    BZIP2("bz2", ::BZip2CompressorInputStream),

    /**
     * Gzip compression file extension.
     */
    GZIP("gz", ::GzipCompressorInputStream),

    /**
     * Xz compression file extension.
     */
    XZ("xz", ::XZCompressorInputStream),

    /**
     * Lzma compression file extension.
     */
    @Deprecated("Not tested!")
    LZMA("lzma", ::LZMACompressorInputStream),

    /**
     * Deflate compression file extension.
     */
    @Deprecated("Not tested!")
    DEFLATE("deflate", ::DeflateCompressorInputStream),

    /**
     * Lz4 compression file extension.
     */
    @Deprecated("Not tested!")
    LZ4("lz4", ::FramedLZ4CompressorInputStream),

    /**
     * Snappy compression file extension.
     */
    @Deprecated("Not tested!")
    SNAPPY("snappy", ::FramedSnappyCompressorInputStream),

    /**
     * Zip archive compression file extension.
     * Decompression looks for the first entry in the archive and decompresses it.
     * Other entries in the archive are ignored.
     */
    ZIP("zip", firstEntryOf(::ZipArchiveInputStream)),

    /**
     * Tar archive compression file extension.
     * Decompression looks for the first entry in the archive and decompresses it.
     * Other entries in the archive are ignored.
     */
    TAR("tar", firstEntryOf(::TarArchiveInputStream)),

    /**
     * Jar archive compression file extension.
     * Decompression looks for the first entry in the archive and decompresses it.
     * Other entries in the archive are ignored.
     */
    @Deprecated("Not tested!")
    JAR("jar", firstEntryOf(::JarArchiveInputStream)),

    /**
     * Ar archive compression file extension.
     * Decompression looks for the first entry in the archive and decompresses it.
     * Other entries in the archive are ignored.
     */
    @Deprecated("Not tested!")
    AR("ar", firstEntryOf(::ArArchiveInputStream)),

    /**
     * Cpio archive compression file extension.
     * Decompression looks for the first entry in the archive and decompresses it.
     * Other entries in the archive are ignored.
     */
    @Deprecated("Not tested!")
    CPIO("cpio", firstEntryOf(::CpioArchiveInputStream)),

    /**
     * Arj archive compression file extension.
     * Decompression looks for the first entry in the archive and decompresses it.
     * Other entries in the archive are ignored.
     */
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
    val decompress1 = decompress(inputStream)

    requireNotNull(decompress1.nextEntry) {
        "Cannot decompress '$decompress1', at least one entry in archive is expected."
    }
    decompress1
}
