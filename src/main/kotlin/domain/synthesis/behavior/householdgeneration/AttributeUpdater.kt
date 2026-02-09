package domain.synthesis.behavior.householdgeneration

import kotlin.math.abs
import kotlin.math.min

class AttributeUpdater(
    val internalUpdaters: Array<AttributeUpdates>,
    private val maxGain: Int
) {
    var counter = 0

    // Return true if any of the sigs changed, because in this scenario the gains of certain moves need to be recalced.
    /**
     * Update the target array [target] by checking what the entries for a given delta are for the original
     * diff and the current diff, and then updating the number.
     *
     * @return The indicies of elements that are dirty from the update operation, but as an Int instead of SignatureIndex
     * because primitive Arrays are stronger.
     */

    fun performUpdate(attributeIndex: Int, originalDiff: Int, newDiff: Int, target: IntArray): List<Int> {
        counter++
        val updater = internalUpdaters[attributeIndex]

        val origA = updater.findArrayFor(originalDiff)
        val newA = updater.findArrayFor(newDiff)

        if (origA === newA) return emptyList()

        val sigs = updater.updateSigIndices
        for (i in sigs.indices) {
            target[sigs[i]] += newA[i] - origA[i]

            if (abs(target[sigs[i]]) > maxGain) {
                error("There should never be an update so that the maxgain is exceeded.")
            }
        }
        return sigs.toList()
    }

    fun getCurrent(attributeIndex: Int, currentDiff: Int): Pair<IntArray, IntArray> {
        val updates = internalUpdaters[attributeIndex]
        return updates.updateSigIndices to updates.findArrayFor(currentDiff)
    }

    companion object {
        fun fromSignatureTracker(signatureTracker: SignatureTracker, attributeSize: Int): AttributeUpdater {
            val updaters = (0..<attributeSize).map { attrIdx ->
                // Get the maximum value that any signature has for a given attribute index
                val maxSigContent = signatureTracker.highestAttributeForIndex(attrIdx)

                require(maxSigContent > 0) {
                    "No signature has a value greater than 0 for a given attribute, are you certain you want to continue?"
                }

                val relevantSigIndices = signatureTracker.getSetByAttributeIndex(attrIdx)

                val updater =
                    AttributeUpdates(
                        relevantSigIndices.toIntArray(),
                        (0..maxSigContent).toList().toIntArray(),
                    )

                val relevantSigs = signatureTracker.getByAttribute(attrIdx)
                val contentNums = relevantSigs.map {
                    it[attrIdx]
                        ?: throw IllegalStateException("A relevant sig must have a value greater than 0 " +
                            "for the target attribute")
                }

                for (j in 0..maxSigContent) {
                    updater.updateArrays[j] = contentNums.map { min(-it + 2 * j, it) }.toIntArray()
                }
                updater
            }

            return AttributeUpdater(updaters.toTypedArray(), signatureTracker.largestDelta)
        }
    }
}
