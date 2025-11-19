package domain.synthesis.behavior.householdgeneration

import it.unimi.dsi.fastutil.PriorityQueue
import kotlin.random.Random
import kotlin.system.exitProcess

fun PriorityQueue<SymmetricalMoved>.pullClean(): Move? {
    while (!isEmpty) {
        val element = dequeue()
        if (element.maxSendAmount == 0) continue

        if (element.isLocked) {
            element.isLocked = false
            enqueue(element)


        } else {
            return element
        }
    }
    return null
}

data class FlaggedMove(
    override val from: TempPartition,
    override var to: TempPartition,
    override val signatureIndex: SignatureIndex,
) : Move {
    override var isLocked: Boolean = false
    var isDirty: Boolean = false
}


fun <T> MutableList<T>.cyclicAccessor(): CyclicAccessor<T> {
    return CyclicAccessor(this)
}


class CyclicAccessor<T>(private val list: MutableList<T>) {
    private var index = 0

    fun next(): T {
        return list[index].also { index = (index + 1) % list.size }
    }

    fun remove(element: T) {
        list.remove(element)
        if (list.isEmpty()) return
        index = index % list.size
    }

    fun isEmpty(): Boolean {
        return list.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return list.isNotEmpty()
    }
}

class FMEmptier(

    val amountStrategy: (Move) -> Int = { 1 },
) : InitialSignatureDistributor {
    override fun distribute(
        partitions: List<Partition>,
        signatureAmounts: Collection<SignatureAmount>,
    ) {
        val maxGain = partitions.first().signatures.largestDifference
        val buckets = BucketList<Move>(maxGain)

        val updater = AttributeUpdater.fromSignatureTracker(
            partitions.first().signatures,
            partitions.first().attributeSize
        )

        val sourcePartition: Partition = createFakePartition(signatureAmounts, partitions.first())
        val senderPartition = SenderPartition(sourcePartition, updater, buckets)

        val maxGain1 = signatureAmounts.maxOf { it.signature.values.sum() }

        val otherPartitions = partitions.map {

            ReceiverPartition(
                it,
                updater,
                buckets,
                senderPartition.activeSignatures,
                maxGain1
            )
        }

        val removeablePartitions = otherPartitions.toMutableList().cyclicAccessor()

        while (removeablePartitions.isNotEmpty()) {
            val currentReceiver =
                runCatching { removeablePartitions.next() }.getOrNull() ?: throw NoSuchElementException()
            if (!currentReceiver.wantsElements()) {
                removeablePartitions.remove(currentReceiver)
            } else {
                val sigIdx = currentReceiver.best()
                val amount = 1
                val kill = senderPartition.operativeDelta(sigIdx, -amount)
                currentReceiver.delta(sigIdx, amount)
                kill?.let { kill ->
                    otherPartitions.forEach {
                        it.remove(kill)
                    }
                }
            }
        }
        senderPartition
        emplaceDead(
            senderPartition,
            otherPartitions
        )


    }

    fun emplaceDead(
        sender: SenderPartition,
        receivers: Collection<ReceiverPartition>,
    ) {
        val distributionFactors = receivers.associateWith { it.expectedAttributeSum() }
        val sum = distributionFactors.values.sum()

        val normalized = distributionFactors.mapValues { it.value.toDouble() / sum }
        val badstuff = sender.remainingElements()

        badstuff.forEach { (sig, amount) ->

            val transfers = scaleDistribution(normalized.values, amount)

            transfers
                .zip(receivers)
                .filter { it.first > 0 }
                .forEach { (amnt, rec) ->
                    rec.delta(sig, amnt)
                    sender.operativeDelta(sig, -amnt)
                }

        }



    }

    fun scaleDistribution(distribution: Collection<Double>, total: Int): List<Int> {
        val scaled = distribution.map { it * total }
        val floored = scaled.map { it.toInt() }.toMutableList()
        val leftOver = total - floored.sum()

        val remainders = scaled.mapIndexed { i, v -> i to (v - floored[i]) }

        remainders.sortedByDescending { it.second }
            .take(leftOver)
            .forEach { (i, _) -> floored[i]++ }

        return floored
    }


    fun createFakePartition(elements: Collection<SignatureAmount>, copyPartition: Partition): Partition {
        val signatureTracker: SignatureTracker = copyPartition.signatures
        val size = copyPartition.attributeSize
        val newPartition = Partition(IntArray(size) { 0 }, signatureTracker)

        elements.forEach {
            newPartition.delta(it.signature, it.amount)
        }
        return newPartition
    }

    var i = 0
    fun runIteration(
        buckets: BucketList<Move>,
        recalculator: UnidirectionalRecalculator,
        bestTargetTracker: BestTargetTracker,
    ) {

        val performedMoves = mutableListOf<Move>()
        while (true) {
            val (element, gain) = buckets.popBest() ?: break
            performedMoves.add(element)
            require(element !in buckets) {
                "The element should be popped, and thus no longer here"
            }

            val sendAmount = amountStrategy(element)
            if (element.maxSendAmount == 0) {
                continue
            }
            require(!element.isLocked) {
                "Element should not be locked."
            }

            require(element.gain == gain) {
                "Mismatch in gain of element and associated bucket element=${element.gain} bucket=$gain"
            }

            val dirtyMoves = element.performMove(sendAmount)
            buckets.remove(element)

            bestTargetTracker.update(element.from)
            bestTargetTracker.update(element.to)

            recalculator.recalculate(buckets, dirtyMoves)

            i++
        }

        println(performedMoves.size)
        exitProcess(2)
    }
}

class UnidirectionalRecalculator(
    val bestTargetTracker: BestTargetTracker,
) {
    fun recalculate(buckets: BucketList<Move>, dirtyMoves: Collection<Move>) {
        if (dirtyMoves.isEmpty()) return

        dirtyMoves.filter { !it.isEmpty && !it.isLocked }.forEach {
            buckets.update(it, it.gain)
        }

    }
}

/**
 * Basically like [BestTargetTracker] but only moves from the source partition to all other partitions
 */
class UnidirectionalBestTargetTracker(
    originPartition: SenderPartition,
    destinationPartitions: Collection<ReceiverPartition>,
    private val random: Random = Random(1),
) {
    private val maxGain: Int = originPartition.signatureTracker.largestDifference

    private val signatureBuckets: Array<BucketList<TempPartition>> = originPartition
        .signatureTracker.indices.map {
            BucketList<TempPartition>(maxGain)
        }.toTypedArray()

    init {
        require(destinationPartitions.all { it.signatureTracker === originPartition.signatureTracker }) {
            "The signature tracker should be global and fixed."
        }
        // The tracker only needs to operate on the destination partitions, since the origin partition will never receive
        // an element.
        signatureBuckets.withIndex().forEach { (i, bucket) ->
            destinationPartitions.forEach { partition ->
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