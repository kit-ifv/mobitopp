package core.results.plots

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import units.share
import utils.units.round
import kotlin.test.assertEquals

class PlotterTest {

    private fun plotBuilderSpecs() = forData {
        elementValues.map { it.second }
    }.groupBy {
        it.x
    }.plot(yAttribute).over {
        it.x
    }.colorBy { _, _, x, _ -> x }.withStyle {
    }.let { it.style to it.values }

    private fun manualSpecs() = (PlotStyleBuilder<Int, Int, Int>()) to (
        PlotDataSpecification(
            entities = { elementValues.map { it.second } },
            groupBy = { it.x },
            xAttribute = { it.x },
            yAttribute = yAttribute,
            aggregation = Aggregation.AllValues(),
            colorBy = { _, _, x, _ -> x },
        )
        )

    @TestFactory
    fun testDataProcessingAllValuesGrouped() = listOf(
        ::plotBuilderSpecs to "Builder",
        ::manualSpecs to "Manual",
    ).map { (specs, name) ->

        DynamicTest.dynamicTest("Check Plotter specs ($name) for correct data processing") {
            val (style, data) = specs()
            assertGroupingAllValuesColorByGroup(style, data)
        }
    }

    private fun assertGroupingAllValuesColorByGroup(
        styling: PlotStyling<Int, Int, Int>,
        dataSpecification: PlotDataSpecification<Entity, Int, Int, Double, Double, Int>,
    ) {
        object : Plotter<Entity, Int, Int, Double, Double, Int>(
            style = styling,
            values = dataSpecification
        ) {

            val expectedTraces = listOf(
                Trace(
                    key = 1,
                    dataPoints = listOf(
                        DataPoint(x = 1, y = 6.5, c = 1),
                        DataPoint(x = 1, y = 9.1, c = 1)
                    )
                ),
                Trace(
                    key = 2,
                    dataPoints = listOf(
                        DataPoint(x = 2, y = 2.2, c = 2),
                        DataPoint(x = 2, y = 3.0, c = 2),
                        DataPoint(x = 2, y = 7.0, c = 2),
                        DataPoint(x = 2, y = 7.6, c = 2),
                    )
                ),
                Trace(
                    key = 3,
                    dataPoints = listOf(
                        DataPoint(x = 3, y = 5.4, c = 3),
                        DataPoint(x = 3, y = 5.4, c = 3),
                        DataPoint(x = 3, y = 8.1, c = 3),
                    )
                ),
                Trace(
                    key = 4,
                    dataPoints = listOf(
                        DataPoint(x = 4, y = 1.3, c = 4),
                        DataPoint(x = 4, y = 3.0, c = 4),
                        DataPoint(x = 4, y = 4.7, c = 4),
                        DataPoint(x = 4, y = 5.0, c = 4),
                        DataPoint(x = 4, y = 6.5, c = 4),
                        DataPoint(x = 4, y = 8.8, c = 4),
                    )
                ),
                Trace(
                    key = 5,
                    dataPoints = listOf(
                        DataPoint(x = 5, y = 3.0, c = 5),
                        DataPoint(x = 5, y = 3.2, c = 5),
                        DataPoint(x = 5, y = 4.0, c = 5),
                        DataPoint(x = 5, y = 5.2, c = 5),
                        DataPoint(x = 5, y = 7.0, c = 5),
                    )
                ),
            )

            override fun plotTraces(
                traces: List<Trace<Int, Int, Double, Int>>,
                comparisonTraces: List<Trace<Int, Int, Double, Int>>?
            ) {
                assertEquals(expectedTraces, traces)
            }
        }
    }
}

class OrderingTest {

    private data class Container(val key: Int) {
        override fun equals(other: Any?): Boolean =
            other?.takeIf {
                it is Container
            }?.let {
                it as Container
            }?.let {
                this.key == it.key
            } ?: false

        override fun hashCode() = key
    }

    private val shuffledIntList = listOf(8, 4, 2, 6, 1, 9, 3, 5, 7)

    private val ascendingIntList = (1..9).toList()

    private val decendingIntList = (9 downTo 1).toList()

    private val shuffeledContainerList = shuffledIntList.map { Container(it) }.toList()

    private val ascendingContainerList = ascendingIntList.map { Container(it) }.toList()

    private val decendingContainerList = decendingIntList.map { Container(it) }.toList()

    @TestFactory
    fun arrangeIntList() = listOf(
        Ordering.Arbitrary<Int>() to shuffledIntList,
        Ordering.Ascending<Int>() to ascendingIntList,
        Ordering.Descending<Int>() to decendingIntList,
        Ordering.AscendingBy<Int, Long> { it.toLong() } to ascendingIntList,
        Ordering.DescendingBy<Int, Long> { it.toLong() } to decendingIntList,

    ).map { (ordering, expected) ->

        DynamicTest.dynamicTest(
            "${ordering::class.simpleName}.arrange applied to $shuffledIntList should produce $expected"
        ) {
            val actual = ordering.arrange(shuffledIntList)
            assertEquals(expected, actual)
        }
    }

    @TestFactory
    fun arrangeContainerListByKey() = listOf(
        Ordering.Arbitrary<Int>() to shuffeledContainerList,
        Ordering.Ascending<Int>() to ascendingContainerList,
        Ordering.Descending<Int>() to decendingContainerList,
        Ordering.AscendingBy<Int, Long> { it.toLong() } to ascendingContainerList,
        Ordering.DescendingBy<Int, Long> { it.toLong() } to decendingContainerList,

    ).map { (ordering, expected) ->

        DynamicTest.dynamicTest(
            "${ordering::class.simpleName}.arrangeBy applied to " +
                "$shuffeledContainerList should produce $expected"
        ) {
            val actual = ordering.arrangeBy(shuffeledContainerList) { it.key }
            assertEquals(expected, actual)
        }
    }
}

class AggregationTest {

    private val expectedAllValues = dataset.map { Triple(it.x, it.y, it.c) }
    private val expectedCount = listOf(
        Triple(1, 2, 1),
        Triple(2, 4, 2),
        Triple(3, 3, 3),
        Triple(4, 6, 4),
        Triple(5, 5, 5)
    )

    private val expectedSum = listOf(
        Triple(1, 14.7, 1),
        Triple(2, 19.8, 2),
        Triple(3, 18.9, 3),
        Triple(4, 29.3, 4),
        Triple(5, 22.4, 5)
    )

    private val expectedMean = listOf(
        Triple(1, 7.35, 1),
        Triple(2, 4.95, 2),
        Triple(3, 6.3, 3),
        Triple(4, (29.3 / 6).round(3), 4),
        Triple(5, 4.48, 5)
    )

    private val expectedMedian = listOf(
        Triple(1, 5.6, 1),
        Triple(2, 3.0, 2),
        Triple(3, 5.4, 3),
        Triple(4, 4.7, 4),
        Triple(5, 4.0, 5)
    )

    private val expectedMin = listOf(
        Triple(1, 5.6, 1),
        Triple(2, 2.2, 2),
        Triple(3, 5.4, 3),
        Triple(4, 1.3, 4),
        Triple(5, 3.0, 5)
    )

    private val expectedMax = listOf(
        Triple(1, 9.1, 1),
        Triple(2, 7.6, 2),
        Triple(3, 8.1, 3),
        Triple(4, 8.8, 4),
        Triple(5, 7.0, 5)
    )

    private val expected90pQuantile = listOf(
        Triple(1, 5.6, 1),
        Triple(2, 7.0, 2),
        Triple(3, 5.4, 3),
        Triple(4, 6.5, 4),
        Triple(5, 5.2, 5)
    )

    private val expected25pQuantile = listOf(
        Triple(1, 5.6, 1),
        Triple(2, 2.2, 2),
        Triple(3, 5.4, 3),
        Triple(4, 3.0, 4),
        Triple(5, 3.2, 5)
    )

    private val expected75pQuantile = listOf(
        Triple(1, 5.6, 1),
        Triple(2, 7.0, 2),
        Triple(3, 5.4, 3),
        Triple(4, 5.0, 4),
        Triple(5, 5.2, 5)
    )

    private val expectedSummary = expectedMin.map {
        Triple(
            first = it.first,
            second = Summary(min = it.second, 0.0, 0.0, 0.0, 0.0),
            third = it.third
        )
    }.zip(expected25pQuantile).map { (target, data) ->
        target.copy(
            second = target.second.copy(lowerQuart = data.second)
        )
    }.zip(expectedMedian).map { (target, data) ->
        target.copy(
            second = target.second.copy(median = data.second)
        )
    }.zip(expected75pQuantile).map { (target, data) ->
        target.copy(
            second = target.second.copy(upperQuart = data.second)
        )
    }.zip(expectedMax).map { (target, data) ->
        target.copy(
            second = target.second.copy(max = data.second)
        )
    }

    private fun <A, B> List<Triple<A, Double, B>>.roundY() = map { it.copy(second = it.second.round(3)) }

    @Test
    fun allValues() {
        val actual = Aggregation.AllValues<Double>().aggregate(
            yAttribute,
            elementValues,
            colorByEntityOrX
        )

        assertEquals(expectedAllValues, actual)
    }

    @Test
    fun count() {
        val actual = Aggregation.Count.aggregate(
            { _ -> },
            elementValues,
            colorByEntityOrX
        )

        assertEquals(expectedCount, actual.sortedBy { it.first })
    }

    @Test
    fun sum() {
        val actual = Aggregation.Sum.aggregate(
            yAttribute,
            elementValues,
            colorByEntityOrX
        )

        assertEquals(expectedSum, actual.sortedBy { it.first }.roundY())
    }

    @Test
    fun median() {
        val actual = Aggregation.LowerMedian<Double>().aggregate(
            yAttribute,
            elementValues,
            colorByEntityOrX
        )

        assertEquals(expectedMedian, actual.sortedBy { it.first })
    }

    @Test
    fun min() {
        val actual = Aggregation.Min<Double>().aggregate(
            yAttribute,
            elementValues,
            colorByEntityOrX
        )

        assertEquals(expectedMin, actual.sortedBy { it.first })
    }

    @Test
    fun max() {
        val actual = Aggregation.Max<Double>().aggregate(
            yAttribute,
            elementValues,
            colorByEntityOrX
        )

        assertEquals(expectedMax, actual.sortedBy { it.first })
    }

    @Test
    fun mean() {
        val actual = Aggregation.Mean.aggregate(
            yAttribute,
            elementValues,
            colorByEntityOrX
        )

        assertEquals(expectedMean, actual.sortedBy { it.first }.roundY())
    }

    @TestFactory
    fun lowerQuantile() = listOf(
        0.25 to expected25pQuantile,
        0.75 to expected75pQuantile,
        0.9 to expected90pQuantile,
    ).map { (quantile, expected) ->

        DynamicTest.dynamicTest("Test aggregate lower quantile ${quantile * 100}p") {
            val actual = Aggregation.LowerQuantile<Double>(quantile.share()).aggregate(
                yAttribute,
                elementValues,
                colorByEntityOrX
            )

            assertEquals(expected, actual.sortedBy { it.first }.roundY())
        }
    }

    @Test
    fun summary() {
        val actual = Aggregation.Summarize<Double>().aggregate(
            yAttribute,
            elementValues,
            colorByEntityOrX
        )

        assertEquals(expectedSummary, actual.sortedBy { it.first })
    }
}

private data class Entity(val x: Int, val y: Double, val c: Int)

//    1 to 5.6, l, u
//    1 to 9.1,

//    2 to 2.2, l
//    2 to 3.0,
//    2 to 7.0, u
//    2 to 7.6,

//    3 to 5.4, l
//    3 to 5.4, u
//    3 to 8.1,

//    4 to 1.3,
//    4 to 3.0, l
//    4 to 4.7,
//    4 to 5.0, u
//    4 to 6.5,
//    4 to 8.8,

//    5 to 3.0,
//    5 to 3.2, l
//    5 to 4.0,
//    5 to 5.2, u
//    5 to 7.0,

private val dataset = listOf(
    1 to 5.6,
    3 to 5.4,
    2 to 7.0,
    2 to 3.0,
    4 to 6.5,
    4 to 4.7,
    2 to 2.2,
    2 to 7.6,
    4 to 1.3,
    5 to 3.2,
    3 to 5.4,
    4 to 3.0,
    5 to 7.0,
    4 to 5.0,
    3 to 8.1,
    4 to 8.8,
    5 to 3.0,
    1 to 9.1,
    5 to 4.0,
    5 to 5.2,
).mapIndexed { index, pair -> Entity(x = pair.first, y = pair.second, c = index) }

private val elementValues = dataset.map { it.x to it }
private val yAttribute: (Entity) -> Double = { it.y }
private val colorByEntityOrX: (Entity?, Int, Any) -> Int = { entity, x, _ -> entity?.c ?: x }
