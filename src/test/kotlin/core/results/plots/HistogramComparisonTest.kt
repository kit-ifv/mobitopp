package core.results.plots

import core.modelsteps.asResource
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.LegacyMode
import kotlin.test.Test

class HistogramComparisonTest {

//    val resultFile = File("src/test/resources/Rastatt_data/demandsimulationResult.csv")
//    val resultData = DemandSimulationResultMapping(resultFile)
//    val trips = resultData.rows.take(50_000).toList()

    private fun plotterBuilder(plotName: String) = forData { data }
        .groupBy {it}
        .count { it }
        .colorBy {_,_,x, y -> x}
        .withStyle {
            colorMap = randomColor
            name = "histogram_Comparison_Test $plotName"
        }

    @Test
    fun `test comparison`(){
        val histogram = plotterBuilder("singleCompGroup")
            .compareToResource(comparisonData.asResource(
                name="comparison", source="I made it up"))
            .groupBy { "comparison" }
            .plot {ch -> comparisonData.count {it == ch} }
            .over (
                xAttribute = { it },
                mapping =  xMappingFunc,
                labelPrefix = "comp_")
            .asHistogram(normalize = false, relative = false)

        histogram.plot()
    }

    @Test
    fun `test comparison with same`() {
        val histogram = plotterBuilder("with same att")
            .compareToResource(comparisonData.asResource(
                name="comparison", source="I made it up"))
            .withSameAttributes(
                mapping = xMappingFunc,
                yAttribute = {comp -> comparisonData.count {it == comp}}
            ).asHistogram(normalize = false, relative = false)
        histogram.plot()
    }

//    @Test
//    fun `test mode over purpose`() {
//        for (i in 0..3){
//            val relative =  i >= 2
//            val normalize = (i % 2) == 1
//            val plotter = forData { trips }
//                .groupBy { it.mainMode }
//                .count { it.tourPurpose }
//                .colorBy {_,g,x, y -> g }
//                .withStyle {
//                    name = "Mode over purpose, relative: $relative, normal $normalize"
//                    _root_ide_package_.core.results.plots.colorMap = ::modeColor
//                }.asHistogram(relative = relative, normalize = normalize)
//            plotter.plot()
//        }
//    }
//
//    @Test
//    fun `test mode over purpose comparison`() {
//        val purposes = trips.map { it.tourPurpose }.toSet().toList()
//        val comparison = trips.map { it.mainMode }.toSet().toList().flatMap { mode ->
//            purposes.map { purpose ->
//                Comparison(mode, purpose, Random.nextInt(5_000))
//            }
//        }
//        for (i in 0..3){
//            val relative =  i < 2
//            val normalize = (i % 2) == 0
//            val plotter = forData { trips }
//                .groupBy { it.mainMode }
//                .count { it.tourPurpose }
//                .colorBy {_,g,x, y -> g }
//                .withStyle {
//                    name = "comp Mode over purpose, relative: $relative, normal $normalize"
//                    _root_ide_package_.core.results.plots.colorMap = ::modeColor
//                }.compareToResource(comparison.asResource(
//                    name="comparison", source="I made it up"))
//                .groupBy { it.mode }
//                .plot { it.count }
//                .over (
//                    xAttribute = { it.activity },
//                    mapping = { it },
//                    labelPrefix = "comp_")
//                .asHistogram(normalize = normalize, relative = relative)
//            plotter.plot()
//        }
//    }

}

private val data =
    List(2) {"var1"} +
    List(2) {"var2"} +
    List(2) {"var3"}

private val comparisonData =
    List(1) { '1' } +
    List(3) { '2' } +
    List(1) { '3' }


private val colorMap: Map<String, RGB> = data.toSet().associateWith { randomColor() }
private val randomColor = {x: String -> colorMap[x]!!}

private val xMappingFunc: (Char) -> String = {it: Char -> xMapping[it]!!}

private val xMapping =
    mapOf(
        '1' to "var1",
        '2' to "var2",
        '3' to "var3"
    )

private class Comparison(val mode: LegacyMode, val activity: LegacyActivityType, val count: Int)