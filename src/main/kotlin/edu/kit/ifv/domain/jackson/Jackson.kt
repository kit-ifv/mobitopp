package edu.kit.ifv.domain.jackson
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.csv.CsvGenerator
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

val module = SimpleModule().apply {
}

val standardMapper: ObjectMapper = CsvMapper().registerKotlinModule().registerModule(module)

val standardSchema = CsvSchema.emptySchema().withHeader()

/**
 * Automatic CSV parser based on the class fields of T using reflection.
 */
inline fun <reified T> standardCSVParse(
    input: InputStream,
    separator: Char = ';',
    charset: Charset = StandardCharsets.UTF_8,
): List<T> = InputStreamReader(
    input,
    charset,
).use { reader ->
    standardMapper
        .readerFor(T::class.java)
        .with(standardSchema.withColumnSeparator(separator)).readValues<T>(reader)
        .readAll()
}

inline fun <reified T : Any> writeCsv(path: Path, rows: List<T>) {
    val mapper = CsvMapper.builder()
        .addModule(KotlinModule.Builder().build())
        // optional: always quote strings, useful if you have commas/newlines
        .enable(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS)
        .build()

    val schema: CsvSchema = mapper
        .schemaFor(T::class.java)
        .withHeader()          // first row = column names
        .withColumnReordering(true) // keeps stable order (best effort)

    Files.newBufferedWriter(path).use { writer ->
        mapper.writer(schema).writeValue(writer, rows)
    }
}