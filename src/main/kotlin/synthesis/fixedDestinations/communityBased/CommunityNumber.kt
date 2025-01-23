package synthesis.fixedDestinations.communityBased

@JvmInline
value class CommunityNumber(private val int: Int) {
    companion object {
        fun parse(string: String): CommunityNumber {
            return CommunityNumber(string.toInt())
        }
    }
}
