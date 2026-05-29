package domain.synthesis.behavior.householdgeneration

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Path

class LenientIntDeserializer : JsonDeserializer<Int?>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Int? {
        val text = p.text.trim()
        if (text == "–") {
            return 0
        }
        // Treat empty, or other non-numeric as null
        if (text.isEmpty() || text.equals("N/A", ignoreCase = true)) {
            return null
        }
        return text.toIntOrNull()
    }
}
val module = SimpleModule().apply {
    addDeserializer(Int::class.java, LenientIntDeserializer())
    addDeserializer(Int::class.javaObjectType, LenientIntDeserializer()) // for nullable Int?
}
val standardMapper = CsvMapper().registerKotlinModule().registerModule(module)

val standardSchema = CsvSchema.emptySchema().withHeader().withColumnSeparator(';')

inline fun <reified T> standardParse(path: Path): List<T> = standardMapper
    .readerFor(T::class.java)
    .with(standardSchema).readValues<T>(path.toFile())
    .readAll()
