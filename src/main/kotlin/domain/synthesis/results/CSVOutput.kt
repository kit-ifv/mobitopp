package domain.synthesis.results

import java.nio.file.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createDirectories

interface CSVOutput<T> {
    // TODO move to utils, maybe use composition of column converters over interface implementation,
    //  buffered writing might be required if string gets large!

    val header: List<String>
    fun convert(element: T): String
    fun generateCSVString(elements: Collection<T>) =
        header.joinToString(separator = ";", postfix = "\n") { it } + elements.joinToString(separator = "\n") {
            convert(
                it,
            )
        }

    fun writeCSVToFile(path: Path, elements: Collection<T>) {
        path.parent.createDirectories() // Ensure that the necessary parent directories exist.
        path.bufferedWriter().use { writer ->
            writer.write(header.joinToString(separator = ";") { it })
            writer.newLine()
            elements.forEach { element ->
                writer.write(convert(element))
                writer.newLine()
            }
        }
    }
}
