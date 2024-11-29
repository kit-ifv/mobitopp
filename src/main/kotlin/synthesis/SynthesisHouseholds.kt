package synthesis

import domain.data.EconomicStatus
import domain.location.Location
import modeling.discreteChoice.CarOwnershipParameters
import units.Currency
import units.euros


class SynthesisHouseholdBuilder(
    var id: Int = 0,
    var income: Currency = 0.euros,


) {
    fun toCarOwnershipParameters(): CarOwnershipParameters {
        return CarOwnershipParameters(
            this,
            {0.0}
        )
    }

    var members: MutableList<SurveyPerson> = mutableListOf()
    lateinit var location: Location
    lateinit var economicStatus: EconomicStatus
    var amountOfCars = 0

}

val SynthesisHouseholdBuilder.numberOfAdults get() = members.count { it.age >= 18 }
val SynthesisHouseholdBuilder.numberOfMinors get() = members.count { it.age < 18 }


data class SurveyHousehold(
    override val id: Int,
    override val income: Currency,
    override val members: List<SurveyPerson>
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
            id = id,
            income = income,
        ).apply {
            members = this@SurveyHousehold.members.toMutableList()

        }
    }
}

interface ISurveyHousehold {
    val id: Int
    val income: Currency
    val members: List<SurveyPerson>
}

class LocatedHousehold(private val surveyHousehold: SurveyHousehold, val location: Location) :
    ISurveyHousehold by surveyHousehold {

}


