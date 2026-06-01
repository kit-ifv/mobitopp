package domain.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

val module = SimpleModule().apply {
}

val standardMapper: ObjectMapper = CsvMapper().registerKotlinModule().registerModule(module)

val standardSchema = CsvSchema.emptySchema().withHeader()

inline fun <reified T> standardCSVParse(
    input: InputStream,
    separator: Char = ';',
    charset: Charset = StandardCharsets.UTF_8,
): List<T> = InputStreamReader(input, charset).use { reader ->
    standardMapper
        .readerFor(T::class.java)
        .with(standardSchema.withColumnSeparator(separator)).readValues<T>(reader)
        .readAll()
}
