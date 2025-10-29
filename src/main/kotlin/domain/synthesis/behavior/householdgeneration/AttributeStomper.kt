package domain.synthesis.behavior.householdgeneration

import domain.synthesis.behavior.householdgeneration.refinement.Refinement
import kotlin.math.min
import kotlin.math.sign
import kotlin.random.Random

fun Partition.eval(): Double {
    val elements = countsList.sum()
    val bias = elements.toDouble() / (elements + 1000)
    return relativeErrors().max() * bias
}

/**
 * Flatten the worst attribute misrepresentation in the partition space by forcibly adding or removing
 * random elements from other partitions until the attribute is corrected.
 */

class AttributeStomper(
    private val repetitions: Int = 4,
    private val random: Random = Random(1),
    val stompTargetSelector: (
        Collection<Partition>
    ) -> Collection<Partition> = {
        it.sortedByDescending { p ->

            p.eval()
        }
    },
) : Refinement {

    override fun refine(partitions: List<Partition>) {
        repeat(repetitions) {
            val worstPartition = stompTargetSelector(partitions).first()

            val targetAttributeIndex = worstPartition.worstRelativeIndex()
            val errorSign = worstPartition.errorFor(targetAttributeIndex).sign

            val sortedPartitions = partitions.sortedBy { it.errorFor(targetAttributeIndex) }.filter {
                it !== worstPartition && it.errorFor(targetAttributeIndex).sign != errorSign
            }

            if (errorSign == 1) {
                resolveOverload(worstPartition, targetAttributeIndex, sortedPartitions)
            } else {
                resolveUnderload(worstPartition, targetAttributeIndex, sortedPartitions)
            }
        }
    }

    // In this instance we want to remove elements from the partition, preferably equally distributed because
    // We have no idea about other qualities. This means that we want to transfer roughly equally to each
    // receiving partition
    private fun resolveOverload(
        worstPartition: Partition,
        attrIdx: Int,
        suitableCandidates: List<Partition>
    ) {
        suitableCandidates.shuffled(random).forEach { candidate ->
            intern(worstPartition, candidate, attrIdx)
        }
    }

    // In this case we want to add elements to the partition. The other sender partitions have a smaller error by
    // default, so in this case we need to make sure that the transfer error is maintained, and sending a roughly
    // equal amount of the elements from the target partition.
    private fun resolveUnderload(
        worstPartition: Partition,
        attrIdx: Int,
        suitableCandidates: List<Partition>
    ) {
        suitableCandidates.shuffled(random).forEach {
            intern(it, worstPartition, attrIdx)
        }
    }

    private fun intern(
        senderPartition: Partition,
        receiverPartition: Partition,
        attrIdx: Int
    ) {
        val transferTargets = senderPartition.currentElementsForAttribute(attrIdx)
        val maxTransferAmount: Int = min(
            senderPartition.errorFor(attrIdx),
            -receiverPartition.errorFor(attrIdx)
        )

        val sum = transferTargets.sumOf { it.amountOfElements }

        val desiredTransferShares = transferTargets.associateWith { target ->
            (target.amountOfElements).toDouble() / sum
        }

        val integerTransferAmount = desiredTransferShares.entries.sumOf { (k, v) ->
            // This target is rounded down
            val integerTarget = (v * maxTransferAmount).toInt() / k.impact
            if (integerTarget != 0) {
                senderPartition.transferTo(
                    receiverPartition,
                    k.signatureIndex,
                    integerTarget
                )
            }

            integerTarget * k.impact
        }

        require(integerTransferAmount <= maxTransferAmount) {
            "This is unacceptable"
        }
        var remainingTransferTarget = maxTransferAmount - integerTransferAmount
        val remainingTargets = senderPartition.currentElementsForAttribute(attrIdx).shuffled(random).toMutableList()
        var i = 0
        while (remainingTransferTarget > 0 && remainingTargets.isNotEmpty()) {
            val target = remainingTargets[i % remainingTargets.size]
            if (target.impact > remainingTransferTarget || target.isEmpty()) {
                remainingTargets.remove(target)
                continue
            }

            val desiredTransfers = 1
            remainingTransferTarget -= desiredTransfers * target.impact
            target.amountOfElements -= desiredTransfers
            senderPartition.transferTo(
                receiverPartition,
                target.signatureIndex,
                desiredTransfers
            )
            i++
        }
    }
}

class CyclicIterator<T>(private val list: MutableList<T>) : Iterator<T> {
    private var index = 0

    override fun hasNext(): Boolean = list.isNotEmpty()

    override fun next(): T {
        if (list.isEmpty()) throw NoSuchElementException("List is empty")
        return list[index++ % list.size]
    }

    fun remove(target: T) = list.remove(target)
}

fun <T> Collection<T>.cyclicIterator(): CyclicIterator<T> {
    return CyclicIterator(this.toMutableList())
}
