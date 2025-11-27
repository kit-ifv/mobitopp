package domain.synthesis.behavior

import domain.synthesis.data.Employment
import domain.synthesis.data.Graduation
import domain.synthesis.data.HouseholdType
import domain.synthesis.data.Sex
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import kotlin.random.Random

fun <T> Collection<T>.pickWithReplacement(
    amount: Int,
    random: Random = Random(1)
): List<T> {
    val inputList = toList()
    return List(amount) { inputList[random.nextInt(inputList.size)] }
}

/**
 * Select an exact amount from a list, if the list is not sufficiently long enough, it will be artificially filled by
 * repeating the elements. each list or list repetition element is shuffled. In order to return a random list of exactly
 * [amount] elements, the shuffled list will be truncated to the length.
 */
fun <T> Collection<T>.selectExact(amount: Int, random: Random = Random(1)): List<T> {
    val repeatedList = repeatExact(amount)
    return repeatedList.shuffled(random)
}

/**
 * Same as [selectExact] only that no shuffle is performed
 */
fun <T> Collection<T>.repeatExact(amount: Int): List<T> {
    if (amount == 0) return emptyList()
    require(isNotEmpty()) {
        "Cannot select an exact amount of elements from an empty collection"
    }
    val inputList = toList()
    return List(amount) { inputList[it % size] }
}

/**
 * This is the class that holds the data extract from the survey population csv. The file merges household and
 * person information.
 */
interface SurveyInfo : HasSurveyEmployment, SurveyAge {
    val householdId: Long
    val sex: Sex
    override val age: Int
    val householdIncome: Currency
    val hasLicence: Boolean
    override val employment: Employment
}

/**
 * This interface annotates the information about a survey person, so that the information of the distance to
 * the work location is known in the survey.
 */
interface CommuteDistance {
    val distanceWork: Distance
}

interface EducationDistance {
    val distanceEducation: Distance
}
interface SurveyWithCommute : SurveyInfo, CommuteDistance, EducationDistance

/**
 * If the survey data has information about the employment status of the survey person, this interface should be added
 * to the class holding the information block
 */

interface HasSurveyEmployment {
    val employment: Employment
}

/**
 * If the survey data or the person has age as an attribute
 */
interface SurveyAge {
    val age: Int
}
interface SurveyType {
    val type: HouseholdType
}

/**
 * Accessor interface for graduation property
 */
interface HasGraduation {
    val graduationCode: Int
}


/**
 * Raw Survey Info as taken from the usual population input from legacy mobitopp. Lots of the attributes are useless,
 * and this interface should not be targeted, rather all relevant information should be extracted in compositve interfaces
 */
interface RawSurveyInfo: SurveyWithCommute, SurveyType {
    override val householdId: Long
    val year: Int
    val areaType: Int
    val householdSize: Int
    val personNumber: Int
    override val sex: Sex
    val birthyear: Int
    override val employment: Employment
    val hasCommuterTicket: Boolean
    override val householdIncome: Currency
    val householdIncomeClass: Int
    override val type: HouseholdType
    val cars: Int
    val hasBicycle: Boolean
    override val hasLicence: Boolean
    override val distanceWork: Distance
    override val distanceEducation: Distance

}
/**
 * All the information from the survey file, including all irrelevant information
 */
data class RawSurveyInfoImpl(
    override val householdId: Long,
    override val year: Int,
    override val areaType: Int, // TODO what is this?
    override val householdSize: Int, // TODO remove?. If I determine household size over the household object, this is useless
    override val personNumber: Int,
    override val sex: Sex,
    override val birthyear: Int,
    override val employment: Employment,
    override val hasCommuterTicket: Boolean,
    override val householdIncome: Currency,
    override val householdIncomeClass: Int, // TODO what is this? it is in a range between 0-8 ???
    override val type: HouseholdType, // TODO what even is this? It Could be raumtype NVM it is Household Type
    override val cars: Int,
    override val hasBicycle: Boolean,
    override val hasLicence: Boolean,
    override val distanceWork: Distance,
    override val distanceEducation: Distance
) : RawSurveyInfo {
    override val age = year - birthyear
}

/**
 * @param converter provide a converter to determine the household income, as the reported incomes can be inaccurate.
 */
fun <T : SurveyInfo> Collection<T>.toSurveyHouseholds(
    converter: (List<Currency>) -> Currency = {
        it.first()
    }
): List<SurveyHousehold<T>> {
    return groupBy { it.householdId }
        .map { line ->
            val income = converter(line.value.map { it.householdIncome })
            SurveyHousehold(
                line.value.first().householdId,
                income,
                line.value.map { person ->
                    DefaultSurveyPerson.create(
                        person
                    )
                }
            )
        }
}

fun Collection<RawSurveyInfo>.toTypedSurveyHouseholds(
    converter: (List<Currency>) -> Currency = {
        it.first()
    }
): List<SurveyHousehold<RawSurveyInfo>> {
    return groupBy { it.householdId }
        .map { line ->
            val income = converter(line.value.map { it.householdIncome })
            SurveyHousehold(
                line.value.first().householdId,
                income,
                line.value.map { person ->
                    DefaultSurveyPerson.create(
                        person
                    )
                },
                type = line.value.first().type
            )
        }
}

interface MinimalistPerson<out T> {
    val information: T
}

interface SurveyPerson<T> : MinimalistPerson<T> {
    val personId: Int
    override val information: T
    val age: Int
    val sex: Sex
}

data class SmallestSurveyPerson<T>(
    override val personId: Int,
    override val information: T,
    override val age: Int,
    override val sex: Sex
) : SurveyPerson<T>

data class DefaultSurveyPerson<T : SurveyInfo>(
    override val personId: Int,
    override val information: T
) : SurveyInfo by information, SurveyPerson<T> {

    override val sex: Sex = information.sex
    override val age: Int = information.age
    override val employment: Employment = information.employment
    override val hasLicence: Boolean = information.hasLicence

    companion object {
        private var idCounter: Int = 0
        fun <T : SurveyInfo> create(
            information: T
        ) = DefaultSurveyPerson(idCounter++, information)
    }
}
