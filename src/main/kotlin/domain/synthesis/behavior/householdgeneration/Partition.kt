package domain.synthesis.behavior.householdgeneration

import domain.synthesis.Signature
import org.jetbrains.annotations.TestOnly
import utils.Metric
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class PotentialTransfer(
    val signatureIndex: SignatureIndex,
    val impact: Int,
    var amountOfElements: Int,
) {
    fun isEmpty(): Boolean = amountOfElements <= 0
}

@Suppress("TooManyFunctions")
class Partition(
    private val expectedArray: IntArray,
    val signatureTracker: SignatureTracker,
    private val mask: BooleanArray = BooleanArray(expectedArray.size) { true },
) {
    private val _actual: IntArray = IntArray(expectedArray.size) { 0 }
    private val _counts: IntArray = IntArray(signatureTracker.size) { 0 }

    val attributeIndices = expectedArray.indices
    val attributeSize = expectedArray.size

    fun expectedSum() = expectedArray.sum()
    fun getExpected(index: Int) = expectedArray[index]
    fun getActual(index: Int) = _actual[index]
    fun getCounts(index: Int) = _counts[index]
    fun getMask(index: Int) = mask[index]

    /**
     * Delta represents how much of the change is needed to satisfy the attribute at the target index.
     * This means that a positive delta means that thie attribute would like to receive more items, and a negative
     * delta means that elements want to be removed.
     */
    fun getDelta(index: Int) = expectedArray[index] - _actual[index]
    private val expected get() = expectedArray.toList()
    private val actual get() = _actual.toList()
    val countsList get() = _counts.toList()

    val error: Double = Double.MAX_VALUE
    fun isEmpty() = actual.all { it == 0 }
    fun isNotEmpty() = actual.any{it != 0}
    fun getExpecteds(signature: Signature) = signature.keys.map { getExpected(it) }
    fun getActuals(signature: Signature) = signature.keys.map { getActual(it) }
    fun verify() {
        val attributeCount = mutableMapOf<Int, Int>()
        _counts.zip(signatureTracker.signatures).map { (amount, signature) ->
            signature.entries.forEach {
                val currentValue = attributeCount.getOrPut(it.key) { 0 }
                attributeCount[it.key] = currentValue + amount * it.value
            }
        }
        val actualsMatch = _actual
            .zip(attributeCount.toSortedMap().values)
            .withIndex()
            .all { (i, element) ->
                val (a, b) = element
                a == b || !mask[i]
            }

        require(actualsMatch) {
            "The counts and actual things do not match"
        }
    }
    fun calculateGain(signature: Signature): Int {
        return signature.entries.sumOf { (idx, factor) ->
            val delta = max(getDelta(idx), 0)
            -factor + min(factor, delta) * 2
        }
    }

    fun calculateLoss(signature: Signature): Int {
        return signature.entries.sumOf { (idx, factor) ->
            val delta = max(-getDelta(idx), 0)
            -factor + min(factor, delta) * 2
        }
    }
    fun count(signature: Signature): Int {
        return _counts[signatureTracker.findSignatureIndex(signature).index]
    }

    fun output(): List<SignatureAmount> {
        return signatureTracker.signatures.zip(_counts.toList()).map {
            SignatureAmount(it.first, it.second)
        }
    }

    /**
     * Return a collection of the errors found in this partition.
     */
    fun errors() = expectedArray.zip(_actual).map { (expected, actual) -> expected - actual }
    fun absoluteErrors() = expectedArray.zip(_actual).map { (expected, actual) -> abs(expected - actual) }
    fun relativeErrors() = expectedArray.zip(
        _actual
    ).map { (expected, actual) -> abs(expected - actual).toDouble() / max(expected, 1) }

    /**
     * Returns the delta of comparing the actual amount of an attribute index in comparison to the
     * expected value. Positive if there are too many elements and negative if there are too few.
     */
    fun errorFor(index: Int) = _actual[index] - expectedArray[index]
    fun worstIndex() = absoluteErrors().withIndex().maxBy { it.value }.index

    fun worstRelativeIndex() = relativeErrors().withIndex().maxBy { it.value }.index

    /**
     * Get the count of indices that currently populate a target attribute in the partition
     */
    fun currentElementsForAttribute(attrIdx: Int): List<PotentialTransfer> {
        return signatureTracker.getSetByAttributeIndex(attrIdx).map { sigIdx ->
            PotentialTransfer(
                SignatureIndex(sigIdx),
                signatureTracker[sigIdx][attrIdx]!!,
                _counts[sigIdx]
            )
        }.filter { !it.isEmpty() }
    }

    @Deprecated("use Delta")
    fun receive(signature: SignatureIndex, amount: Int) {
        delta(signature, amount)
    }

    fun error() = Metric.meanAbsolutePercentError.evaluateNumber(expectedArray.toList(), _actual.toList())
    fun evaluateMetric(sigIdx: Int, function: PartitionMetric): Double {
        return function.calculate(expectedArray, _actual, resolve(sigIdx))
    }

    /**
     * Figyre out how much of a given signature can be inserted or extracted from this partition
     * until either metric switches context.
     */
    fun untilFlagChange(sigIdx: Int, searchDirection: Int): Int {
        val sig = signatureTracker[sigIdx]
        val minAmountMoves = sig.entries.minOf { (k, v) ->
            // positive diff means i need that signature, but go on
            val diff = (expectedArray[k] - _actual[k]) * searchDirection

            val intDiv = diff / v
            if (intDiv >= 0) intDiv else Int.MAX_VALUE
        }
        return max(1, minAmountMoves)
    }

    fun indexDelta(sigIdx: Int, amount: Int) {
        _counts[sigIdx] += amount
        updateActual(sigIdx, amount)
    }

    @TestOnly
    fun delta(signature: Signature, amount: Int) {
        delta(signatureTracker.findSignatureIndex(signature), amount)
    }

    fun transferTo(to: Partition, signatureIndex: SignatureIndex, amount: Int) {
        require(this._counts[signatureIndex.index] >= amount) {
            "The requested transfer is too large"
        }
        require(amount >= 0) {
            "Cannot handle negative transfers yet"
        }
        delta(signatureIndex, -amount)
        to.delta(signatureIndex, amount)
    }

    fun delta(signature: SignatureIndex, amount: Int) {
        _counts[signature.index] += amount
        updateActual(signature.index, amount)
    }

    private val _signatures = signatureTracker.signatures.map { signature ->
        signature.filterKeys { mask[it] }
    }.toTypedArray()
    private fun resolve(sigIdx: Int): Signature = _signatures[sigIdx]
    private fun updateActual(sigIndex: Int, delta: Int) {
        val signature = resolve(sigIndex)
        signature.filter { mask[it.key] }.forEach { (k, v) ->
            _actual[k] += v * delta
        }
    }

    fun amount(signatureIndex: SignatureIndex): Int = _counts[signatureIndex.index]
    val id = counter
    companion object {
        @OptIn(ExperimentalAtomicApi::class)
        private var _counter: AtomicInt = AtomicInt(0)

        @OptIn(ExperimentalAtomicApi::class)
        val counter get() = _counter.fetchAndIncrement()
    }

    override fun toString(): String {
        return "Partition $id : actual=$actual errors=${relativeErrors()}"
    }
}

fun Collection<Partition>.averagePercentError(): Double {
    return map { it.error() }.average()
}
