package core.results.plots.render

import core.results.plots.RGB
import core.results.plots.data.PlotData
import core.results.plots.data.Summary
import core.results.plots.data.Trace
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.kandy.util.color.Color

const val IS_COMP_COL = "is_comp"
const val GROUP_COL = "group"
const val X_COL = "x"
const val Y_COL = "y"

const val MIN_COL = "min"
const val LOWER_QUART_COL = "lower_quart"
const val MEDIAN_COL = "median"
const val UPPER_QUART_COL = "upper_quart"
const val MAX_COL = "max"

class DataFrameBuilder<G, X, Y>(
    private val name: String,
    data: PlotData<G, X, Y>,
    comparisonData: PlotData<G, X, Y>?,
) {

    private val rawGroups: List<G>
    private val rawXs: List<X>
    private val rawYs: List<Y>

    internal var dataFrame: DataFrame<*>

    private var groupWasUsed: Boolean = false
    private var xWasUsed: Boolean = false
    private var yWasUsed: Boolean = false

    fun getRawGroupData(): List<G> {
        if (groupWasUsed) {
            println("WARNING: Group data was already used for column creation for plot $name!")
        }
        groupWasUsed = true
        return rawGroups
    }

    fun getRawXData(): List<X> {
        if (xWasUsed) {
            println("WARNING: X data was already used for column creation for plot $name!")
        }
        xWasUsed = true
        return rawXs
    }

    fun getRawYData(): List<Y> {
        if (yWasUsed) {
            println("WARNING: Y column was already used for column creation for plot $name!")
        }
        yWasUsed = true
        return rawYs
    }

    init {
        val traces = data.asTraces()
        val comparisonTraces = comparisonData?.asTraces() ?: emptyList()

        // Pre-size for fewer resizes in large datasets
        val totalPoints = traces.sumOf { it.points.size } + comparisonTraces.sumOf { it.points.size }

        val isCompList = ArrayList<Boolean>(totalPoints)
        val rawGroupsList = ArrayList<G>(totalPoints)
        val rawXsList = ArrayList<X>(totalPoints)
        val rawYsList = ArrayList<Y>(totalPoints)

        fun addAll(from: List<Trace<G, X, Y>>, isComp: Boolean) {
            for (t in from) {
                for (p in t.points) {
                    isCompList += isComp
                    rawGroupsList += t.group
                    rawXsList += p.x
                    rawYsList += p.y
                }
            }
        }

        addAll(traces, false)
        addAll(comparisonTraces, true)

        rawGroups = rawGroupsList
        rawXs = rawXsList
        rawYs = rawYsList

        dataFrame = DataFrame.Companion.empty(totalPoints)
        dataFrame = dataFrame.add(isCompList.toColumn(IS_COMP_COL))
    }

    fun build(): DataFrame<*> {
        if (!groupWasUsed) {
            println("WARNING: group data was not used for dataframe creation for plot: $name")
        }

        if (!xWasUsed) {
            println("WARNING: x data was not used for dataframe creation for plot: $name")
        }

        if (!yWasUsed) {
            println("WARNING: y data was not used for dataframe creation for plot: $name")
        }

        return dataFrame
    }

    fun groupAsString(toString: (G) -> String = { it.toString() }): DataFrameBuilder<G, X, Y> {
        dataFrame = dataFrame.add(getRawGroupData().map(toString).toColumn(GROUP_COL))
        return this
    }

    fun xAsString(toString: (X) -> String = { it.toString() }): DataFrameBuilder<G, X, Y> {
        dataFrame = dataFrame.add(getRawXData().map(toString).toColumn(X_COL))
        return this
    }

    private fun <X> colorBy(colorMap: (X) -> RGB, column: String, raw: List<X>): ColorScale {
        require(column in dataFrame.columns().map { it.name() }) {
            "Cannot color by $column before data was added to dataframe!"
        }

        val keys: List<Any> = dataFrame[column].toList().filterNotNull()

        require(raw.size == keys.size) {
            "Row count mismatch: df[$column] has ${keys.size}, raw has ${raw.size}"
        }

        val domain = mutableListOf<Any>()
        val range = mutableListOf<Color>()

        keys.zip(raw).toMap().forEach { (key, x) ->
            domain += key
            range += colorMap(x).toColor()
        }

        return ColorScale(domain, range)
    }

    // access to raw data here without getter, since creation of color map does not add a column to dataframe
    // hence this should not update the data was used flags
    fun colorByGroup(map: (G) -> RGB) = colorBy(map, GROUP_COL, rawGroups)
    fun colorByX(map: (X) -> RGB) = colorBy(map, X_COL, rawXs)
    fun colorByY(map: (Y) -> RGB) = colorBy(map, Y_COL, rawYs)

    fun combineCompAndXLabel(newColumnName: String, toCompLabel: (Any) -> String = ::compLabelWrapper) =
        combineCompAndColLabel(X_COL, xWasUsed, newColumnName, toCompLabel)

    fun combineCompAndGroupLabel(newColumnName: String, toCompLabel: (Any) -> String = ::compLabelWrapper) =
        combineCompAndColLabel(GROUP_COL, groupWasUsed, newColumnName, toCompLabel)

    fun combineCompAndYLabel(newColumnName: String, toCompLabel: (Any) -> String = ::compLabelWrapper) =
        combineCompAndColLabel(Y_COL, yWasUsed, newColumnName, toCompLabel)

    private fun combineCompAndColLabel(
        keyColumn: String,
        checkColumn: Boolean,
        newColumnName: String,
        toCompLabel: (Any) -> String = ::compLabelWrapper
    ): DataFrameBuilder<G, X, Y> {
        require(checkColumn) {
            "Cannot combine $IS_COMP_COL with $keyColumn since the latter was not yet created for plot: $name!" +
                " Current dataframe columns: ${dataFrame.columns().map { it.name() }}"
        }

        dataFrame = dataFrame.add(newColumnName) {
            val x: String = this[keyColumn] as String
            val flag: Boolean = this[IS_COMP_COL] as Boolean
            if (flag) { toCompLabel(x) } else { x }
        }

        val indexCol = "_row_index"
        val withIndex = dataFrame.add(indexCol) {
            index()
        }

        val firstOrigIdxByKey: Map<String, Int> =
            withIndex.rows()
                .asSequence()
                .filter { !(it[IS_COMP_COL] as Boolean) }
                .groupingBy { it[keyColumn] as String }
                .fold(Int.MAX_VALUE) { acc, row ->
                    minOf(acc, row[indexCol] as Int)
                }

        val firstCompIdxByKey: Map<String, Int> =
            withIndex.rows()
                .asSequence()
                .filter { it[IS_COMP_COL] as Boolean }
                .groupingBy { it[keyColumn] as String }
                .fold(Int.MAX_VALUE) { acc, row ->
                    minOf(acc, row[indexCol] as Int)
                }

        val orderColumn = "_order_column"
        val withGroupOrder = withIndex.add(orderColumn) {
            val k = this[keyColumn] as String
            firstOrigIdxByKey[k] ?: firstCompIdxByKey[k] ?: Int.MAX_VALUE
        }

        val sorted = withGroupOrder.sortBy {
            cols(orderColumn, IS_COMP_COL, indexCol)
        }.remove(orderColumn, indexCol)

        dataFrame = sorted

        return this
    }

    private fun compLabelWrapper(value: Any) = "[$value]"

    // TODO x, y, group to any basic type + time (with converter lambda) so we can hide the raw data list
}

data class ColorScale(val domain: List<Any>, val range: List<Color>)

fun <G, X, Y : Number> DataFrameBuilder<G, X, Y>.yAsDouble(): DataFrameBuilder<G, X, Y> {
    dataFrame = dataFrame.add(getRawYData().map { it.toDouble() }.toColumn(Y_COL))
    return this
}

fun <G, Y> DataFrameBuilder<G, Int, Y>.xAsInt(): DataFrameBuilder<G, Int, Y> {
    dataFrame = dataFrame.add(getRawXData().toColumn(X_COL))
    return this
}

fun <G, X : Number, Y> DataFrameBuilder<G, X, Y>.xAsDouble(): DataFrameBuilder<G, X, Y> {
    dataFrame = dataFrame.add(getRawXData().map { it.toDouble() }.toColumn(X_COL))
    return this
}

fun <G, X, Y : Number> DataFrameBuilder<G, X, Summary<Y>>.yAsDoubleSummary(): DataFrameBuilder<G, X, Summary<Y>> {
    val rows = dataFrame.size().nrow

    val mins = ArrayList<Double>(rows)
    val lowerQuarts = ArrayList<Double>(rows)
    val medians = ArrayList<Double>(rows)
    val upperQuarts = ArrayList<Double>(rows)
    val maxes = ArrayList<Double>(rows)

    getRawYData().forEach {
        mins += it.min.toDouble()
        lowerQuarts += it.lowerQuart.toDouble()
        medians += it.median.toDouble()
        upperQuarts += it.upperQuart.toDouble()
        maxes += it.max.toDouble()
    }

    dataFrame = dataFrame.add(mins.toColumn(MIN_COL))
    dataFrame = dataFrame.add(lowerQuarts.toColumn(LOWER_QUART_COL))
    dataFrame = dataFrame.add(medians.toColumn(MEDIAN_COL))
    dataFrame = dataFrame.add(upperQuarts.toColumn(UPPER_QUART_COL))
    dataFrame = dataFrame.add(maxes.toColumn(MAX_COL))
    return this
}
