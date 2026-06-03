package domain.synthesis.behavior.fixeddestinations.communitybased

@JvmInline
value class CommunityNumber(private val int: Int) {
    companion object {
        fun parse(string: String): CommunityNumber = CommunityNumber(string.toInt())

        val INVALID = CommunityNumber(Int.MIN_VALUE)
    }
}
