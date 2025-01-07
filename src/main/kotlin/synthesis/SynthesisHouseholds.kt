package synthesis

import domain.data.EconomicStatus
import domain.location.Location
import synthesis.discreteChoice.CarOwnershipFactors
import units.Currency
import units.euros


class SynthesisHouseholdBuilder(
    var id: Int = 0,
    var income: Currency = 0.euros,


) {
    fun toCarOwnershipAttributes(): CarOwnershipFactors {
        return CarOwnershipFactors(
            this,
        )
    }

    var members: MutableList<SynthesisPerson> = mutableListOf()
    lateinit var location: Location
    lateinit var economicStatus: EconomicStatus
    var amountOfCars = 0

}
// TODO move this somewhere else
var GLOBAL_PERSON_ID_GENERATOR = 0
    get() = field.also { field++ }
    private set
class SynthesisPerson(
    val household: SynthesisHouseholdBuilder,
    val person: SurveyInfo
): SurveyInfo by person {
    val homeLocation get() = household.location
    var hasTransitPass = false
    val personId = GLOBAL_PERSON_ID_GENERATOR
}
val SynthesisHouseholdBuilder.numberOfAdults get() = members.count { it.age >= 18 }
val SynthesisHouseholdBuilder.numberOfMinors get() = members.count { it.age < 18 }


data class SurveyHousehold(
    override val householdId: Int,
    override val income: Currency,
    override val members: List<SurveyPerson<*>>
) :
    ISurveyHousehold {
    lateinit var economicStatus: EconomicStatus
    val representative = toRepresentative()
    private fun toRepresentative(): HouseholdRepresentative {
        val memberCount = members.map { it.representative }.groupingBy { it }.eachCount()
            .map { (element, count) -> Pair(count, element) }.toSet()
        return HouseholdRepresentative(memberCount)
    }

    fun toBuilder(): SynthesisHouseholdBuilder {
        return SynthesisHouseholdBuilder(
            id = householdId,
            income = income,
        ).apply {
            members = this@SurveyHousehold.members.map{SynthesisPerson(this, it)}.toMutableList()

        }
    }
}

interface ISurveyHousehold {
    val householdId: Int
    val income: Currency
    val members: List<SurveyPerson<*>>
}



