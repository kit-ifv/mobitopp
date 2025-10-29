package domain.synthesis.behavior.householdgeneration

import domain.synthesis.Signature
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.householdgeneration.refinement.Refinement
import domain.synthesis.behavior.householdgeneration.refinement.SignatureTracker
import kotlin.math.abs
import kotlin.math.min

data class NewAlgorithmConfig(
    val ipu: GenericIPU = GenericIPU.legacy,
    val refinement: Refinement = Refinement { },

)

fun prepSignatures(signatures: SignatureTracker, attributeSize: Int): AttributeUpdater {
    val updaters = (0..<attributeSize).map { attrIdx ->
        // Get the maximum value that any signature has for a given attribute index
        val maxSigContent = signatures.highestAttributeForIndex(attrIdx)

        require(maxSigContent > 0) {
            "No signature has a value greater than 0 for a given attribute, are you certain you want to continue?"
        }

        val relevantSigIndices = signatures.getByAttributeIndex(attrIdx)

        val updater =
            AttributeUpdates(
                relevantSigIndices.toIntArray(),
                (0..maxSigContent).toList().toIntArray(),
            )

        val relevantSigs = signatures.getByAttribute(attrIdx)
        val contentNums = relevantSigs.map {
            it[attrIdx]
                ?: throw IllegalStateException("A relevant sig must have a value greater than 0 for the target attribute")
        }

        for (j in 0..maxSigContent) {
            updater.updateArrays[j] = contentNums.map { min(-it + 2 * j, it) }.toIntArray()
        }
        updater
    }

    return AttributeUpdater(updaters.toTypedArray(), signatures.largestDelta)
}

class AttributeUpdater(
    val internalUpdaters: Array<AttributeUpdates>,
    private val maxGain: Int
) {
    var counter = 0

    // Return true if any of the sigs changed, because in this scenario the gains of certain moves need to be recalculated.
    fun performUpdate(attributeIndex: Int, originalDiff: Int, newDiff: Int, target: IntArray): List<Int> {
        counter++
        val updater = internalUpdaters[attributeIndex]

        val origA = updater.findArrayFor(originalDiff)
        val newA = updater.findArrayFor(newDiff)

        if (origA === newA) return emptyList()
        val sigs = updater.updateSigIndices
        for (i in sigs.indices) {
            target[sigs[i]] += newA[i] - origA[i]

            if (abs(target[sigs[i]]) > maxGain) {
                throw IllegalStateException("There should never be an update so that the maxgain is exceeded.")
            }
        }
        return sigs.toList()
    }

    fun getCurrent(attributeIndex: Int, currentDiff: Int): Pair<IntArray, IntArray> {
        val updates = internalUpdaters[attributeIndex]
        return updates.updateSigIndices to updates.findArrayFor(currentDiff)
    }
}

data class AttributeUpdates(
    val updateSigIndices: IntArray,
    val updateTriggerPoints: IntArray,
) {

    val updateArrays: Array<IntArray> = Array(updateTriggerPoints.size) { IntArray(updateSigIndices.size) }

    fun findArrayFor(diff: Int): IntArray {
        val idx = updateTriggerPoints.binarySearch(diff)
        return if (idx >= 0) {
            updateArrays[idx]
        } else {
            // We know that this case can only occur when the requested value is outside the possible trigger points,
            // because the trigger points are contiguous integers.

            if (diff <= 0) updateArrays.first() else updateArrays.last()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttributeUpdates

        if (!updateSigIndices.contentEquals(other.updateSigIndices)) return false

        return true
    }

    override fun hashCode(): Int {
        return updateSigIndices.contentHashCode()
    }
}

fun <T> List<Rule<ISurveyHousehold<out T>>>.toSignature(household: ISurveyHousehold<out T>): Signature {
    return withIndex().map { (index, rule) ->
        index to rule.evaluate(household)
    }.filter { it.second != 0 }.toMap()
}

fun <T, H : MinimalistHousehold<out T>> List<NamedCountRule<H>>.toSignatureNamed(household: H): Signature {
    return withIndex().map { (index, rule) ->
        index to rule.matches(household)
    }.filter { it.second != 0 }.toMap()
}

data class SignatureAmount(
    val signature: Signature,
    val amount: Int,
) {
    fun toMutable(index: Int): MutableSignatureAmount {
        return MutableSignatureAmount(signature, amount, SignatureIndex(index))
    }
}

data class MutableSignatureAmount(
    val signature: Signature,
    var amount: Int,
    val index: SignatureIndex,
)

fun interface InitialSignatureDistributor {
    fun distribute(partitions: List<Partition>, signatureAmounts: Collection<SignatureAmount>)
}

fun interface PartitionMetric {
    fun calculate(expected: IntArray, actual: IntArray, signature: Signature): Double
}

val SquaredDiff = PartitionMetric { expected, actual, signature ->
    var origDiff = .0
    var newDiff = .0
    signature.entries.forEach { (k, v) ->
        val exp = expected[k]
        val act = actual[k]
        val denom = if (exp == 0) 1e-9 else exp.toDouble() // avoid div by 0
        val delta = v
        val relErrorOrig = (exp - act) / denom
        val relErrorNew = (exp - (act + delta)) / denom

        origDiff += relErrorOrig * relErrorOrig
        newDiff += relErrorNew * relErrorNew
    }

    origDiff - newDiff
}

class GreedyAmountDistro(
    val insertionMetric: PartitionMetric = SquaredDiff,
) : InitialSignatureDistributor {
    override fun distribute(partitions: List<Partition>, signatureAmounts: Collection<SignatureAmount>) {
        val elements = signatureAmounts.withIndex().map { it.value.toMutable(it.index) }.toMutableList()
        elements.removeAll { it.amount == 0 } // Don't need to bother evaluating signatures that should not be placed (also causes buggy behaviour if left untouched)
        elements.assignGreedy(partitions)
        elements.assignEmergency(partitions)
    }

    fun MutableList<MutableSignatureAmount>.assignGreedy(partitions: List<Partition>) {
        var i = 0
        var improvementFound = true
        val removeablePartitions = partitions.toMutableList()
        while (isNotEmpty()) {
            if (removeablePartitions.isEmpty()) {
                // If there is no partition left where elements could be added we need to exit the greedy assignment strategy
                return
            }
            if (i % 10000 == 0) {
//                println("Working on household $i")
            }
            val loopedIndex = i % removeablePartitions.size // Iterate through the active partitions.
            if (loopedIndex == 0) {
                // If one cycle of assignments has found no change, then no partition wants to take in the remaining elements
                // In this instance, the assignment strategy should also be terminated, and the remaining elements be assigned
                // via whatever strategy is used for that.
                if (!improvementFound) return
                improvementFound = false
            }
            val currentPartition = removeablePartitions[loopedIndex]
            val bestElement = maxBy { currentPartition.evaluateMetric(it.index.index, insertionMetric) }
            val potentialElement =
                if (currentPartition.evaluateMetric(bestElement.index.index, insertionMetric) < 0) null else bestElement
            potentialElement?.let {
                currentPartition.receive(it.index, 1)
                it.amount--
                i++
                improvementFound = true
                if (it.amount <= 0) {
                    remove(it)
                }
            } ?: run {
                // Emergency, if the 0 indexed element is removed, the other elements may still gain benefits and we should not terminate early
                if (loopedIndex == 0) improvementFound = true
                removeablePartitions.remove(currentPartition)
            }
        }
    }

    fun MutableList<MutableSignatureAmount>.assignEmergency(regions: List<Partition>) {
        var i = 0
        while (isNotEmpty()) {
            if (i % 10000 == 0) {
//                println("Emergency on household $i")
            }
            val current = first()
            val bestRegion = regions.maxBy { it.evaluateMetric(current.index.index, insertionMetric) }
            bestRegion.receive(current.index, 1)
            current.amount--
            if (current.amount <= 0) {
                remove(current)
            }
            i++
        }
    }
}
