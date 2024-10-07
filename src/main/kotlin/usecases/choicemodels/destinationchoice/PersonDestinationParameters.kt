package usecases.choicemodels.destinationchoice

import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.Person
import domain.enums.ActivityType
import domain.enums.Mode
import domain.location.Location
import utils.units.AbsoluteTime

/**
 * All parameters that may influence a utility function for destination are bundled in this interface. The idea is
 * that sub-interfaces may provide an implementation which realizes the determination of the parameter value for a given
 * attribute. This interface collects the parameters and returns a [PersonParameterBlock] object which contains all the relevant
 * numeric parameters for a target [Person]
 * @property [base] the constant parameter that shall be always applied to the block. [base] + (optional calculations)
 */
interface PersonDestinationParameters {
    val base: Double

    /**
     * Determine the parameter value (a double) for the age of a person
     *
     * @param person the target person
     * @return 0.0 as default parameter
     */
    fun getAge(person: Person): Double = 0.0
    fun getEmployment(person: Person): Double = 0.0
    fun getCommuterTicket(person: Person) = 0.0
    fun getCarSurplus(person: Person) = 0.0
    fun getEconomicStatus(person: Person) = 0.0

    /**
     * Generate a parameter block from a target person.
     *
     * @param person target person
     * @return a [PersonParameterBlock] for destination
     */
    fun toParameters(person: Person): PersonParameterBlock {
        return PersonParameterBlock(
            base,
            getAge(person),
            getEmployment(person),
            getCommuterTicket(person),
            getCarSurplus(person),
            getEconomicStatus(person)
        )
    }
}

/**
 * A Person Scope contains all the attributes of a person that are used in the destination utility calculation
 *
 * @property age
 * @property employment
 * @property hasCommuterTicket
 * @property householdHasMoreCarsThanAdults
 * @property economicStatus
 * @property nextActivityType
 * @property availableModes
 * @property time
 * @property nextFixedActivityLocation the next fixed location, if present.
 */
data class PersonScope(
    val age: Int,
    val employment: Employment,
    val hasCommuterTicket: Boolean,
    val householdHasMoreCarsThanAdults: Boolean,
    val economicStatus: EconomicStatus,
    val nextActivityType: ActivityType,
    val availableModes: Collection<Mode>,
    val time: AbsoluteTime,
    val nextFixedActivityLocation: Location
)

/**
 * The person parameter block encapsulates the parameters for the different attributes of a person that may take influence
 * onto a utility function.
 *
 * @property base the base numeric factor
 * @property age the numeric factor for the utility function determined by the [Person.age]
 * @property employment the numeric factor for the utility function determined by [Person.employment]
 * @property commuterTicket the numeric factor for the utility function determined by [Person.hasCommuterTicket]
 * @property caravan the numeric factor applied if the household has more cars than adults.
 * @property highIncome the numeric factor applied to the utiltiy function by the household economic status.
 */
data class PersonParameterBlock(
    val base: Double,
    val age: Double,
    val employment: Double,
    val commuterTicket: Double,
    val caravan: Double,
    val highIncome: Double,

) {
    /**
     * Evaluates the parameters by summation
     *
     * @return the sum of all numeric values
     */
    fun toDouble(): Double = base + age + employment + commuterTicket + caravan + highIncome
}

/**
 * Adds age to the parameter composition. The standard age evaluation provides parameters for 5 age groups, so
 * by implementing this interface the inheriting class must provide parameter values for these age groups.
 *
 * @property [youngAdults] Apply this parameter to a utility function block if the target [Person] is between 18 and 29 years old.
 * @property [adults] Apply this parameter to a utility function block if the target [Person] is between 30 and 39 years old.
 * @property [seniorAdults] Apply this parameter to a utility function block if the target [Person] is between 40 and 49 years old.
 * @property [seniors] Apply this parameter to a utility function block if the target [Person] is between 50 and 69 years old.
 * @property [venerables] Apply this parameter to a utility function block if the target [Person] is between 70 and 120 years old.
 */
interface DestinationAge : PersonDestinationParameters {

    val youngAdults: Double
    val adults: Double
    val seniorAdults: Double
    val seniors: Double
    val venerables: Double

    /**
     * This function determines the appropriate parameter for the age of a [person]. The parameter is determined by
     * grouping the age into specific age blocks.
     *
     * {18,29}, {30,39}, {40,49}, {50,69} & {70,120}
     *
     * Note since this function is a replica from the legacy destination choice model there is a cutoff where people
     * aged above 120 will not be assigned a parameter. The oldest documented age is above 120, so this could theoretically
     * cause problems.
     *
     * @param person the target person for which a parameter should be selected from the set
     * @return the appropriate age parameter, if the age matches a target group.
     */
    @Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
    override fun getAge(person: Person): Double {
        return when (person.age) {
            in 18..29 -> youngAdults
            in 30..39 -> adults
            in 40..49 -> seniorAdults
            in 50..69 -> seniors
            in 70..120 -> venerables
            else -> 0.0
        }
    }
}

/**
 * This interface provides a standard implementation for determining parameters in regard to the employment status of
 * a person. The standard implementation only differentiates between
 *
 * educational occupations: ( [Employment.STUDENT],
 * [Employment.STUDENT_PRIMARY], [Employment.STUDENT_SECONDARY], [Employment.STUDENT_TERTIARY], [Employment.EDUCATION]).
 *
 * and work occupations ([Employment.FULLTIME], [Employment.PARTTIME], [Employment.MARGINAL])
 *
 * @property [employmentEducation] this parameter is used if the target person has an educational occupation
 * @property [employmentWork] this parameter is used if the target person has a work occupation
 */
interface DestinationEmployment : PersonDestinationParameters {
    val employmentEducation: Double
    val employmentWork: Double
    override fun getEmployment(person: Person): Double {
        return when (person.employment) {
            Employment.STUDENT,
            Employment.STUDENT_PRIMARY,
            Employment.STUDENT_SECONDARY,
            Employment.STUDENT_TERTIARY,
            Employment.EDUCATION -> employmentEducation
            Employment.FULLTIME,
            Employment.PARTTIME,
            Employment.MARGINAL -> employmentWork
            else -> 0.0
        }
    }
}

/**
 * This Interface provides an implementation to determine whether a household has at least one car for each adult member.
 *
 * @property surplusCars this parameter is applied if the number of cars in a household >= number of adults.
 */
@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
interface SurplusCars : PersonDestinationParameters {
    val surplusCars: Double
    override fun getCarSurplus(person: Person): Double {
        return if (person.household.cars.size >=
            person.household.members.filter { it.age >= 18 }.size
        ) {
            surplusCars
        } else {
            0.0
        }
    }
}

/**
 * This interface provides a standard implementation to determine whether a person has a commuter ticket and a parameter
 * which should be applied to the utility parameter block if the ticket is present
 *
 * @property commuterTicket this parameter is added to the utility if the person has a commuter ticket.
 */

interface DestCommuterTicket : PersonDestinationParameters {
    val commuterTicket: Double
    override fun getCommuterTicket(person: Person): Double {
        return if (person.hasCommuterTicket) commuterTicket else 0.0
    }
}

/**
 * This interface provides a standard implementation to determine whether a person is rich (Household status in category
 * 4 or 5) and should use the [highIncome] parameter in the utility function
 *
 * @property highIncome this parameter is applied to the utility function if the person is from a HIGH or VERY_HIGH household
 * economic status
 */
interface DestinationEconomicStatus : PersonDestinationParameters {
    val highIncome: Double
    override fun getEconomicStatus(person: Person): Double =
        if (person.household.economicStatus == EconomicStatus.HIGH ||
            person.household.economicStatus == EconomicStatus.VERY_HIGH
        ) {
            highIncome
        } else {
            0.0
        }
}
