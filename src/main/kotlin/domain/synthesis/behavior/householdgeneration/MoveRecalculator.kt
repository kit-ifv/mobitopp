package domain.synthesis.behavior.householdgeneration

class MoveRecalculator(
    val bestTargetTracker: BestTargetTracker
) {
    /**
     * Updates dirty moves by removing them from the bucketlist and readding them based on the gain. Checks in the
     * best target tracker what the best target partition is.
     */
    fun recalculate(buckets: BucketList<Move>, dirtyMoves: Collection<Move>) {
        if (dirtyMoves.isEmpty()) return

        dirtyMoves.forEach {
            buckets.remove(it)
        }

        dirtyMoves.filter { !it.isEmpty && !it.isLocked }.forEach {
            val sigIdx = it.signatureIndex.index
            if (!bestTargetTracker.isBest(sigIdx, it.to)) {
                val bestTarget: TempPartition = bestTargetTracker.getRandom(
                    sigIdx
                )
                it.to.myIncomingMoves[sigIdx].remove(it)
                it.to = bestTarget
                it.to.myIncomingMoves[sigIdx].add(it)
            }

            buckets.insert(it, it.gain)
        }
    }
}
