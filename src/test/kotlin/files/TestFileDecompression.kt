package files

import org.junit.jupiter.api.Test
import utils.files.decompressedBufferedReader
import java.io.BufferedReader
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.bufferedReader
import kotlin.test.assertEquals

private const val ROOT = "src/test/resources/compression"

class TestFileDecompression {

    private val original: Path = Path("$ROOT/decompress.txt")
    private val expectedFileContent: String = read(original.bufferedReader())

    private fun read(reader: BufferedReader): String = reader.lineSequence().joinToString("\n")

    @Test
    fun decompressBzip2PathString() {
        val decompressed = read("$ROOT/decompress.txt.bz2".decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressBzip2Path() {
        val decompressed = read(Path("$ROOT/decompress.txt.bz2").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressBzip2FastPath() {
        val decompressed = read(Path("$ROOT/decompress_fast.txt.bz2").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressBzip2UltraPath() {
        val decompressed = read(Path("$ROOT/decompress_ultra.txt.bz2").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressGzFile() {
        val decompressed = read(Path("$ROOT/decompress.txt.gz").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressXzFile() {
        val decompressed = read(Path("$ROOT/decompress.txt.xz").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressZipArchive() {
        val decompressed = read(Path("$ROOT/decompress.zip").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressTarArchive() {
        val decompressed = read(Path("$ROOT/decompress.tar").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }

    @Test
    fun decompressStack() {
        val decompressed = read(Path("$ROOT/decompress.zip.bz2.gz.tar.xz").decompressedBufferedReader())

        assertEquals(expectedFileContent, decompressed)
    }
}
