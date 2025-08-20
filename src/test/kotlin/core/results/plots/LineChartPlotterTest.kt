package core.results.plots

import core.modelsteps.asResource
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test

class LineChartPlotterTest {
    private data class Entity<X, Y, C>(val x: X, val y: Y, val c: C)
    private val plotBuilder = getBuilder("lineChart")

    private fun getBuilder(plotName: String):  PlotterBuilder<Pair<String, Entity<Double, Double, Double>>, String, Double, Double, Double, String> {
        return forData { data }
            .groupBy {it.first }
            .aggregateBy({it.second.y}, Aggregation.AllValues())
            .over {it.second.x}
            .colorBy { e, g, x, y -> g }
            .withStyle {
                colorMap = { stringColorFun(it) }
                name = plotName
            }
    }

    @Test
    fun test() {
        //val check = {it: Any? -> it is Number && it.toDouble().isFinite() } // for some reason the color data is converted to strings and then is checked for nnumbers.
        plotBuilder.asLineChart().plot()
    }

    private fun< C> plot(comparisonData: List<Pair<String, Entity<Double, Double, C>>>) {
        val plot = plotBuilder.compareToResource(comparisonData.asResource(
            name="comparison", source="I made it up"))
            .groupBy {it.first }
            .plot { it.second.y }
            .over({it.second.x}, {it})
            .asLineChart()

        plot.plot()
    }

    @Test
    fun testComparison(){
        this.plot(comparisonData)
    }
    @Test
    fun testOneComparisonMissing() {
        val missing = comparisonData.filter { it.first == "line" || it.first == "parabola"}
        this.plot(missing)
    }

    @Test
    fun testPartialDataPoints() {
        val missing = comparisonData.filter {it.second.x < 0}
        this.plot(missing)
    }

    private fun getAggregated():  PlotterBuilder<Pair<String, Entity<Double, Double, Double>>, String, String, Number, Double, String>{
        return forData { data }
            .groupBy {"it.first" }
            .aggregateBy({it.second.y}, Aggregation.Mean)
            .over {it.first}
            .colorBy { e, g, x, y -> g }
            .withStyle {
                colorMap = { stringColorFun(it) }
            }
    }

    @Test
    fun testAggregation() {
        val builder = getAggregated()
        builder.asLineChart().plot()
    }

    @Test
    fun testCompAggregation(){
        val builder = getAggregated()
        builder.compareToResource(comparisonData.asResource(
            name="comparison", source="I made it up"))
            .groupBy {"it.first "}
            .plot { it.second.y }
            .over({it}, {it.first})
            .asLineChart()
            .plot()
    }

    @Test
    fun testYColoring() { //todo: add to builder that groupingBy and coloring not by group does not work together. für plotter vielleicht mitgeben, ob kontinuierlich mit farbskale oder oder gruppen mit diskreten farben
        val builder = forData { data }
            .groupBy {it.first }
            .aggregateBy({it.second.y}, Aggregation.AllValues())
            .over {it.second.x}
            .colorBy { e, g, x, y -> y }
            .withStyle {
                colorMap = colorFun
                name="yColoring"
            }
        builder.asLineChart().plot()
    }

    @Test
    fun normalizeRelativeTest() {
        for (i in 0..3){
            val relative =  i >= 2
            val normalize = (i % 2) == 1
            getBuilder("lineChart_rel${relative}_nor${normalize}").asScalableSortableLineChart(relative=relative,
            normalize = normalize).plot()
        }
    }

    @Test
    fun comparisonNormalizeTest() {
        plotBuilder.compareToResource(comparisonData.asResource(
            name="comparison", source="I made it up"))
            .groupBy {it.first }
            .plot { it.second.y }
            .over({it.second.x}, {it})
            .asScalableSortableLineChart(relative=false, normalize = true)
            .plot()
    }

    @Test()
    fun testSorting() {
        forData { data }
            .groupBy {(it.second.x).toString()}
            .aggregateBy({it.second.y}, Aggregation.AllValues())
            .over {it.second.x}
            .colorBy { e, g, x, y -> g }
            .withStyle {
                colorMap = { randomColor() }
                groupOrder = Ordering.AscendingBy { it }
                name="groupByX"
            }
            .asScalableSortableLineChart()
            .plot()
    }

    private val xValues = (-50..50).map {it.toDouble() / 10}
    private val data =mapOf(
        "parabola" to xValues.map { Entity(it, it * it + 1, it % 3) },
        "line" to xValues.map { Entity(it, it + 10, it % 3) },
        "sine" to xValues.map { Entity(it, sin(it) + 2, it % 3) }
    ).toList().flatMap { (name, entities) -> entities.map { name to it } }
    private val colorFun = { color: Any ->
        if (color is Int)
            RGB(120 * (abs(color) % 3) % 255,
                120 * ((abs(color) + 1) % 3) % 255,
                120* ((abs(color) + 2) % 3) % 255)
        else RGB(0, 120, 0)
    }
    private fun stringColorFun(function: String): RGB {
        return when (function) {
            "parabola" -> RGB(255, 0, 0)
            "line" -> RGB(0, 255, 0)
            "sine" -> RGB(0, 0, 255)
            else -> RGB(0, 0, 0)
        }
    }


    private val comparisonData = mapOf(
        "parabola" to xValues.map { Entity(it, -it * it, it % 3) }, //Todo: currently the parabola is white?
        "line" to xValues.map { Entity(it, 0.5 * it, it % 3) },
        "sine" to xValues.map { Entity(it, cos(it), it % 3) }
    ).toList().flatMap { (name, entities) -> entities.map { name to it } }
}