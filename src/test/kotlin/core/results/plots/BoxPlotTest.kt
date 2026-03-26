package core.results.plots

import core.results.plots.data.Summary
import org.junit.jupiter.api.Tag
import kotlin.io.path.Path
import kotlin.test.Test

val testPlotResultDir = Path("results/test")
@Tag("plot")
class BoxPlotTest {
    //
    private fun plotterBuilder() =
        forData { data }
            .groupBy { it.first }
            .summarize { it.second }
            .over { it.first }

    @Test
    fun testBoxPlot() {
        plotterBuilder().asBoxPlot {
            name = "BoxPlot normal"
            coloring = { randomColor() }
        }.plot(testPlotResultDir)
    }

    @Test
    fun testComparisonBoxPlot() {
        val comparisonData = rawData.map { (name, values) ->
            val length = values.size
            name to Summary(
                values[0],
                values[length / 4],
                values[length / 2],
                values[length / 4 * 3],
                values[length - 1]
            )
        }

        plotterBuilder().compareTo {
            forData {
                comparisonData
            }.groupBy {
                "comparison"
            }.plot {
                it.second
            }.over {
                it.first
            }
        }.asBoxPlot {
            name = "BoxPlot comparison"
            coloring = { randomColor() }
        }.plot(testPlotResultDir)
    }

    // generate data that can be used for a sample box plot
    private fun generateData(amount: Int): List<Double> {
        val data = mutableListOf<Double>()
        for (i in amount / 2..amount) {
            data.add(i.toDouble())
        }
        return data
    }

    val rawData = mapOf(
        "1" to generateData(10),
        "2" to generateData(20),
        "3" to generateData(15),
        "4" to generateData(25),
    )

    val data = rawData.toList().flatMap { (name, entities) -> entities.map { name to it } }
}
