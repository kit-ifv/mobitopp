package domain.synthesis.behavior.householdgeneration

import kotlin.time.measureTime

class FMRun(
    val amountOfPasses: Int = 100,
    val amountStrategy: (Move) -> Int,
) : Refinement {

    fun refreshRound(
        partitions: List<TempPartition>,
        buckets: BucketList<Move>,
        bestTargetTracker: BestTargetTracker
    ) {
        buckets.clear()

        partitions.forEach {
            it.reset()
        }
        partitions.forEach {
            it.initialize(bestTargetTracker)
        }
    }

    var i = 0

    @Suppress("LoopWithTooManyJumpStatements")
    fun runIteration(
        buckets: BucketList<Move>,
        recalculator: MoveRecalculator,
        bestTargetTracker: BestTargetTracker
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

    override fun refine(partitions: List<Partition>) {
        val maxGain = partitions.first().signatureTracker.largestDifference
        require(partitions.all { it.signatureTracker === partitions.first().signatureTracker }) {
            "How did we get here, they should all have the same signature tracker"
        }
        val buckets = BucketList<Move>(maxGain)

        val updater = AttributeUpdater.fromSignatureTracker(
            partitions.first().signatureTracker,
            partitions.first().attributeSize
        )

        val otherPartitions = partitions.map {
            TempPartition(
                it,
                updater,
                buckets
            )
        }

        val bestTargetTracker =
            BestTargetTracker(otherPartitions)

        val recalculator = MoveRecalculator(bestTargetTracker)
        otherPartitions.forEach {
            it.initialize(bestTargetTracker)
        }
        buckets.validateElements {
            !it.isEmpty
        }
        otherPartitions.forEach {
            it.verifyAll()
        }
        repeat(amountOfPasses) {
            measureTime {
                runIteration(buckets, recalculator, bestTargetTracker)
                refreshRound(otherPartitions, buckets, bestTargetTracker)
            }
        }
        return
    }
}
