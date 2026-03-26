package domain.synthesis.results.fastcsv

import java.io.Writer
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

/**
 * Be advised, fields in reflection are returned alphabetically, and not in the order in which they appear in the class
 */
fun <S : Any, X> Collection<X>.writeCsvWithGenericAttributes(
    writer: Writer,
    config: FastCsvConfig = FastCsvConfig.DEFAULT,
    attributeExtractor: (X) -> S,
    headerPrefix: List<String>,
    outputPrefix: (X) -> List<String>,
) {
    if (isEmpty()) return

    val attributeClass = attributeExtractor(first())::class
    val attributeMemberProperties =
        attributeClass.memberProperties.filterNot { property -> property.annotations.any { it is CsvIgnore } }
            .map { it as KProperty1<S, *> }
    val writer = config.build(writer)

    writer.use { csv ->
        csv.writeRecord(headerPrefix + attributeMemberProperties.map { it.name })
        forEach { element ->
            csv.writeRecord(outputPrefix(element) + attributeMemberProperties.map {
                it.get(attributeExtractor(element)).toString()
            })
        }
    }
}

fun <X> Collection<X>.writeCsv(
    writer: Writer,
    config: FastCsvConfig = FastCsvConfig.DEFAULT,
    header: List<String>,
    output: (X) -> List<String>,
) {
    if (isEmpty()) return
    val writer = config.build(writer)
    writer.use { csv ->
        csv.writeRecord(header)
        forEach { element ->
            csv.writeRecord(output(element))
        }
    }
}
