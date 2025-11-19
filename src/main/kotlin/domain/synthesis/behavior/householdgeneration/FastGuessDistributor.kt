package domain.synthesis.behavior.householdgeneration

import utils.scaleToInts

/**
 * Why not simply distribute based on predicted size, and then do a bit of refinement afterwards.
 */
class FastGuessDistributor: InitialSignatureDistributor {
    override fun distribute(
        partitions: List<Partition>,
        signatureAmounts: Collection<SignatureAmount>,
    ) {
        val distribution = partitions.associateWith { it.expectedSum() }
        val sum = distribution.values.sum()
        val normalized = distribution.mapValues { it.value.toDouble() / sum }
        signatureAmounts.forEach { (sig, amount) ->
            val transfers = normalized.values.scaleToInts(amount)

            transfers.zip(partitions).forEach { (amnt, rec) ->
                rec.delta(sig, amnt)
            }
        }


    }
}