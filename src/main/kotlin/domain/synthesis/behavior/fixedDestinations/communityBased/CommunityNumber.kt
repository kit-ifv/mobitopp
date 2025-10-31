package domain.synthesis.behavior.fixedDestinations.communityBased

@JvmInline
value class CommunityNumber(private val int: Int) {
    companion object {
        fun parse(string: String): CommunityNumber {
            return CommunityNumber(string.toInt())
        }

        val INVALID = CommunityNumber(Int.MIN_VALUE)
    }
}
