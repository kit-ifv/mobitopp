package edu.kit.ifv.domain.synthesis.results.fastcsv
import de.siegmar.fastcsv.reader.CsvCallbackHandler
import de.siegmar.fastcsv.reader.CsvReader
import de.siegmar.fastcsv.writer.CsvWriter
import java.io.Reader
import java.io.Writer

/**
 * To convert the java builder pattern into a more kotlin style builder
 */
data class FastCsvConfig(var separator: Char = ';') {
    fun build(writer: Writer): CsvWriter = CsvWriter.builder().fieldSeparator(separator).build(writer)

    fun <T> buildReader(reader: Reader, lambda: () -> CsvCallbackHandler<T>): CsvReader<T> {
        val callback = lambda()
        return CsvReader.builder().fieldSeparator(separator).build(callback, reader)
    }

    companion object {
        val DEFAULT = FastCsvConfig()
    }
}
