package domain.synthesis.behavior

import domain.synthesis.behavior.discreteChoice.CarOwnershipAttributes
import domain.synthesis.behavior.discreteChoice.CarOwnershipFactors
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.behavior.householdgeneration.Rule
import domain.synthesis.behavior.householdgeneration.ScalableVector
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Employment
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
val SynthesisPerson<out SurveyEmployment>.employment get() = information.employment
fun SynthesisPerson<out SurveyEmployment>.isPrimaryStudent(): Boolean = employment == Employment.STUDENT_PRIMARY
fun SynthesisPerson<out SurveyEmployment>.isSecondaryStudent(): Boolean = employment == Employment.STUDENT_SECONDARY
fun SynthesisPerson<out SurveyEmployment>.isTertiaryStudent(): Boolean = employment == Employment.STUDENT_TERTIARY

fun SynthesisPerson<out SurveyEmployment>.isWorker(): Boolean {
    return employment == Employment.FULLTIME || employment == Employment.PARTTIME
}

// TODO move this somewhere else
var GLOBAL_PERSON_ID_GENERATOR = 0
    get() = field.also { field++ }
    private set

class SurveyHousehold<T>(
    override val surveyHouseholdId: Int,
    override val income: Currency,
    override val members: List<SurveyPerson<out T>>
) :
    ISurveyHousehold<T> {
    lateinit var economicStatus: EconomicStatus

    fun toScalableVector(rules: List<Rule<in T>>): ScalableVector {
        return ScalableVector.createFrom(this, rules)
    }

    fun toSynthesisHousehold(): SynthesisHousehold<T> {
        return SynthesisHousehold<T>(
            surveyHouseholdId = surveyHouseholdId,
            income = income,
        ).apply {
            members = this@SurveyHousehold.members.map { SynthesisPerson(this, it.age, it.sex, it.information) }
                .toMutableList()
        }
    }

    override fun toString(): String {
        return "Survey Household($surveyHouseholdId) [${members.joinToString { it.toString() }}"
    }
}

interface ISurveyHousehold<T> {
    val surveyHouseholdId: Int
    val income: Currency
    val members: List<SurveyPerson<out T>>
    val size get() = members.size
    fun count(condition: (SurveyPerson<out T>) -> Boolean): Int {
        return members.count(condition)
    }
}
