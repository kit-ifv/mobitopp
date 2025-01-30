package synthesis

import domain.data.EconomicStatus
import domain.data.Employment
import synthesis.discreteChoice.CarOwnershipFactors
import synthesis.domain.SynthesisHousehold
import synthesis.domain.SynthesisPerson
import synthesis.householdgeneration.Rule
import synthesis.householdgeneration.ScalableVector
import units.Currency

fun SynthesisHousehold<out SurveyInfo>.toCarOwnershipAttributes(): CarOwnershipFactors {
    return CarOwnershipFactors(
        this,
    )
}

val SynthesisPerson<out SurveyInfo>.householdId get() = info.householdId

val SynthesisPerson<out SurveyInfo>.householdIncome get() = info.householdIncome
val SynthesisPerson<out SurveyInfo>.hasLicence get() = info.hasLicence
val SynthesisPerson<out SurveyEmployment>.employment get() = info.employment
fun SynthesisPerson<out SurveyEmployment>.isPrimaryStudent(): Boolean = employment == Employment.STUDENT_PRIMARY
fun SynthesisPerson<out SurveyEmployment>.isHigherStudent(): Boolean =
    employment == Employment.STUDENT_SECONDARY || employment == Employment.STUDENT_TERTIARY

fun SynthesisPerson<out SurveyEmployment>.isWorker() = employment == Employment.FULLTIME || employment == Employment.PARTTIME

// TODO move this somewhere else
var GLOBAL_PERSON_ID_GENERATOR = 0
    get() = field.also { field++ }
    private set

class SurveyHousehold<T>(
    override val householdId: Int,
    override val income: Currency,
    override val members: List<SurveyPerson<out T>>
) :
    ISurveyHousehold {
    lateinit var economicStatus: EconomicStatus
    val representative = toRepresentative()

    val size get() = members.size
    fun toScalableVector(rules: List<Rule<in T>>): ScalableVector {
        return ScalableVector.createFrom(this, rules)
    }
    private fun toRepresentative(): HouseholdRepresentative {
        val memberCount = members.map { it.toRepresentative() }.groupingBy { it }.eachCount()
            .map { (element, count) -> Pair(count, element) }.toSet()
        return HouseholdRepresentative(memberCount)
    }
    fun toSynthesisHousehold(): SynthesisHousehold<T> {
        return SynthesisHousehold<T>(
            id = householdId,
            income = income,
        ).apply {
            members = this@SurveyHousehold.members.map { SynthesisPerson(this, it.age, it.sex, it.information) }.toMutableList()
        }
    }

    fun count(condition: (SurveyPerson<out T>) -> Boolean): Int {
        return members.count(condition)
    }

    override fun toString(): String {
        return "Survey Household($householdId) [${members.joinToString { it.toString() }}"
    }
}

interface ISurveyHousehold {
    val householdId: Int
    val income: Currency
    val members: List<SurveyPerson<*>>
}
