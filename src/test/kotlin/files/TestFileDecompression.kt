package files

import org.junit.jupiter.api.Test
import utils.files.decompressedBufferedReader
import java.io.BufferedReader
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals

private const val ROOT = "src/test/resources/compression"

class TestFileDecompression {

    private val original: File = File("$ROOT/decompress.txt")
    private val expectedFileContent: String = read(original.bufferedReader())

    private fun read(reader: BufferedReader): String = reader.lineSequence().joinToString("\n")

    @Test
    fun decompressBzip2PathString() {
        val decompressed = read("$ROOT/decompress.txt.bz2".decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressBzip2Path() {
        val decompressed = read(Path.of("$ROOT/decompress.txt.bz2").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressBzip2File() {
        val decompressed = read(File("$ROOT/decompress.txt.bz2").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressBzip2FastFile() {
        val decompressed = read(File("$ROOT/decompress_fast.txt.bz2").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressBzip2UltraFile() {
        val decompressed = read(File("$ROOT/decompress_ultra.txt.bz2").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressGzFile() {
        val decompressed = read(File("$ROOT/decompress.txt.gz").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressXzFile() {
        val decompressed = read(File("$ROOT/decompress.txt.xz").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressZipArchive() {
        val decompressed = read(File("$ROOT/decompress.zip").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressTarArchive() {
        val decompressed = read(File("$ROOT/decompress.tar").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressStack() {
        val decompressed = read(File("$ROOT/decompress.zip.bz2.gz.tar.xz").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }
}
