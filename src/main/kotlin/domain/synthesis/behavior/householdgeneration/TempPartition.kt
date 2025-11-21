package domain.synthesis.behavior.householdgeneration

import kotlin.math.min

open class TempPartition(
    val partition: Partition,
    val updater: AttributeUpdater,
    val buckets: BucketList<Move>,
) {

    val signatureTracker get() = partition.signatureTracker

    fun isNotEmpty() = partition.isNotEmpty()

    fun expectedAttributeSum() = partition.expectedSum()

    val id = partition.id

    override fun toString(): String {
        return "Temp Partition $id"
    }

    /**
     * The expected gain when receiving a signature with index i
     */
    protected val expectedGains = IntArray(partition.signatureTracker.size) {
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
    protected val expectedLosses = IntArray(partition.signatureTracker.size) {
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

    /**
     * THe moves. for each signature index i the partition keeps track of where it wants to send the signature index
     */
    open val myOutgoingMoves: Array<MutableSet<Move>> = Array<MutableSet<Move>>(partition.signatureTracker.size) {
        mutableSetOf(Moved(this, this, SignatureIndex(it)))
    }

    /**
     * Keeps track of the incoming moves targeting a signature index. Could be that multiple other partitions
     * want to send signature i to this parititon.
     */
    open val myIncomingMoves: Array<MutableSet<Move>> = Array(partition.signatureTracker.size) {
        mutableSetOf()
    }

    /**
     * Performs update immediately. Contracts that even after the delta the gains and losses are accurate.
     * Return a list of moves that are considered dirty after performing the move.
     */
    open fun delta(signature: SignatureIndex, amount: Int): List<Move> {
        require(partition.getCounts(signature.index) + amount >= 0) {
            "Thats a too large move, don't please"
        }
        hasBeenMoved[signature.index] = true

        val sig = partition.signatureTracker[signature.index]

        val dirtyIndices = sig.entries.filter { partition.getMask(it.key) }.flatMap { (k, factor) ->
            val currentDelta = partition.getDelta(k)
            val nextDelta = currentDelta - amount * factor
            val gainindices = updater.performUpdate(k, currentDelta, nextDelta, expectedGains)
            val lossindices = updater.performUpdate(k, -currentDelta, -nextDelta, expectedLosses)
            gainindices + lossindices
        }.toSet()
        // Perform actual move after recalculation of gains
        partition.delta(signature, amount)

        return dirtyIndices.flatMap {
            myOutgoingMoves[it]

        } + dirtyIndices.flatMap { myIncomingMoves[it] }
    }

    fun verifyAll(): Boolean {
        for (i in partition.signatureTracker.indices) {
            val t = verifyInternal(i)
            if (!t) {
                return false
            }
        }
        return true
    }

    fun verify(signature: SignatureIndex) = verifyInternal(signature.index)
    fun calculateAll(): List<Int> {
        return partition.signatureTracker.indices.map {
            calculateInternal(it)
        }
    }

    fun calculateInternal(sigIdx: Int): Int {
        val sig = partition.signatureTracker[sigIdx]
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
        val sig = partition.signatureTracker[signature.index]
    }
    // TODO this array is never used.
    private val hasBeenMoved: BooleanArray = BooleanArray(partition.signatureTracker.size) {
        false
    }

    fun reset() {
        for (i in hasBeenMoved.indices) {
            hasBeenMoved[i] = false
        }

        myOutgoingMoves.forEach {
            it.forEach {
                it.isLocked = false
            }

        }
    }

    /**
     * Return the amount of a signature in this partition.
     * Read it as: How many elements of the signatureIndex are present in the partition.
     */
    fun amount(signature: SignatureIndex): Int {
        return partition.amount(signature)
    }

    fun add(signature: SignatureIndex, amount: Int) {
        delta(signature, amount)
    }

    fun remove(signature: SignatureIndex, amount: Int) {
        delta(signature, -amount)
    }

    /**
     * Initializes the moves based on the best target partition from the target tracker.
     */
    open fun initialize(bestTargetTracker: BestTargetTracker) {
        for (i in partition.signatureTracker.indices) {
            if (this.partition.amount(SignatureIndex(i)) < 1) continue
            val targetPartition = bestTargetTracker.getRandom(i)

            myOutgoingMoves[i].apply {
                val move = first()
                move.apply {
                    to = targetPartition
                    targetPartition.myIncomingMoves[i].add(this)
                    buckets.insert(this, gain)
                }

            }
        }
    }


}

