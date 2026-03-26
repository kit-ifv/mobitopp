package core.results.plots

import org.junit.jupiter.api.Tag
import kotlin.random.Random
import kotlin.test.Test
@Tag("plot")
class ScatterPlotterTest {

    private fun plotterBuilder() = forData { data }
        .groupBy { it.first }
        .plot { it.second.second }
        .over { it.second.first }

    @Test
    fun testScatterPlot() {
        plotterBuilder().asScatterPlot {
            name = "scatter"
            coloring = { randomColor() }
        }.plot(testPlotResultDir)
    }

    @Test
    fun testComparison() {
        plotterBuilder().compareTo {
            forData {
                comparisonData
            }.groupBy {
                it.first
            }.plot {
                it.second.second
            }.over {
                it.second.first
            }
        }.asScatterPlot {
            name = "scatter_comparison"
            coloring = { colors[it]!! }
        }.plot(testPlotResultDir)
    }

    val random = Random(0)
    val dataFun = { x: Number -> x.toDouble() + 30 * random.nextDouble() }
    val data = (0..100).map {
        listOf(
            Pair("data1", Pair(it, dataFun(it))),
            Pair("data2", Pair(it, 20 + dataFun(it / 2))),
        )
    }.flatten()

    val comparisonData = (0..100).map {
        listOf(
            Pair("data1", Pair(it, 100 - dataFun(it))),
            Pair("data2", Pair(it, 100 - 20 + dataFun(it / 2))),
        )
    }.flatten()

    val colors = mapOf("data1" to RGB(0, 255, 0), "data2" to RGB(255, 0, 0))
}
