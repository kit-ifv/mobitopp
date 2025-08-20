package core.results.plots

import kotlin.test.Test
import kotlin.test.assertContentEquals

class HistogramPlotterTest {

    @Test
    fun `test coloring by y value`() {
        val yColor = plotterWithX.colorBy{ _, _, _, y -> y }
        val expectedColorCalls = listOf(2, 1)
        assertPlotterColorValues(yColor, expectedColorCalls)
    }

    @Test
    fun `test coloring by x value`() {
        val xColor = plotterWithX.colorBy{ _, _, x, _ -> x }
        val expectedColorCalls = listOf(true, false)
        assertPlotterColorValues(xColor, expectedColorCalls)
    }

    @Test
    fun `test coloring by group`() {
        val groupColor = plotterWithX.colorBy{ _, g, _, _ -> g }
        val expectedColorCalls = listOf(true, false)
        assertPlotterColorValues(groupColor, expectedColorCalls)
    }

    @Test
    fun `test coloring by entity`() {
        val entityColor = plotterWithX.colorBy{ e, _, _, _ -> e }
        val expectedColorCalls = listOf(null, null) // todo: maybe add coloring when E = X?
        assertPlotterColorValues(entityColor, expectedColorCalls)
    }

    private fun <E, G, X, C> assertPlotterColorValues(plotter: PlotterBuilderWithColor<E, G, X, Unit, Int, C>, expected: List<C>) {
        val calledValues = mutableListOf<C>()
        plotter.withStyle {
            colorMap = {color: C -> calledValues.add(color); black }
        }.asHistogram(false, false)
            .plot()
        assertContentEquals(expected, calledValues)
    }
}

private val data = listOf(true, true, false)
private val plotterWithX = forData { data }
    .groupBy { it }
    .count{it}

private val black = RGB(0, 0, 0)
private val testColorMap = {num : Int -> RGB(num*10, num*10, num*10)}