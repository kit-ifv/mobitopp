package domain.synthesis.behavior.householdgeneration

/**
 * This is the handwritten update history where an attribute produces a delta vector for signature indices.
 */
data class AttributeUpdates(
    val updateSigIndices: IntArray,
    val updateTriggerPoints: IntArray,
) {

    val updateArrays: Array<IntArray> = Array(updateTriggerPoints.size) { IntArray(updateSigIndices.size) }

    fun findArrayFor(diff: Int): IntArray {
        val idx = updateTriggerPoints.binarySearch(diff)
        return if (idx >= 0) {
            updateArrays[idx]
        } else {
            // We know that this case can only occur when the requested value is outside the possible trigger points,
            // because the trigger points are contiguous integers.

            if (diff <= 0) updateArrays.first() else updateArrays.last()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttributeUpdates

        return updateSigIndices.contentEquals(other.updateSigIndices)
    }

    override fun hashCode(): Int {
        return updateSigIndices.contentHashCode()
    }
}
