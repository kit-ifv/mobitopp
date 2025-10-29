package domain.synthesis.behavior.householdgeneration.refinement

import domain.synthesis.behavior.householdgeneration.Moved
import domain.synthesis.behavior.householdgeneration.prepSignatures
import kotlin.time.measureTime

class FMRun(
    val amountOfPasses: Int = 100,
    val amountStrategy: (Moved) -> Int,
) : Refinement {

    fun refreshRound(partitions: List<domain.synthesis.behavior.householdgeneration.TempPartition>, buckets: domain.synthesis.behavior.householdgeneration.BucketList<Moved>, bestTargetTracker: domain.synthesis.behavior.householdgeneration.BestTargetTracker) {
        buckets.clear()

        partitions.forEach {
            it.reset()
        }
        partitions.forEach {
            it.initialize(bestTargetTracker)
        }
    }

    var i = 0
    fun runIteration(
        buckets: domain.synthesis.behavior.householdgeneration.BucketList<Moved>,
        recalculator: MoveRecalculator,
        bestTargetTracker: domain.synthesis.behavior.householdgeneration.BestTargetTracker
    ) {
        while (true) {
            val (element, gain) = buckets.popBest() ?: break
            require(!element.isLocked) {
                "Element should not be locked."
            }

            require(element.gain == gain) {
                "Mismatch in gain of element and associated bucket element=${element.gain} bucket=$gain"
            }

            if (gain <= 0) {
                break
            }

            val dirtyMoves = element.performMove(amountStrategy(element))
            buckets.remove(element)

            bestTargetTracker.update(element.from)
            bestTargetTracker.update(element.to)

            recalculator.recalculate(buckets, dirtyMoves)

            i++
        }
    }

    override fun refine(partitions: List<domain.synthesis.behavior.householdgeneration.Partition>) {
        val maxGain = partitions.first().signatures.largestDifference
        require(partitions.all { it.signatures === partitions.first().signatures }) {
            "How did we get here, they should all have the same signature tracker"
        }
        val buckets = _root_ide_package_.domain.synthesis.behavior.householdgeneration.BucketList<Moved>(maxGain)

        val updater = prepSignatures(partitions.first().signatures, partitions.first().attributeSize)

        val otherPartitions = partitions.map {
            _root_ide_package_.domain.synthesis.behavior.householdgeneration.TempPartition(
                it,
                updater,
                buckets
            )
        }

        val bestTargetTracker =
            _root_ide_package_.domain.synthesis.behavior.householdgeneration.BestTargetTracker(otherPartitions)

        val recalculator = MoveRecalculator(otherPartitions, maxGain, bestTargetTracker)
        otherPartitions.forEach {
            it.initialize(bestTargetTracker)
        }
        buckets.validateElements {
            !it.isEmpty
        }
        otherPartitions.forEach {
            it.verifyAll()
        }

        for (x in 0..this.amountOfPasses) {
            val duration = measureTime {
                runIteration(buckets, recalculator, bestTargetTracker)
                refreshRound(otherPartitions, buckets, bestTargetTracker)
            }
        }
        return
    }
}
