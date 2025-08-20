package core.results.plots

import core.modelsteps.asResource
import kotlin.random.Random
import kotlin.test.Test

class ScatterPlotterTest {

    private fun plotterBuilder(plotName: String)
    = forData { data }
        .groupBy { it.first}
        .plot {it.second.second}
        .over {it.second.first}
        .colorBy {_, g, _, _ -> g}
        .withStyle {
            colorMap = {it -> colors[it]!! }
            name = plotName
        }

    @Test
    fun testScatterPlot() {
        plotterBuilder("scatter").asScatterPlot().plot()
    }

    @Test
    fun testComparison() {
        plotterBuilder("scatter_comparison").compareToResource(comparisonData.asResource("comparison", "I made it up"))
            .groupBy { it.first}
            .plot { it.second.second }
            .over({ it.second.first }, { it }, "")
            .asScatterPlot()
            .plot()
    }

    val random = Random(0)
    val data_fun = {x: Number -> x.toDouble() + 30*random.nextDouble()}
    val data = (0..100).map { listOf(
        Pair("data1", Pair(it, data_fun(it))),
        Pair("data2", Pair(it, 20 + data_fun(it/2))),
        ) }.flatten()

    val comparisonData = (0..100).map { listOf(
        Pair("data1", Pair(it, 100 - data_fun(it))),
        Pair("data2", Pair(it, 100 - 20 + data_fun(it/2))),
        ) }.flatten()

    val colors = mapOf("data1" to RGB(0, 255, 0), "data2" to RGB(255, 0, 0))
}