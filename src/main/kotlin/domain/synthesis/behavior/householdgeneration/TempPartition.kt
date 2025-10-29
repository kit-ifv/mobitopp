package domain.synthesis.behavior.householdgeneration

import kotlin.math.min

class TempPartition(
    val partition: Partition,
    val updater: AttributeUpdater,
    val buckets: BucketList<Moved>,
) {

    val signatureTracker get() = partition.signatures

    companion object {
        private var counter = 0
            get() = field++
    }

    val id = counter

    override fun toString(): String {
        return "Temp Partition $id"
    }

    /**
     * The expected gain when receiving a signature with index i
     */
    private val expectedGains = IntArray(partition.signatures.size) {
        0
    }

    fun getGain(index: Int): Int {
        return expectedGains[index]
    }

    fun untilFlagChange(sigIdx: Int, searchDirection: Int) = partition.untilFlagChange(sigIdx, searchDirection)

    /**
     * The expected loss when removing the signature from the partition. Note that this
     * is not identical to -gain because attributes may differ by given numbers and needs to be calculate4d
     * individually.
     */
    private val expectedLosses = IntArray(partition.signatures.size) {
        0
    }

    fun getLoss(index: Int): Int {
        return expectedLosses[index]
    }

    init {
        for (i in partition.attributeIndices) {
            val diff = partition.getDelta(
                i
            ) // So if diff is positive, I would like to gain elements with that attribute

            // Positive diff, means that the gain table wants to be influenced positively
            val (updateIdx, change) = updater.getCurrent(i, diff)
            for (j in change.indices) {
                expectedGains[updateIdx[j]] += change[j]
            }

            val (updateIdx2, change2) = updater.getCurrent(i, -diff)
            for (j in change2.indices) {
                expectedLosses[updateIdx[j]] += change2[j]
            }
        }
        partition.verify()
    }

    fun error() = partition.error()
    val myOutgoingMoves: Array<Moved> = Array<Moved>(partition.signatures.size) {
        Moved(this, this, SignatureIndex(it))
    }

    val myIncomingMoves: Array<MutableSet<Moved>> = Array(partition.signatures.size) {
        mutableSetOf()
    }

    /**
     * Performs update immediately. Contracts that even after the delta the gains and losses are accurate
     */
    fun delta(signature: SignatureIndex, amount: Int): List<Moved> {
        require(partition.getCounts(signature.index) + amount >= 0) {
            "Thats a too large move, don't please"
        }
        hasBeenMoved[signature.index] = true

        val sig = partition.signatures[signature.index]

        val dirtyIndices = sig.entries.filter { partition.getMask(it.key) }.flatMap { (k, factor) ->
            val currentDelta = partition.getDelta(k)
            val nextDelta = currentDelta - amount * factor
//            println("Perform update on Partition $id for key $k current delta $currentDelta $nextDelta")
            val gainindices = updater.performUpdate(k, currentDelta, nextDelta, expectedGains)
            val lossindices = updater.performUpdate(k, -currentDelta, -nextDelta, expectedLosses)
            gainindices + lossindices
        }.toSet()
        // Perform actual move after recalculation of gains
        partition.delta(signature, amount)

        return dirtyIndices.map { myOutgoingMoves[it] } + dirtyIndices.flatMap { myIncomingMoves[it] }
    }

    fun verifyAll(): Boolean {
        for (i in partition.signatures.indices) {
            val t = verifyInternal(i)
            if (!t) {
                return false
            }
        }
        return true
    }

    fun verify(signature: SignatureIndex) = verifyInternal(signature.index)
    fun calculateAll(): List<Int> {
        return partition.signatures.indices.map {
            calculateInternal(it)
        }
    }

    fun calculateInternal(sigIdx: Int): Int {
        val sig = partition.signatures[sigIdx]
        val trgt = sig.entries.sumOf { (key, value) ->
            val currentDiff = partition.getDelta(key)
            min(value, -value + 2 * currentDiff.coerceAtLeast(0))
        }
        return trgt
    }

    fun verifyInternal(sigIdx: Int): Boolean {
        val trgt = calculateInternal(sigIdx)
        return trgt == expectedGains[sigIdx]
    }

    fun updateGains(signature: SignatureIndex) {
        val sig = partition.signatures[signature.index]
    }

    val hasBeenMoved: BooleanArray = BooleanArray(partition.signatures.size) {
        false
    }

    fun reset() {
        for (i in hasBeenMoved.indices) {
            hasBeenMoved[i] = false
        }

        myOutgoingMoves.forEach {
            it.isLocked = false
        }
    }

    fun updateDirtyMoves() {
    }

    fun amount(signature: SignatureIndex): Int {
        return partition.amount(signature)
    }

    fun add(signature: SignatureIndex, amount: Int) {
    }

    fun remove(signature: SignatureIndex, amount: Int) {
    }

    fun initialize(bestTargetTracker: BestTargetTracker) {
        for (i in partition.signatures.indices) {
            if (this.partition.amount(SignatureIndex(i)) < 1) continue
            val targetPartition = bestTargetTracker.getRandom(i)

            myOutgoingMoves[i].apply {
                to = targetPartition
                targetPartition.myIncomingMoves[i].add(this)
                buckets.insert(this, gain)
            }
        }
    }
}
