package modeling.discreteChoice

import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.Sex
import synthesis.ISurveyHousehold
import synthesis.SurveyHousehold
import synthesis.SurveyPerson
import synthesis.SynthesisHouseholdBuilder
import utils.collections.select
import java.util.*
import java.util.function.DoubleSupplier
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.random.Random

class EconomicHousehold(
    val id: Int,
    val economicStatus: EconomicStatus,
    val members: List<SurveyPerson>
) {

}
class HouseholdBuilder {
    var id: Int = -1
    lateinit var income: Currency

}

class EconomicalHousehold(
    surveyHousehold: ISurveyHousehold,
    val economicStatus: EconomicStatus
): ISurveyHousehold by surveyHousehold {

}

data class EmployedPerson(
    val sex: Sex,
    val age: Int,
    val employment: Employment = Employment.UNKNOWN,
    val hasDrivingLicence: Boolean,
)

interface EmploymentSorter {
    fun isWorking(employment: Employment): Boolean
    fun isUniversityStudent(employment: Employment): Boolean
    fun isRetired(employment: Employment): Boolean
    fun isUnemployed(employment: Employment): Boolean
}

object DefaultEmploymentSorter : EmploymentSorter {
    private val workingOccupation = setOf(Employment.FULLTIME, Employment.PARTTIME)
    override fun isWorking(employment: Employment): Boolean {
        return employment in workingOccupation
    }

    override fun isUniversityStudent(employment: Employment): Boolean {
        return employment == Employment.STUDENT_TERTIARY
    }

    override fun isRetired(employment: Employment): Boolean {
        return employment == Employment.RETIRED
    }

    override fun isUnemployed(employment: Employment): Boolean {
        return employment == Employment.UNEMPLOYED
    }

}

class CarOwnershipParameters(
    val household: SynthesisHouseholdBuilder,
    val doubleGenerator: DoubleSupplier,
    employmentSorter: EmploymentSorter = DefaultEmploymentSorter,
) {
    val size = household.members.size
    val economicStatus: EconomicStatus = household.economicStatus
    val numDrivingLicence: Int = household.members.count { it.driverLicence }
    val numberOfWorkers = household.members.count { employmentSorter.isWorking(it.employment) }
    val isWg = household.members.all { employmentSorter.isUniversityStudent(it.employment) } && size >= 3
    val isOnlyRetired = household.members.all { employmentSorter.isRetired(it.employment) }
    val isOnlyUnemployed = household.members.all { employmentSorter.isUnemployed(it.employment) }
    val amountOfChildren = household.members.count { it.age < 10 }
    val amountOfYouth = household.members.count { it.age in 10..17 }

}


interface ICarOwnershipParameters {
    val mu: Double
    val sigma: Double
    fun evaluate(params: CarOwnershipParameters): Double {
        return mu + params.doubleGenerator.asDouble * sigma
    }

    fun evaluateHouseholdSize(params: CarOwnershipParameters): Double = 0.0
    fun evaluateEconomicStatus(params: CarOwnershipParameters): Double = 0.0
    fun evaluateAmountOfChildren(params: CarOwnershipParameters): Double = 0.0
    fun evaluateAmountOfYouths(params: CarOwnershipParameters): Double = 0.0
    fun evaluateNumberOfWorkers(params: CarOwnershipParameters): Double = 0.0
    fun evaluateNumberOfLicences(params: CarOwnershipParameters): Double = 0.0
    fun evaluateFlat(params: CarOwnershipParameters): Double = 0.0
    fun evaluateRetired(params: CarOwnershipParameters): Double = 0.0
    fun evaluateUnemployed(params: CarOwnershipParameters): Double = 0.0

    fun calculate(params: CarOwnershipParameters): Double {

        return evaluate(params) + evaluateHouseholdSize(params) + evaluateEconomicStatus(params) +
                evaluateAmountOfChildren(params) + evaluateAmountOfYouths(params) + evaluateNumberOfWorkers(params) +
                evaluateNumberOfLicences(params) + evaluateFlat(params) + evaluateRetired(params) + evaluateUnemployed(
            params
        )
    }


}

interface HouseholdSize : ICarOwnershipParameters {
    val oneMember: Double
    val twoMembers: Double
    val fourOrMoreMembers: Double

    override fun evaluateHouseholdSize(params: CarOwnershipParameters): Double {
        return when (params.size) {
            1 -> oneMember
            2 -> twoMembers
            in 4..Int.MAX_VALUE -> fourOrMoreMembers
            else -> 0.0
        }
    }
}

interface HouseholdEconomicStatus : ICarOwnershipParameters {
    val lowIncome: Double
    val highIncome: Double
    override fun evaluateEconomicStatus(params: CarOwnershipParameters): Double {
        return when (params.economicStatus.code) {
            in 1..2 -> lowIncome
            in 4..5 -> highIncome
            else -> 0.0
        }
    }
}

interface Children : ICarOwnershipParameters {
    val childrenFactor: Double
    override fun evaluateAmountOfChildren(params: CarOwnershipParameters): Double {
        return childrenFactor * params.amountOfChildren
    }
}

interface Youths : ICarOwnershipParameters {
    val youthFactor: Double
    override fun evaluateAmountOfYouths(params: CarOwnershipParameters): Double {
        return youthFactor * params.amountOfYouth
    }
}

interface Workers : ICarOwnershipParameters {
    val oneWorker: Double
    val twoWorkers: Double
    override fun evaluateNumberOfWorkers(params: CarOwnershipParameters): Double {
        return when (params.numberOfWorkers) {
            1 -> oneWorker
            2 -> twoWorkers
            else -> 0.0
        }
    }
}

interface Licences : ICarOwnershipParameters {
    val oneLicence: Double
    val twoLicences: Double
    val threeLicences: Double
    val fourOrMoreLicences: Double
    override fun evaluateNumberOfLicences(params: CarOwnershipParameters): Double {
        return when (params.numDrivingLicence) {
            1 -> oneLicence
            2 -> twoLicences
            3 -> threeLicences
            in 4..Int.MAX_VALUE -> fourOrMoreLicences
            else -> 0.0
        }
    }
}

interface Flat : ICarOwnershipParameters {
    val isFlat: Double
    override fun evaluateFlat(params: CarOwnershipParameters): Double {
        return if (params.isWg) isFlat else 0.0
    }
}

interface Retired : ICarOwnershipParameters {
    val isRetired: Double
    override fun evaluateRetired(params: CarOwnershipParameters): Double {
        return if (params.isOnlyRetired) isRetired else 0.0
    }
}

interface Unemployed : ICarOwnershipParameters {
    val isUnemployed: Double
    override fun evaluateUnemployed(params: CarOwnershipParameters): Double {
        return if (params.isOnlyUnemployed) isUnemployed else 0.0
    }
}


data class CarOwnershipParameterse(
    val base: Double = 0.0
)

fun calculateUtilityCarOwnership(parameterObject: CarOwnershipParameterse) {

}

val NoCarParameters = object : ICarOwnershipParameters {
    override val mu = 0.0
    override val sigma = 2.67593060385488

}
val OneCarParameters: ICarOwnershipParameters = CarParameters(
    oneMember = -3.23972758525991 + 0.2,
    twoMembers = -0.768269409894878 + 0.05,
    fourOrMoreMembers = 0.768330308949847 + 0.08,
    lowIncome = -1.56661976932426,
    highIncome = 1.08268911673901,
    childrenFactor = -0.869349032505858,
    youthFactor = -0.00794724968036045,
    oneWorker = -1.22908910461908,
    twoWorkers = -1.63411786599527,
    oneLicence = 5.09926672616369,
    twoLicences = 5.71206620763584,
    threeLicences = 6.04717465368787,
    fourOrMoreLicences = 6.10710612959228,
    isFlat = -1.89710718194422,
    isRetired = -0.426099220231941,
    isUnemployed = -1.82327048027482,
    mu = 0.420030501062286,
    sigma = -0.0321520185405091,
)
val TwoCarParameters = CarParameters(
    oneMember = -3.38452474375784,
    twoMembers = -0.780969322578045,
    fourOrMoreMembers = 0.852290307592964,
    lowIncome = -1.59297671279903,
    highIncome = 1.1350853798127,
    childrenFactor = -0.909452047188882,
    youthFactor = -0.0219624250483548,
    oneWorker = -1.2195353573398,
    twoWorkers = -1.62448090895747,
    oneLicence = 5.12739682956448,
    twoLicences = 5.74786330947324,
    threeLicences = 6.10619957285624,
    fourOrMoreLicences = 6.18064906514258,
    isFlat = -1.93403374165459,
    isRetired = -0.439459412733719,
    isUnemployed = -1.81337481153653,
    mu = 0.368067635941016,
    sigma = 4.58347401363783E-4
)
val ThreeCarParameters = CarParameters(
    oneMember = -3.29131084074051,
    twoMembers = -0.810594293834768,
    fourOrMoreMembers = 0.8661976785324159,
    lowIncome = -1.59385035236844,
    highIncome = 1.1599647632117,
    childrenFactor = -0.926288986018561,
    youthFactor = -0.029801647082422,
    oneWorker = -1.218323241698,
    twoWorkers = -1.62309602799223,
    oneLicence = 5.13038846915259,
    twoLicences = 5.74908851934019,
    threeLicences = 6.11834038428974,
    fourOrMoreLicences = 6.19782128535075,
    isFlat = -1.93789041056075,
    isRetired = -0.441144051104201,
    isUnemployed = -1.81001905327479,
    mu = 0.309599866397395,
    sigma = -0.0128506565299745
)

val FourCarParameters = CarParameters(
    oneMember = -3.37573395032723,
    twoMembers = -0.8581438677666421,
    fourOrMoreMembers = 0.896825471631694,
    lowIncome = -1.59769261885509,
    highIncome = 1.1567981811908101,
    childrenFactor = -0.93180031405934,
    youthFactor = -0.035681526138492,
    oneWorker = -1.21977909853093,
    twoWorkers = -1.62356367470392,
    oneLicence = 5.12656953796273,
    twoLicences = 5.74399114538186,
    threeLicences = 6.11070204049675,
    fourOrMoreLicences = 6.19275905159747,
    isFlat = -1.93789041056075,
    isRetired = -0.441144051104201,
    isUnemployed = -1.81001905327479,
    mu = 0.320270299510763 - 0.05,
    sigma = 0.0
)

data class CarParameters(
    override val oneMember: Double,
    override val twoMembers: Double,
    override val fourOrMoreMembers: Double,
    override val lowIncome: Double,
    override val highIncome: Double,
    override val childrenFactor: Double,
    override val youthFactor: Double,
    override val oneWorker: Double,
    override val twoWorkers: Double,
    override val oneLicence: Double,
    override val twoLicences: Double,
    override val threeLicences: Double,
    override val fourOrMoreLicences: Double,
    override val isFlat: Double,
    override val isRetired: Double,
    override val isUnemployed: Double,
    override val mu: Double,
    override val sigma: Double,
) : ICarOwnershipParameters, HouseholdSize, HouseholdEconomicStatus, Children, Youths, Workers, Licences, Flat,
    Retired, Unemployed {
    fun toString(identifier: String): String {
        return listOf(
            oneMember to "b_hh_size_1_on_$identifier",
            twoMembers to "b_hh_size_2_on_$identifier"

        ).joinToString(separator = "\n") { "${it.second} = ${it.first}" }

    }

    companion object {
        fun parse(identifier: String, values: Map<String, Double>): CarParameters {
            return CarParameters(
                oneMember = values.getOrWarn("b_hh_size_1_on_$identifier"),
                twoMembers = values.getOrWarn("b_hh_size_2_on_$identifier"),
                fourOrMoreMembers = values.getOrWarn("b_hh_size_4_on_$identifier"),
                lowIncome = values.getOrWarn("b_income_low_on_$identifier"),
                highIncome = values.getOrWarn("b_income_high_on_$identifier"),
                childrenFactor = values.getOrWarn("b_person_age_0_9_on_$identifier"),
                youthFactor = values.getOrWarn("b_person_age_10_17_on_$identifier"),
                oneWorker = values.getOrWarn("b_person_working_1_on_$identifier"),
                twoWorkers = values.getOrWarn("b_person_working_2_on_$identifier"),
                oneLicence = values.getOrWarn("b_license_1_on_$identifier"),
                twoLicences = values.getOrWarn("b_license_2_on_$identifier"),
                threeLicences = values.getOrWarn("b_license_3_on_$identifier"),
                fourOrMoreLicences = values.getOrWarn("b_license_4_on_$identifier"),
                isFlat = values.getOrWarn("b_shared_flat_on_$identifier"),
                isRetired = values.getOrWarn("b_hh_retired_on_$identifier"),
                isUnemployed = values.getOrWarn("b_hh_unemployed_on_$identifier"),
                mu = values.getOrWarn("asc_${identifier}_mu"),
                sigma = values.getOrWarn("asc_${identifier}_sig")
            )
        }
    }
}

fun Map<String, Double>.getOrWarn(key: String): Double {
    return getOrElse(key) {
        println("cannot find $key, returning 0.0")
        0.0
    }
}


//fun main() {
//    val input = "  -3.23972758525991 + 0.2 - 0.1 + 1.0 "
//    val result = evaluateExpression(input)
//    println("Result: $result")
//        val map = parseParameterMap(textdump)
//    val attempt = CarParameters.parse("2", map)
//    val attempt2 = CarParameters.parse("3", map)
//    val attempt3 = CarParameters.parse("4", map)
//    println(attempt)
//    println(attempt2)
//    println(attempt3)
//    println(map)
//}
val carNest = NestedLogit.root<Int, CarOwnershipParameters> {
    add(0) { _, p ->
        NoCarParameters.calculate(p)

    }
    nest(lambda = 0.0463786710826848 + 0.01) {
        add(1) { _, p ->
            OneCarParameters.calculate(p)
        }
        nest(lambda = 0.0132092332380213 + 0.01) {
            add(2) { _, p -> TwoCarParameters.calculate(p) }
            add(3) { _, p -> ThreeCarParameters.calculate(p) }
            add(4) { _, p -> FourCarParameters.calculate(p) }

        }
    }

}
val carChoiceModel =DiscreteChoiceModel<Int, CarOwnershipParameters>(
    carNest
) { it.select(Random(1).nextDouble()) }
fun main() {


//    val lambda_car = 0.0463786710826848 + 0.01
//    val lambda_two_more_car = 0.0132092332380213 + 0.01

    val nest = carNest

    val choiceModel = carChoiceModel

    val person = SurveyPerson.create(Sex.MALE, age = 19, employment = Employment.FULLTIME, true)
    val otherPerson = SurveyPerson.create(Sex.MALE, age = 19, employment = Employment.FULLTIME, false)
    val parameters = CarOwnershipParameters(SynthesisHouseholdBuilder(1).apply {
        economicStatus = EconomicStatus.MIDDLE
        members = mutableListOf(person)
    }, { 0.0 })
    println(choiceModel.selectVerbose(parameters))

    val fue = UtilityFunction<Int, CarOwnershipParameters> { _, p ->
        NoCarParameters.calculate(p)

    }
    println(fue.calculateUtility(0, parameters))
    val result = nest.calculateProbabilities(parameters)
    val result2 = nest.calculateProbabilities(
        CarOwnershipParameters(SynthesisHouseholdBuilder(1).apply {
            economicStatus = EconomicStatus.MIDDLE
            members = mutableListOf(otherPerson)
        }, { 0.0 })
    )
    println(result)
    println(result2)

}

