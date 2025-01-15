package synthesis

import domain.data.Employment
import domain.data.Sex
import domain.data.ZoneId
import synthesis.fixedDestinations.ZoneNumber
import units.Currency
import units.Distance
import utils.csv.DefaultCsvParser
import java.nio.file.Path
import java.util.*
import kotlin.math.abs





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
    if (amount == 0) return emptyList()
    require(isNotEmpty()) {
        "Cannot select an exact amount of elements from an empty collection"
    }
    val inputList = toList()
    val repeatedList = List(amount) { inputList[it % size] }
    return repeatedList.shuffled(random)

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
    val areaType: Int, //TODO what is this?
    val householdSize: Int, //TODO remove. If I determine household size later over the household object, this info is useless
    val personNumber: Int,
    override val sex: Sex,
    val birthyear: Int,
    override val employment: Employment,
    val hasCommuterTicket: Boolean,
    override val householdIncome: Currency,
    val householdIncomeClass: Int, // TODO what is this? it is in a range between 0-8 ???
    val type: Int, //TODO what even is this? It Could be raumtype NVM it is Household Type (SINGLE_HH_ETC
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
fun <T : SurveyInfo> Collection<T>.toSurveyHouseholds(converter: (List<Currency>) -> Currency = { it.first() }): List<SurveyHousehold<T>> {
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
                })
        }
}





interface SurveyPerson<T> {
    val personId: Int
    val information: T
    val age: Int
    val sex: Sex

    fun toRepresentative(): PersonRepresentative {
        return PersonRepresentative.fromData(sex, age)
    }
}




data class SmallestSurveyPerson<T>(
    override val personId: Int,
    override val information: T,
    override val age: Int,
    override val sex: Sex
): SurveyPerson<T> {

}

data class DefaultSurveyPerson<T : SurveyInfo>(
    override val personId: Int,
    override val information: T
) : SurveyInfo by information, SurveyPerson<T> {

    override val sex: Sex = information.sex
    override val age: Int = information.age
    override val employment: Employment = information.employment
    override val hasLicence: Boolean = information.hasLicence
    val representative = toRepresentative()

    val groupCode: Int
        get() {
            val groupCode = when (age) {
                in 0..5 -> 0
                in 6..9 -> 1
                in 10..14 -> 2
                in 15..17 -> 3
                in 18..24 -> 4
                in 25..29 -> 5
                in 30..44 -> 6
                in 45..59 -> 7
                in 60..64 -> 8
                in 65..74 -> 9
                in 75..Int.MAX_VALUE -> 10
                else -> throw NoSuchElementException("Negative Age cannot be translated to a group code person=$this")
            }
            return groupCode
        }

    companion object {
        private var idCounter: Int = 0
        fun <T : SurveyInfo> create(
            information: T
        ) = DefaultSurveyPerson(idCounter++, information)
    }
}