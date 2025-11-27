package domain.synthesis.behavior

import domain.synthesis.behavior.discreteChoice.CarOwnershipAttributes
import domain.synthesis.behavior.discreteChoice.CarOwnershipFactors
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.behavior.householdgeneration.Rule
import domain.synthesis.behavior.householdgeneration.ScalableVector
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Employment
import domain.synthesis.data.HouseholdType
import edu.kit.ifv.units.Currency

fun SynthesisHousehold<out SurveyInfo>.toCarOwnershipAttributes(): CarOwnershipAttributes {
    return CarOwnershipAttributes(
        CarOwnershipFactors(
            this,
        )
    )
}

val SynthesisPerson<out SurveyInfo>.householdId get() = information.householdId

val SynthesisPerson<out SurveyInfo>.householdIncome get() = information.householdIncome
val SynthesisPerson<out SurveyInfo>.hasLicence get() = information.hasLicence
val SynthesisPerson<out HasSurveyEmployment>.employment get() = information.employment
fun SynthesisPerson<out HasSurveyEmployment>.isPrimaryStudent(): Boolean = employment == Employment.STUDENT_PRIMARY
fun SynthesisPerson<out HasSurveyEmployment>.isSecondaryStudent(): Boolean = employment == Employment.STUDENT_SECONDARY
fun SynthesisPerson<out HasSurveyEmployment>.isTertiaryStudent(): Boolean = employment == Employment.STUDENT_TERTIARY

fun SynthesisPerson<out HasSurveyEmployment>.isWorker(): Boolean {
    return employment == Employment.FULLTIME || employment == Employment.PARTTIME
}

// TODO move this somewhere else
var GLOBAL_PERSON_ID_GENERATOR = 0
    get() = field.also { field++ }
    private set

class SurveyHousehold<T>(
    override val surveyHouseholdId: Long,
    override val income: Currency,
    override val members: List<SurveyPerson<out T>>,
    override val type: HouseholdType = HouseholdType.UNDEFINED
) :
    ISurveyHousehold<T> {
    lateinit var economicStatus: EconomicStatus

    override fun toString(): String {
        return "Survey Household($surveyHouseholdId) [${members.joinToString { it.toString() }}"
    }
}

interface MinimalistHousehold<T> {
    val members: Collection<MinimalistPerson<T>>
    val size get() = members.size

    fun toSynthesisHousehold(): SynthesisHousehold<T>
}

interface ISurveyHousehold<T> : MinimalistHousehold<T> {
    val surveyHouseholdId: Long
    val income: Currency
    override val members: List<SurveyPerson<out T>>
    val type: HouseholdType
    fun count(condition: (SurveyPerson<out T>) -> Boolean): Int {
        return members.count(condition)
    }

    fun toScalableVector(rules: List<Rule<ISurveyHousehold<T>>>): ScalableVector {
        return ScalableVector.createFrom(this, rules)
    }

    override fun toSynthesisHousehold(): SynthesisHousehold<T> {
        return SynthesisHousehold<T>(
            surveyHouseholdId = surveyHouseholdId,
            income = income,
            type = type,
        ).apply {
            members = this@ISurveyHousehold.members.map { SynthesisPerson(this, it.age, it.sex, it.information) }
                .toMutableList()
        }
    }
}
