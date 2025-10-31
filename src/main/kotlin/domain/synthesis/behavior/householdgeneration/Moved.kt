package domain.synthesis.behavior.householdgeneration

data class Moved(
    val from: TempPartition,
    var to: TempPartition,
    val signatureIndex: SignatureIndex,

) {

    override fun equals(other: Any?): Boolean {
        if (other !is Moved) return false
        return from.id == other.from.id && signatureIndex == other.signatureIndex
    }

    override fun hashCode(): Int {
        var result = from.id.hashCode()
        result = 31 * result + signatureIndex.hashCode()
        return result
    }

    val gain get() = from.getLoss(signatureIndex.index) + to.getGain(signatureIndex.index)
    val isEmpty get() = from.partition.amount(signatureIndex) <= 0
    val isSelfReferential get() = from === to
    var isLocked = false

    fun performMove(amount: Int = 1): List<Moved> {
        require(!isLocked) {
            "This should not occur"
        }
        //        println("This move expects a gain from $gain [${from.getLoss(signatureIndex.index)}, ${to.getGain(signatureIndex.index)}]and goes from ${from.id} to ${to.id} amount=$amount")

        val dirtyMoves = from.delta(signatureIndex, -amount)
        val otherDirtyMoves = to.delta(signatureIndex, +amount)
        isLocked = true

        return dirtyMoves + otherDirtyMoves
    }
}
