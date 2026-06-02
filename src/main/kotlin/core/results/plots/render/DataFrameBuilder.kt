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

/** Column flag indicating a row belongs to comparison data. */
const val IS_COMP_COL = "is_comp"

/** Column containing group labels. */
const val GROUP_COL = "group"

/** Column containing x values (string or numeric depending on builder). */
const val X_COL = "x"

/** Column containing y values (numeric). */
const val Y_COL = "y"

/** Column names for summary statistics. */
const val MIN_COL = "min"
const val LOWER_QUART_COL = "lower_quart"
const val MEDIAN_COL = "median"
const val UPPER_QUART_COL = "upper_quart"
const val MAX_COL = "max"

/**
 * Utility to convert PlotData traces into a tabular DataFrame used by renderers.
 * Manages raw columns and supports adding derived columns and color scales.
 *
 * @param G The type of the group identifier.
 * @param X The type of the x-coordinate.
 * @param Y The type of the y-coordinate value.
 * @property name The name of the plot, used for warnings.
 */
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

    /**
     * Access the raw group values; warns if already consumed for a column.
     *
     * @return A list of raw group values.
     */
    fun getRawGroupData(): List<G> {
        if (groupWasUsed) {
            println("WARNING: Group data was already used for column creation for plot $name!")
        }
        groupWasUsed = true
        return rawGroups
    }

    /**
     * Access the raw x values; warns if already consumed for a column.
     *
     * @return A list of raw x values.
     */
    fun getRawXData(): List<X> {
        if (xWasUsed) {
            println("WARNING: X data was already used for column creation for plot $name!")
        }
        xWasUsed = true
        return rawXs
    }

    /**
     * Access the raw y values; warns if already consumed for a column.
     *
     * @return A list of raw y values.
     */
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

        /**
         * Internal helper to add all points from traces to the raw lists.
         */
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

    /**
     * Finalize and return the DataFrame; warns if some raw data was never used.
     *
     * @return The constructed [DataFrame].
     */
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

    /**
     * Add a string group column derived from raw group values.
     *
     * @param toString Function to convert a group identifier to a string.
     * @return This [DataFrameBuilder] for chaining.
     */
    fun groupAsString(toString: (G) -> String = { it.toString() }): DataFrameBuilder<G, X, Y> {
        dataFrame = dataFrame.add(getRawGroupData().map(toString).toColumn(GROUP_COL))
        return this
    }

    /**
     * Add a string x column derived from raw x values.
     *
     * @param toString Function to convert an x-coordinate to a string.
     * @return This [DataFrameBuilder] for chaining.
     */
    fun xAsString(toString: (X) -> String = { it.toString() }): DataFrameBuilder<G, X, Y> {
        dataFrame = dataFrame.add(getRawXData().map(toString).toColumn(X_COL))
        return this
    }

    /**
     * Internal helper to create a color scale for a given column.
     */
    private fun <K> colorBy(colorMap: (K) -> RGB, column: String, raw: List<K>): ColorScale {
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

    /**
     * Build a categorical color scale from group raw values and add no column.
     *
     * @param map Function to map a group identifier to a color.
     * @return A [ColorScale] for groups.
     */
    fun colorByGroup(map: (G) -> RGB) = colorBy(map, GROUP_COL, rawGroups)

    /**
     * Build a categorical color scale from x raw values and add no column.
     *
     * @param map Function to map an x-coordinate to a color.
     * @return A [ColorScale] for x-values.
     */
    fun colorByX(map: (X) -> RGB) = colorBy(map, X_COL, rawXs)

    /**
     * Build a categorical color scale from y raw values and add no column.
     *
     * @param map Function to map a y-value to a color.
     * @return A [ColorScale] for y-values.
     */
    fun colorByY(map: (Y) -> RGB) = colorBy(map, Y_COL, rawYs)

    /**
     * Create a key column combining comparison flag and X label for legend-free stacking.
     *
     * @param newColumnName The name of the new column.
     * @param toCompLabel Function to wrap a value in a comparison label.
     * @return This [DataFrameBuilder] for chaining.
     */
    fun combineCompAndXLabel(newColumnName: String, toCompLabel: (Any) -> String = ::compLabelWrapper) =
        combineCompAndColLabel(X_COL, xWasUsed, newColumnName, toCompLabel)

    /**
     * Create a key column combining comparison flag and group label.
     *
     * @param newColumnName The name of the new column.
     * @param toCompLabel Function to wrap a value in a comparison label.
     * @return This [DataFrameBuilder] for chaining.
     */
    fun combineCompAndGroupLabel(newColumnName: String, toCompLabel: (Any) -> String = ::compLabelWrapper) =
        combineCompAndColLabel(GROUP_COL, groupWasUsed, newColumnName, toCompLabel)

    /**
     * Create a key column combining comparison flag and Y label.
     *
     * @param newColumnName The name of the new column.
     * @param toCompLabel Function to wrap a value in a comparison label.
     * @return This [DataFrameBuilder] for chaining.
     */
    fun combineCompAndYLabel(newColumnName: String, toCompLabel: (Any) -> String = ::compLabelWrapper) =
        combineCompAndColLabel(Y_COL, yWasUsed, newColumnName, toCompLabel)

    /**
     * Combines comparison flag and another column's value into a new label column and sorts the DataFrame.
     */
    private fun combineCompAndColLabel(
        keyColumn: String,
        checkColumn: Boolean,
        newColumnName: String,
        toCompLabel: (Any) -> String = ::compLabelWrapper,
    ): DataFrameBuilder<G, X, Y> {
        require(checkColumn) {
            "Cannot combine $IS_COMP_COL with $keyColumn since the latter was not yet created for plot: $name!" +
                " Current dataframe columns: ${dataFrame.columns().map { it.name() }}"
        }

        dataFrame = dataFrame.add(newColumnName) {
            val x: String = this[keyColumn] as String
            val flag: Boolean = this[IS_COMP_COL] as Boolean
            if (flag) {
                toCompLabel(x)
            } else {
                x
            }
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

    /**
     * Internal helper to wrap a value for comparison labels.
     */
    private fun compLabelWrapper(value: Any) = "[$value]"

    // TODO x, y, group to any basic type + time (with converter lambda) so we can hide the raw data list
}

/**
 * Domain and range values for a categorical color mapping.
 *
 * @property domain List of domain values.
 * @property range List of corresponding colors.
 */
data class ColorScale(val domain: List<Any>, val range: List<Color>)

/**
 * Add a numeric y column from raw Y values by converting Number to Double.
 *
 * @receiver The [DataFrameBuilder].
 * @param G The group type.
 * @param X The x type.
 * @param Y The y type (must be [Number]).
 * @return The [DataFrameBuilder] for chaining.
 */
fun <G, X, Y : Number> DataFrameBuilder<G, X, Y>.yAsDouble(): DataFrameBuilder<G, X, Y> {
    dataFrame = dataFrame.add(getRawYData().map { it.toDouble() }.toColumn(Y_COL))
    return this
}

/**
 * Add an integer x column from raw Int values.
 *
 * @receiver The [DataFrameBuilder].
 * @param G The group type.
 * @param Y The y type.
 * @return The [DataFrameBuilder] for chaining.
 */
fun <G, Y> DataFrameBuilder<G, Int, Y>.xAsInt(): DataFrameBuilder<G, Int, Y> {
    dataFrame = dataFrame.add(getRawXData().toColumn(X_COL))
    return this
}

/**
 * Add a numeric x column from raw Number values by converting to Double.
 *
 * @receiver The [DataFrameBuilder].
 * @param G The group type.
 * @param X The x type (must be [Number]).
 * @param Y The y type.
 * @return The [DataFrameBuilder] for chaining.
 */
fun <G, X : Number, Y> DataFrameBuilder<G, X, Y>.xAsDouble(): DataFrameBuilder<G, X, Y> {
    dataFrame = dataFrame.add(getRawXData().map { it.toDouble() }.toColumn(X_COL))
    return this
}

/**
 * Decompose Summary<Y> into separate numeric columns used for box plots.
 *
 * @receiver The [DataFrameBuilder].
 * @param G The group type.
 * @param X The x type.
 * @param Y The numeric type within the summary.
 * @return The [DataFrameBuilder] for chaining.
 */
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
