package core.results.plots

import org.junit.jupiter.api.Tag
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
@Tag("plot")
class LineChartPlotterTest {
    data class Entity<X, Y, C>(val x: X, val y: Y, val c: C)

    private val plotBuilder = getBuilder()

    private fun getBuilder() = forData { data }
        .groupBy { it.first }
        .plot { it.second.y }
        .over { it.second.x }

    @Test
    fun test() {
        plotBuilder.asLinePlot {
            name = "lineChart test"
            coloring = ::stringColorFun
        }.plot(testPlotResultDir)
    }

    private fun <C> plot(comparisonData: List<Pair<String, Entity<Double, Double, C>>>) {
        val plot = plotBuilder.compareTo {
            forData {
                comparisonData
            }.groupBy {
                "comparison"
            }.plot {
                it.second.y
            }.over {
                it.second.x
            }
        }.asLinePlot {
            name = "lineChart comparison"
            coloring = ::stringColorFun
        }

        plot.plot(testPlotResultDir)
    }

    @Test
    fun testComparison() {
        this.plot(comparisonData)
    }

    @Test
    fun testOneComparisonMissing() {
        val missing = comparisonData.filter { it.first == "line" || it.first == "parabola" }
        this.plot(missing)
    }

    @Test
    fun testPartialDataPoints() {
        val missing = comparisonData.filter { it.second.x < 0 }
        this.plot(missing)
    }

    private fun getAggregated() = forData { data }
        .groupBy { "it.first" }
        .plotMeanOf {
            it.second.y
        }
        .over { it.first }

    @Test
    fun testAggregation() {
        val builder = getAggregated()
        builder.asLinePlot {
            name = "lineChart test agg"
            coloring = ::stringColorFun
        }.plot(testPlotResultDir)
    }

    @Test
    fun testCompAggregation() {
        val builder = getAggregated()
        builder.compareTo {
            forData {
                comparisonData
            }.groupBy {
                "it.first"
            }.plot {
                it.second.y
            }.over {
                it.first
            }
        }.asLinePlot {
            name = "lineChart test agg comparison"
            coloring = { colorFun(it) }
        }.plot(testPlotResultDir)
    }

    private val xValues = (-50..50).map { it.toDouble() / 10 }
    private val data = mapOf(
        "parabola" to xValues.map { Entity(it, it * it + 1, it % 3) },
        "line" to xValues.map { Entity(it, it + 10, it % 3) },
        "sine" to xValues.map { Entity(it, sin(it) + 2, it % 3) },
    ).toList().flatMap { (name, entities) -> entities.map { name to it } }

    private val colorFun = { color: Any ->
        if (color is Int) {
            RGB(
                120 * (abs(color) % 3) % 255,
                120 * ((abs(color) + 1) % 3) % 255,
                120 * ((abs(color) + 2) % 3) % 255,
            )
        } else {
            RGB(0, 120, 0)
        }
    }

    private fun stringColorFun(function: String): RGB = when (function) {
        "parabola" -> RGB(255, 0, 0)
        "line" -> RGB(0, 255, 0)
        "sine" -> RGB(0, 0, 255)
        else -> RGB(0, 0, 0)
    }

    private val comparisonData = mapOf(
        "parabola" to xValues.map { Entity(it, -it * it, it % 3) },
        "line" to xValues.map { Entity(it, 0.5 * it, it % 3) },
        "sine" to xValues.map { Entity(it, cos(it), it % 3) },
    ).toList().flatMap { (name, entities) -> entities.map { name to it } }
}
