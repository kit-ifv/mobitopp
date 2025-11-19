package domain.synthesis.behavior.householdgeneration

/**
 * This partition can only receive signatures, but never send them.
 */

class ReceiverPartition(
    partition: Partition,
    updater: AttributeUpdater,
    buckets: BucketList<Move>,
    val activeSignatures: BooleanArray,
    val maxGain: Int
) : TempPartition(
    partition, updater, buckets
) {
    private val movePotential: BucketList<Int> = BucketList(maxGain)
    init {
        expectedGains.withIndex().forEach { (index, gain) ->
            movePotential.insert(index, gain)
        }
    }

    override val myOutgoingMoves: Array<MutableSet<Move>> = Array(partition.signatures.size) {
        mutableSetOf()
    }



    fun best(): SignatureIndex {
        return SignatureIndex(movePotential.pollBest())
    }


    fun kill(signature : SignatureIndex) {
        movePotential.remove(signature.index)
    }


    fun wantsElements() = movePotential.currentGain() >= 0
    override fun delta(signature: SignatureIndex, amount: Int): List<Move> {
        require(amount > 0) {
            "Receiver partition can only gain elements but amount=$amount "
        }

        val sig = partition.signatures[signature.index]
        val dirtyIndices = sig.entries.filter { partition.getMask(it.key) }.flatMap { (k, factor) ->
            val currentDelta = partition.getDelta(k)
            val nextDelta = currentDelta - amount * factor
            updater.performUpdate(k, currentDelta, nextDelta, expectedGains)

        }
        // Dont bother updating elements that you will never see
        dirtyIndices.filter { activeSignatures[it] }.forEach {
            movePotential.update(it, expectedGains[it])
        }
        partition.delta(signature, amount)
        return emptyList()
    }

    override fun toString(): String {
        return "Receiver Partition ${partition.id}"
    }
}