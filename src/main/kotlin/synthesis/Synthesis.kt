package synthesis

import domain.data.Employment
import domain.data.Sex
import units.Currency
import units.Distance
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
interface SurveyInfo : SurveyEmployment, SurveyAge {
    val householdId: Int
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

/**
 * If the survey data has information about the employment status of the survey person, this interface should be added
 * to the class holding the information block
 */

interface SurveyEmployment {
    val employment: Employment
}

/**
 * If the survey data or the person has age as an attribute
 */
interface SurveyAge {
    val age: Int
}

/**
 * All the information from the survey file, including all irrelevant information
 */
data class RawSurveyInfo(
    override val householdId: Int,
    val year: Int,
    val areaType: Int, // TODO what is this?
    val householdSize: Int, // TODO remove?. If I determine household size over the household object, this is useless
    val personNumber: Int,
    override val sex: Sex,
    val birthyear: Int,
    override val employment: Employment,
    val hasCommuterTicket: Boolean,
    override val householdIncome: Currency,
    val householdIncomeClass: Int, // TODO what is this? it is in a range between 0-8 ???
    val type: Int, // TODO what even is this? It Could be raumtype NVM it is Household Type (SINGLE_HH_ETC
    val cars: Int,
    val hasBicycle: Boolean,
    override val hasLicence: Boolean,
    override val distanceWork: Distance,
    val distanceEducation: Distance
) : SurveyInfo, CommuteDistance {
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

interface SurveyPerson<T> {
    val personId: Int
    val information: T
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
