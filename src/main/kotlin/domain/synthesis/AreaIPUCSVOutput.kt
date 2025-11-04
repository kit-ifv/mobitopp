package domain.synthesis

import domain.synthesis.results.CSVOutput

object AreaIPUCSVOutput : CSVOutput<AreaIPUOutput<*>> {
    override val header: List<String> = listOf("zone") + IPUCSV.header
    override fun convert(element: AreaIPUOutput<*>): String {
        return "${element.zone};${IPUCSV.convert(element.original)}"
    }
}