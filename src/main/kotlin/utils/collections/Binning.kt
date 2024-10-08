package utils.collections

/**
 * Maps the elements of the [Iterable] to a label representing one of the
 * [n] equal sized bins between [min] and [max].
 *
 * @param T the generic type of [Comparable] elements to be mapped
 * @param min the lower bound (inclusive) of the first bin
 * @param max the upper bound (exclusive) of the last bin
 * @param n the number us bins
 * @receiver an [Iterable] containing the elements to be mapped to bins
 *
 * @return a list of respective bin labels, one for each element in the provided order of the [Iterable]
 */
fun <T> T.toEqualSizedBins(
    min: Double,
    max: Double,
    n: Int,
): Bin<Double> where T : Number {
    require(n > 0) { "Number of intervals must be greater than zero" }

    val intervalSize = (max - min) / n

    return this.toBins(min, max, intervalSize)
}

/**
 * Maps the elements of the [Iterable] to a label representing one of the bins between [min] and [max].
 * All bins, except for the last one, have the specified [binSize].
 * If the range [[min], [max]] is not exactly divisible by [binSize], the last bin is cut off at [max].
 *
 * @param T the generic type of [Comparable] elements to be mapped
 * @param min the lower bound (inclusive) of the first bin
 * @param max the upper bound (exclusive) of the last bin
 * @param binSize the desired size of the bins
 * @param epsDiff an epsilon to account for rounding errors when checking whether the [max] bound is reached.
 *                Defaults to 0.1% of the [binSize].
 * @receiver an [Iterable] containing the elements to be mapped to bins
 *
 * @return a list of respective bin labels, one for each element in the provided order of the [Iterable]
 */
@Suppress("MagicNumber")
fun <T> T.toBins(
    min: Double,
    max: Double,
    binSize: Double,
    epsDiff: Double = binSize * 0.0001,
): Bin<Double> where T : Number {
    require(binSize > 0.0) { "Bin size must be greater than zero" }
    require(max > min) { "Maximum must be greater than minimum" }

    val threshold = max - epsDiff

    val bins = generateSequence(min) { prev ->
        (prev + binSize).takeIf { it < threshold }
    }.map {
        Bin(lower = it, upper = (it + binSize).coerceAtMost(max))
    }.toSet()

    return this.toDouble().mapToBins(bins)
}

open class Bin<T>(val lower: T, val upper: T) : Comparable<Bin<T>> where T : Comparable<T> {

    init {
        require(lower <= upper) { "The bins upper bound must not be lower than its lower bound: $lower <= $upper" }
    }

    fun contains(value: T): Boolean = lower <= value && value < upper

    fun toPair() = lower to upper

    override fun compareTo(other: Bin<T>) = Comparator.comparing {
            b: Bin<T> ->
        b.lower
    }.thenComparing {
            b: Bin<T> ->
        b.upper
    }.compare(this, other)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bin<*>

        if (lower != other.lower) return false
        return upper == other.upper
    }

    override fun hashCode(): Int {
        var result = lower.hashCode()
        result = 31 * result + upper.hashCode()
        return result
    }

    override fun toString() = "[$lower, $upper)"
}

class LabeledBin<T>(
    lower: T,
    upper: T,
    private val toLabel: (T, T) -> String,
) : Bin<T>(lower, upper) where T : Comparable<T> {

    override fun toString(): String = toLabel(lower, upper)
}

fun <B, T> B.addLabel(toLabel: (T, T) -> String) where B : Bin<T>, T : Comparable<T> =
    LabeledBin(lower, upper, toLabel)

fun <C, T> C.asBins() where C : Collection<Pair<T, T>>, T : Comparable<T> = map {
    Bin(it.first, it.second)
}.distinct().sorted()

/**
 * Map to bins
 *
 * @param T
 * @param bins
 * @return
 */
fun <T> T.mapToBins(
    bins: Collection<Bin<T>>
): Bin<T> where T : Comparable<T> {
    require(assertNonOverlapping(bins))
    return bins.getBinOf(this)
}

/**
 * Get bin of
 *
 * @param T
 * @param value
 * @return
 */
fun <T> Iterable<Bin<T>>.getBinOf(value: T): Bin<T> where T : Comparable<T> =
    this.find { it.contains(value) }
        ?: error("No bin [a,b) found s.t. a <= $value < b: " + this.joinToString())

private fun <T> assertNonOverlapping(
    bins: Collection<Bin<T>>
): Boolean where T : Comparable<T> = bins.distinct().sorted().zipWithNext().all { (bin1, bin2) ->
    bin1.upper <= bin2.lower
}
