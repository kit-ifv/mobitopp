package domain.synthesis.behavior.householdgeneration

interface Move {
    val from: TempPartition
    var to: TempPartition
    val signatureIndex: SignatureIndex
    var isLocked: Boolean

    val isEmpty get() = from.partition.amount(signatureIndex) <= 0
    val gain get() = from.getLoss(signatureIndex.index) + to.getGain(signatureIndex.index)
    val maxSendAmount get() = from.amount(signatureIndex)
    fun performMove(amount: Int = 1): List<Move> {
        require(!isLocked && from.amount(signatureIndex) >= amount) {
            "This should not occur"
        }
        val dirtyMoves = from.delta(signatureIndex, -amount)
        val otherDirtyMoves = to.delta(signatureIndex, +amount)
        isLocked = true

        return dirtyMoves + otherDirtyMoves
    }
}

data class Moved(
    override val from: TempPartition,
    override var to: TempPartition,
    override val signatureIndex: SignatureIndex,

) : Move {

    override fun equals(other: Any?): Boolean {
        if (other !is Moved) return false
        return from.id == other.from.id && signatureIndex == other.signatureIndex
    }

    override fun hashCode(): Int {
        var result = from.id.hashCode()
        result = 31 * result + signatureIndex.hashCode()
        return result
    }

    override var isLocked = false
}

data class SymmetricalMoved(
    override val from: TempPartition,
    override var to: TempPartition,
    override val signatureIndex: SignatureIndex,
) : Move {

    override var isLocked = false
    override fun equals(other: Any?): Boolean {
        if (other !is Moved) return false
        return from.id == other.from.id && to.id == other.to.id && signatureIndex == other.signatureIndex
    }

    override fun hashCode(): Int {
        var result = from.id.hashCode()
        result = 31 * result + to.id.hashCode()
        result = 31 * result + signatureIndex.hashCode()
        return result
    }
}
