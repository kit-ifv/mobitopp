package core.results.plots

import org.junit.jupiter.api.Tag
import kotlin.test.Test
@Tag("plot")
class HistogramComparisonTest {

    private fun plotterBuilder() =
        forData { data }
            .groupBy { it }
            .count { it }

    @Test
    fun `test comparison`() {
        val histogram = plotterBuilder().compareTo {
            forData {
                comparisonData
            }.groupBy {
                "comparison"
            }.plot { ch ->
                comparisonData.count { it == ch }
            }.over {
                "comp_" + xMappingFunc(it)
            }
        }.asHistogram {
            name = "Histogram singleCompGroup"
            coloring = { randomColor() }
        }

        histogram.plot(testPlotResultDir)
    }
}

private val data =
    List(2) { "var1" } +
        List(2) { "var2" } +
        List(2) { "var3" }

private val comparisonData =
    List(1) { '1' } +
        List(3) { '2' } +
        List(1) { '3' }

private val xMappingFunc: (Char) -> String = { c: Char -> xMapping[c]!! }

private val xMapping =
    mapOf(
        '1' to "var1",
        '2' to "var2",
        '3' to "var3"
    )
