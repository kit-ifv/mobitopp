package domain.synthesis

import domain.synthesis.results.CSVOutput
import domain.synthesis.results.toCSV

object IPUCSV : CSVOutput<IPUOutputLog> {
    override val header: List<String>
        get() = listOf("description", "expected", "actual", "absoluteDifference", "quotientDifference", "percentError")

    override fun convert(element: IPUOutputLog): String {
        return element.run {
            toCSV(
                description,
                expected,
                actual,
                difference,
                quotientDifference,
                percentDifference
            )


        }
    }
}