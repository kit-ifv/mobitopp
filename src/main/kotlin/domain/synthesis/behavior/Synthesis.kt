package domain.synthesis.behavior

import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.data.Employment
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
 * All the information from the survey file, including all irrelevant information
 */
data class RawSurveyInfo(
    val householdId: Long,
    val year: Int,
    val areaType: Int, // TODO what is this?
    val householdSize: Int, // TODO remove?. If I determine household size over the household object, this is useless
    val personNumber: Int,
    val sex: Sex,
    val birthyear: Int,
     val employment: Employment,
    val hasCommuterTicket: Boolean,
    val householdIncome: Currency,
    val householdIncomeClass: Int, // TODO what is this? it is in a range between 0-8 ???
    val typeCode: Int,
    val cars: Int,
    val hasBicycle: Boolean,
    val hasLicence: Boolean,
    val distanceWork: Distance,
    val distanceEducation: Distance
)  {
    val age = year - birthyear

    val type: HouseholdType = HouseholdType.decodeOrNull(typeCode) ?: HouseholdType.UNDEFINED
}

/**
 * @param converter provide a converter to determine the household income, as the reported incomes can be inaccurate.
 */
//fun <T> Collection<T>.toSurveyHouseholds(
//    converter: (List<Currency>) -> Currency = {
//        it.first()
//    }
//): List<SurveyHousehold<MinimumHouseholdAttributes, T>> where T : MinimumPersonAttributes, T : SurveyInfo, T : HasHouseholdType {
//    return groupBy { it.householdId }
//        .map { line ->
//            val income = converter(line.value.map { it.householdIncome })
//            SurveyHousehold(
//                surveyHouseholdId = line.value.first().householdId,
//                members = line.value.map { person ->
//                    DefaultSurveyPerson.create(
//                        person
//                    )
//                },
//                attributes = MinimumHouseholdAttributesImpl(
//                    income = income,
//                    type = line.value.first().type,
//                )
//
//            )
//        }
//}
//






interface MinimalistPerson<out T> {
    val information: T
}


interface SurveyPerson<out T> : MinimalistPerson<T> where T: MinimumPersonAttributes {
    val personId: Int
    override val information: T
    val age: Int get() = information.age
    val sex: Sex get() = information.sex
}

data class SmallestSurveyPerson<T : MinimumPersonAttributes> constructor(
    override val personId: Int,
    override val information: T,
) : SurveyPerson<T>

