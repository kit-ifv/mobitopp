package domain.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.InputStream


val module = SimpleModule().apply {

}

val standardMapper: ObjectMapper = CsvMapper().registerKotlinModule().registerModule(module)

val standardSchema = CsvSchema.emptySchema().withHeader()


inline fun <reified T> standardCSVParse(input: InputStream, separator: Char = ';'): List<T> {
    return standardMapper
        .readerFor(T::class.java)
        .with(standardSchema.withColumnSeparator(separator)).readValues<T>(input)
        .readAll()
}
