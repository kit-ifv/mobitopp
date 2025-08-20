package core.results.plots

import core.modelsteps.asResource
import kotlin.test.Test

class BoxPlotTest {
    //
    private fun plotterBuilder(plotName: String)
    = forData { data }
        .groupBy { it.first }
        .summarize { it.second }
        .over { it.first }
        .colorBy { e, g, x, y -> g }
        .withStyle {
            name = "BoxPlot $plotName"
        }


    @Test
    fun testBoxPlot() {
        plotterBuilder("normal").asBoxPlot().plot()
    }

    @Test
    fun testComparisonBoxPlot() {
        val comparisonData = raw_data.map { (name, value_list) ->
            val length = value_list.size
            name to Summary(
            value_list[0],value_list[length/4], value_list[length/2], value_list[length/4 * 3], value_list[length-1]) }

        val comparisonResource = comparisonData.asResource("comparison", "I made it up")
        plotterBuilder("comparison").compareToResource(comparisonResource)
            .groupBy { "comparison" }
            .plot { it.second }
            .over({ it.first }, { it }, "")
            .asBoxPlot()
            .plot()
    }

    //generate data that can be used for a sample box plot
    private fun generateData(amount: Int): List<Double> {
        val data = mutableListOf<Double>()
        for (i in amount/2..amount) {
            data.add(i.toDouble())
        }
        return data
    }

    val raw_data = mapOf(
        "1" to generateData(10),
        "2" to generateData(20),
        "3" to generateData(15),
        "4" to generateData(25),
    )
    val data = raw_data.toList().flatMap { (name, entities) -> entities.map { name to it } }
}