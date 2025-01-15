package synthesis

import domain.data.Person
import domain.data.Sex

data class HouseholdRepresentative(
    val members: Set<Pair<Int, PersonRepresentative>>
) {
    val size = members.sumOf { it.first }

    override fun toString(): String {
        return members.joinToString(prefix = "[", postfix = "]") { "${it.first}x ${it.second.shortString()}" }
    }
}

fun Person.toRepresentative(): PersonRepresentative {
    return PersonRepresentative.fromData(sex, age)
}

data class PersonRepresentative(
    val sex: Sex,
    val ageGroup: Int
) {
    fun shortString(): String {
        return "$sex ageGroup=$ageGroup"
    }
    companion object {
        fun fromData(sex: Sex, age: Int): PersonRepresentative {
            val groupCode = groupCode(age)
            return PersonRepresentative(
                sex,
                groupCode

            )
        }

        private fun groupCode(age: Int): Int {
            val groupCode = when (age) {
                in 0..5 -> 0
                in 6..9 -> 1
                in 10..14 -> 2
                in 15..17 -> 3
                in 18..24 -> 4
                in 25..29 -> 5
                in 30..44 -> 6
                in 45..59 -> 7
                in 60..64 -> 8
                in 65..74 -> 9
                in 75..Int.MAX_VALUE -> 10
                else -> throw NoSuchElementException("Negative Age cannot be translated to a group code person=$this")
            }
            return groupCode
        }
    }
}
