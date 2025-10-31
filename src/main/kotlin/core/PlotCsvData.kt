package core

import core.modelsteps.CsvResource
import core.results.plots.PlotDataBuilderWithEntities
import utils.csv.CsvParser
import utils.csv.Row
import java.nio.file.Path

fun <E> forCsv(filePath: Path, parseScope: (Row) -> E?): PlotDataBuilderWithEntities<E> {
    val csvResource = CsvResource<E>(
        filePath,
        CsvParser(mapping = parseScope),
    )

    return PlotDataBuilderWithEntities { csvResource.elements.toList() }
}
