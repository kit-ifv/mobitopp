package domain.synthesis.behavior.householdgeneration.refinement

import domain.synthesis.behavior.householdgeneration.Moved

class MoveRecalculator(
    val partitions: List<domain.synthesis.behavior.householdgeneration.TempPartition>,
    val maxGain: Int,
    val bestTargetTracker: domain.synthesis.behavior.householdgeneration.BestTargetTracker
) {

    fun recalculate(buckets: domain.synthesis.behavior.householdgeneration.BucketList<Moved>, dirtyMoves: Collection<Moved>) {
        if (dirtyMoves.isEmpty()) return

        dirtyMoves.forEach {
            buckets.remove(it)
        }

        dirtyMoves.filter { !it.isEmpty && !it.isLocked }.forEach {
            val sigIdx = it.signatureIndex.index
            if (bestTargetTracker.isBest(sigIdx, it.to)) {
            } else {
                val bestTarget: domain.synthesis.behavior.householdgeneration.TempPartition = bestTargetTracker.getRandom(
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
