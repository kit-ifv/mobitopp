package domain.synthesis.behavior.householdgeneration

class MoveRecalculator(
    val partitions: List<TempPartition>,
    val maxGain: Int,
    val bestTargetTracker: BestTargetTracker
) {

    fun recalculate(buckets: BucketList<Moved>, dirtyMoves: Collection<Moved>) {
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
