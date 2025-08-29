package core.datastructure.matrix

import domain.shared.location.CostMetric
import domain.shared.location.DistanceMetric
import domain.shared.location.DurationMetric
import domain.shared.location.Location
import domain.shared.location.ZoneId
import units.Currency
import units.Distance
import kotlin.time.Duration

/**
 * A 2-D wrapper around a one dimensional array to simplify (x, y) access while keeping contiguous memory.
 */
class DoubleMatrix(private val values: DoubleArray, private val numColumns: Int) {
    operator fun get(row: Int, column: Int): Double {
        val index = row * numColumns + column
        return values[index]
    }
}


fun interface Indexer<I> {
    fun toIndex(indexableElement: I): Int
}
/**
 * A double-valued origin-destination matrix with generic row/column indices.
 *
 * Internally, indices of type [I] are converted to integers via an [Indexer].
 * The underlying matrix is stored as a [DoubleMatrix].
 *
 * @param I the type of row/column indices (e.g. ZoneId, LocationId).
 */
interface IndexedDoubleMatrix<I> {

    val matrix: DoubleMatrix
    val converter: Indexer<I>
    operator fun get(row: I, column: I): Double {
        val rowIndex = converter.toIndex(row)
        val columnIndex = converter.toIndex(column)
        return matrix[rowIndex, columnIndex]
    }
}

/**
 * A specialization of [IndexedDoubleMatrix] for zone-based indices.
 *
 * Provides efficient lookups by [ZoneId] (unboxed) and convenience
 * access by [Location], which is mapped to its enclosing [ZoneId].
 */
interface ZoneIdMatrix : IndexedDoubleMatrix<ZoneId> {
    override operator fun get(row: ZoneId, column: ZoneId): Double

    operator fun get(row: Location, column: Location): Double {
        return get(row.requireZone().id, column.requireZone().id)
    }
}

fun IndexedDoubleMatrix<ZoneId>.toConcreteMatrix(): ZoneIdMatrix = object: ZoneIdMatrix {
    override fun get(row: ZoneId, column: ZoneId): Double {
        return this@toConcreteMatrix[row, column]
    }

    override val matrix: DoubleMatrix =         this@toConcreteMatrix.matrix
    override val converter: Indexer<ZoneId> = this@toConcreteMatrix.converter
}

fun interface DoubleToDuration {
    fun from(x: Double): Duration
}
class DurationMatrix(
    private val translatedMatrix: ZoneIdMatrix,
    private val converter: DoubleToDuration,
) :
    DurationMetric {
    operator fun get(row: ZoneId, column: ZoneId): Duration {
        return converter.from(translatedMatrix[row, column])
    }

    override fun evaluate(
        origin: Location,
        destination: Location,
    ): Duration {
        return this[origin.requireZone().id, destination.requireZone().id]
    }
}

fun interface DoubleToCurrency {
    fun from(x: Double): Currency
}

class CurrencyMatrix(
    private val translatedMatrix: ZoneIdMatrix,
    private val converter: DoubleToCurrency,
) : CostMetric {
    operator fun get(row: ZoneId, column: ZoneId): Currency {
        return converter.from(translatedMatrix[row, column])
    }

    override fun evaluate(
        origin: Location,
        destination: Location,
    ): Currency {
        return this[origin.requireZone().id, destination.requireZone().id]
    }
}



class DistanceMatrix(
    private val translatedMatrix: ZoneIdMatrix,
    private val converter: (Double) -> Distance,
) : DistanceMetric {
    operator fun get(row: ZoneId, column: ZoneId): Distance {
        return converter(translatedMatrix[row, column])
    }

    override fun evaluate(
        origin: Location,
        destination: Location,
    ): Distance {
        return this[origin.requireZone().id, destination.requireZone().id]
    }
}


class ConstantMatrix<I, O>(val value: O) : IndexedDoubleMatrix<I> {
    override val matrix: DoubleMatrix
        get() = TODO("Not yet implemented")
    override val converter: Indexer<I> get()= TODO()
}

class ConstantZoneIdMatrix(val value: Double): ZoneIdMatrix {
    override fun get(row: ZoneId, column: ZoneId): Double {
        return value
    }

    override val matrix: DoubleMatrix
        get() = TODO("Not yet implemented")
    override val converter: Indexer<ZoneId>
        get() = TODO("Not yet implemented")
}


// TODO maybe apply the converter to the elements of the matrix directly,
//  unless this would waste storage space when <O> is complex -> alternative version: ArrayMatrix
class FloatMatrix<I>(
    val size: Int,
    val translation: Map<I, Int>,
    val floatArray: FloatArray,


    ) : IndexedDoubleMatrix<I> {
    override val matrix: DoubleMatrix = TODO()
    override val converter: Indexer<I> = TODO()
    override fun get(row: I, column: I): Double {
        val rowIndex =
            translation[row] ?: throw IllegalArgumentException("Row $row not found in index lookup")
        val columnIndex =
            translation[column] ?: throw IllegalArgumentException("Column $column not found in index lookup")

        val index = rowIndex * size + columnIndex

        return floatArray[index].toDouble()
    }
}
