package domain.synthesis.behavior

import domain.synthesis.attributes.household.MaximumHouseholdAttributes
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.HasEmployment
import domain.synthesis.attributes.person.HasLicence
import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.discreteChoice.CarOwnershipAttributes
import domain.synthesis.behavior.discreteChoice.CarOwnershipFactors
import domain.synthesis.SynthesisHousehold
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Employment
import domain.synthesis.data.HouseholdType
import edu.kit.ifv.units.Currency

fun MinimalistHousehold<MaximumHouseholdAttributes, MaximumPersonAttributes>.toCarOwnershipAttributes(): CarOwnershipAttributes {
    return CarOwnershipAttributes(
        CarOwnershipFactors(
            this,
        )
    )
}

val MinimalistPerson<HasLicence>.hasLicence get() = attributes.hasLicence
val MinimalistPerson<HasEmployment>.employment get() = attributes.employment
fun MinimalistPerson<HasEmployment>.isPrimaryStudent(): Boolean = employment == Employment.STUDENT_PRIMARY
fun MinimalistPerson<HasEmployment>.isSecondaryStudent(): Boolean = employment == Employment.STUDENT_SECONDARY
fun MinimalistPerson<HasEmployment>.isTertiaryStudent(): Boolean = employment == Employment.STUDENT_TERTIARY

fun MinimalistPerson<HasEmployment>.isWorker(): Boolean {
    return employment == Employment.FULLTIME || employment == Employment.PARTTIME
}




class SurveyHousehold<out HouseholdInfo: MinimumHouseholdAttributes, out PersonInfo : MinimumPersonAttributes>(
    override val surveyHouseholdId: Long,

    override val members: List<SurveyPerson<PersonInfo>>,

    override val attributes: HouseholdInfo,
) :
    ISurveyHousehold<HouseholdInfo, PersonInfo> {
    lateinit var economicStatus: EconomicStatus

    override fun toString(): String {
        return "Survey Household($surveyHouseholdId) [${members.joinToString { it.toString() }}"
    }
}

interface MinimalistHousehold<out S, out T> {
    val members: Collection<MinimalistPerson<T>>
    val size get() = members.size
    val attributes: S
}

interface ISurveyHousehold<out S: MinimumHouseholdAttributes, out T : MinimumPersonAttributes> : MinimalistHousehold<S, T> {
    val surveyHouseholdId: Long
    val income: Currency get() = attributes.income
    override val members: List<SurveyPerson<T>>
    val type: HouseholdType get() = attributes.type
    fun count(condition: (SurveyPerson<T>) -> Boolean): Int {
        return members.count(condition)
    }
}

object HouseholdFactory  {

    fun <S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> createFrom(input: ISurveyHousehold<S, T>): SynthesisHousehold<S, T> {

        val household = SynthesisHousehold<S, T>(
            surveyHouseholdId = input.surveyHouseholdId,
            attributes = input.attributes,

            )
        household.addMembers(input.members)
        return household

    }
}