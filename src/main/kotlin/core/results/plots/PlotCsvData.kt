package core.results.plots

import core.modelsteps.resources.CsvResource
import utils.csv.CsvParser
import utils.csv.Row
import java.nio.file.Path

/**
 * Creates a [PlotDataBuilderWithEntities] that sources data from a CSV file.
 *
 * @param E The type of entities parsed from the CSV.
 * @param filePath The path to the CSV file.
 * @param parseScope A function to parse a [Row] into an entity of type [E].
 * @return A [PlotDataBuilderWithEntities] configured with the CSV data source.
 */
fun <E> forCsv(filePath: Path, parseScope: (Row) -> E?): PlotDataBuilderWithEntities<E> {
    val csvResource = CsvResource<E>(
        filePath,
        CsvParser(mapping = parseScope),
    )

    return PlotDataBuilderWithEntities { csvResource.elements.toList() }
}
