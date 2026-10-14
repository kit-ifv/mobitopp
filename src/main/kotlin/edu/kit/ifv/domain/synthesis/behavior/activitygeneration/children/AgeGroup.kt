package edu.kit.ifv.domain.synthesis.behavior.activitygeneration.children

enum class AgeGroup(val ageRange: IntRange) {
    ZERO_TO_TWO(0..2),
    THREE_TO_FIVE(3..5),
    SIX_TO_NINE(6..9),
    ;

    operator fun contains(age: Int): Boolean = age in ageRange

    companion object {
        @Suppress("MagicNumber")
        fun decode(code: Int): AgeGroup = when (code) {
            1 -> ZERO_TO_TWO
            2 -> THREE_TO_FIVE
            3 -> SIX_TO_NINE
            else -> throw IllegalArgumentException("Invalid AgeGroup code $code")
        }

        fun fromAge(age: Int): AgeGroup? = entries.firstOrNull { age in it }
    }
}
