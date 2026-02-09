package domain.synthesis

data class AreaIPUOutput<AREA>(
    val zone: AREA,
    val original: IPUOutputLog,
) {
    fun isImperfect() = original.isImperfect()

    val expected get() = original.expected
    val actual get() = original.actual
}
