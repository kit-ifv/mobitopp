package domain.simulation.data.household

import utils.Decodable
import utils.Encodable

enum class HouseholdType(override val code: Int) : Encodable {

    SINGLE_HH_WITH_CHILDREN(1),
    SINGLE_HH(2),
    COUPLE_WITH_CHILDREN(3),
    COUPLE_WITHOUT_CHILDREN(4),
    OTHER_MULTI_PERSON_HH(5),
    UNDEFINED(-1),
    ;

    override val description: String = name

    companion object : Decodable<HouseholdType> {

        val validTypes = setOf(
            SINGLE_HH_WITH_CHILDREN,
            SINGLE_HH,
            COUPLE_WITH_CHILDREN,
            COUPLE_WITHOUT_CHILDREN,
            OTHER_MULTI_PERSON_HH,
        )

        private val mapping = entries.associateBy(HouseholdType::code).toMutableMap()

        private val toSet = entries.toSet()
        override fun values(): Set<HouseholdType> = toSet

        override fun decode(i: Int): HouseholdType = mapping[i] ?: UNDEFINED.also {
            println("Code $i is an undefined Household Type.")
            mapping[i] = it // So that later occurences do not get printed
        }
    }
}
