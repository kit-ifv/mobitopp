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
fun <T> T.toEqualSizedBins(min: Double, max: Double, n: Int): Bin<Double> where T : Number {
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
        BaseBin(lower = it, upper = (it + binSize).coerceAtMost(max))
    }.toSet()

    return this.toDouble().mapToBins(bins)
}

interface Bin<T> : Comparable<Bin<T>> where T : Comparable<T> {
    val lower: T
    val upper: T

    operator fun contains(value: T): Boolean

    fun toPair(): Pair<T, T> = lower to upper

    override fun compareTo(other: Bin<T>) = Comparator.comparing { b: Bin<T> ->
        b.lower
    }.thenComparing { b: Bin<T> ->
        b.upper
    }.compare(this, other)
}

class BaseBin<T>(override val lower: T, override val upper: T) : Bin<T> where T : Comparable<T> {

    init {
        require(lower <= upper) { "The bins upper bound must not be lower than its lower bound: $lower <= $upper" }
    }

    override fun contains(value: T): Boolean = lower <= value && value < upper

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

class OpenBin<T>(override val lower: T) : Bin<T> where T : Comparable<T> {
    override val upper: T
        get() = lower

    override fun contains(value: T): Boolean = value >= lower

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

    override fun toString() = "$lower+"
}

fun <C, T> C.asBins(appendOpenBin: Boolean = false) where C : Collection<Pair<T, T>>, T : Comparable<T> = map {
    BaseBin(it.first, it.second)
}.distinct().sorted().let {
    if (appendOpenBin) {
        it + listOf(OpenBin(it.last().upper))
    } else {
        it
    }
}

fun <S, T> List<Bin<S>>.mapBounds(transform: (S) -> T): List<Bin<T>> where T : Comparable<T>, S : Comparable<S> = map {
    BaseBin(transform(it.lower), transform(it.upper))
}

/**
 * Map to bins
 *
 * @param T
 * @param bins
 * @return
 */
fun <T> T.mapToBins(bins: Collection<Bin<T>>): Bin<T> where T : Comparable<T> {
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
fun <T> Iterable<Bin<T>>.getBinOf(value: T): Bin<T> where T : Comparable<T> = this.find { it.contains(value) }
    ?: error("No bin [a,b) found s.t. a <= $value < b: " + this.joinToString())

private fun <T> assertNonOverlapping(bins: Collection<Bin<T>>): Boolean where T : Comparable<T> =
    bins.distinct().sorted().zipWithNext().all { (bin1, bin2) ->
        bin1.upper <= bin2.lower
    }
