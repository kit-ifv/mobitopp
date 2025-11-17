package domain.synthesis.behavior.householdgeneration

import kotlin.math.min

class BucketListSignatureDistributor : InitialSignatureDistributor {
    override fun distribute(
        partitions: List<Partition>,
        signatureAmounts: Collection<SignatureAmount>,
    ) {
        val elements = signatureAmounts.withIndex().map { it.value.toMutable(it.index) }.toMutableList()
        elements.removeAll { it.amount == 0 } // Don't need to bother evaluating signatures that should not be placed
        val fakePartition = createFakePartition(signatureAmounts, partitions.first())


        val fm = FMRun(1) {
            min(
            it.from.amount(it.signatureIndex),
            min(
                it.from.untilFlagChange(it.signatureIndex.index, -1),
                it.to.untilFlagChange(it.signatureIndex.index, 1)
            )
        )}

        fm.refine(partitions + fakePartition)

        println("finished")
    }

    fun createFakePartition(elements: Collection<SignatureAmount>, copyPartition: Partition): Partition {
        val signatureTracker: SignatureTracker = copyPartition.signatures
        val size = copyPartition.attributeSize
        val newPartition = Partition(IntArray(size) {0}, signatureTracker)

        elements.forEach {
            newPartition.delta(it.signature, it.amount)
        }
        return newPartition
    }


}