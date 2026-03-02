package domain.synthesis.behavior.householdgeneration

/**
 * This partition can only send signatures, but never receive them. It will try to send elements to each partition,
 * not just the best partition.
 */
class SenderPartition(
    partition: Partition,
    updater: AttributeUpdater,
    buckets: BucketList<Move>,
) : TempPartition(
    partition,
    updater,
    buckets
) {
    override val myIncomingMoves: Array<MutableSet<Move>> = emptyArray()

    override val myOutgoingMoves: Array<MutableSet<Move>> = emptyArray()

    override fun initialize(bestTargetTracker: BestTargetTracker) {
        for (i in partition.signatureTracker.indices) {
            if (this.partition.amount(SignatureIndex(i)) < 1) continue

            bestTargetTracker.allPartitions.forEach {
                val move = SymmetricalMoved(this, it, SignatureIndex(i))
                myOutgoingMoves[i].add(move)
                it.myIncomingMoves[i].add(move)
                buckets.insert(move, move.gain)
            }
        }
    }

    val activeSignatures: BooleanArray = BooleanArray(partition.signatureTracker.size) {
        true
    }
    fun emptyElements() = pairs {
        it.value == 0
    }

    fun remainingElements() = pairs {
        it.value > 0
    }

    private fun pairs(predicate: (IndexedValue<Int>) -> Boolean): List<Pair<SignatureIndex, Int>> = partition.countsList
        .withIndex()
        .filter(predicate)
        .map { SignatureIndex(it.index) to it.value }

    @Suppress("NotImplementedDeclaration")
    override fun delta(signature: SignatureIndex, amount: Int): List<Move> {
        TODO()
    }

    fun operativeDelta(signature: SignatureIndex, amount: Int): SignatureIndex? {
        require(amount < 0) {
            "Sender Partition can only send elements. but amount=$amount"
        }
        require(-amount <= amount(signature)) {
            "Wants to move $amount but I only have ${amount(signature)}"
        }

        partition.delta(signature, amount)
        if (amount(signature) == 0) {
            activeSignatures[signature.index] = false
            return signature
        }
        return null
    }

    override fun toString(): String {
        return "Sender Partition ${partition.id}"
    }
}
