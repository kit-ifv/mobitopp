package domain.synthesis.behavior.householdgeneration

import kotlin.random.Random

/**
 * Keeps track which partition is currently the best to receive a signature based on the calculated gain.
 */
class BestTargetTracker(allPartitions: Collection<TempPartition>, private val random: Random = Random(1)) {

    private val maxGain: Int = allPartitions.first().signatureTracker.largestDifference

    private val signatureBuckets: Array<BucketList<TempPartition>> = allPartitions.first()
        .signatureTracker.indices.map {
            BucketList<TempPartition>(maxGain)
        }.toTypedArray()

    init {
        signatureBuckets.withIndex().forEach { (i, bucket) ->
            allPartitions.forEach { partition ->
                bucket.insert(partition, partition.getGain(i))
            }
        }
    }

    fun isBest(sigIdx: Int, partition: TempPartition): Boolean {
        return signatureBuckets[sigIdx].isBest(partition)
    }
    fun getRandom(sigIdx: Int): TempPartition {
        return signatureBuckets[sigIdx].randomBest(random)
    }

    fun getFirst(sigIdx: Int): TempPartition {
        return signatureBuckets[sigIdx].pollBest()
    }

    // TODO theoretically the updater does not need to change all sigBuckets, but just the ones that actually changed
    fun update(tempPartition: TempPartition) {
        signatureBuckets.withIndex().forEach { (i, bucket) ->
            bucket.update(tempPartition, tempPartition.getGain(i))
        }
    }
}

class UnidirectionalBestTargetTracker(originPartition: TempPartition, destinationPartitions: Collection<TempPartition>, private val random: Random = Random(1)) {
    private val maxGain: Int = originPartition.signatureTracker.largestDifference


}


typealias Bucket<T> = MutableSet<T>
