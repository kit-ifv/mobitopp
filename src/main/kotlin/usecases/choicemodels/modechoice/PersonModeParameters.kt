package usecases.choicemodels.modechoice

import datastructure.StationaryAction
import domain.data.EconomicStatus
import domain.data.Person
import domain.data.Sex
import domain.enums.Mode
import modeling.discreteChoice.D
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes

/**
 * Encapsulates all the attributes of a person that may influence the mode choice. Collects all the attributes in a
 * [ModePersonParameterBlock] for further analysis.
 *
 * Currently, the attributes that are considered for mode choice are Age, Number of Cars, Economic Status, Previous Mode
 * Commuter Ticket, Gender, Driving Licence and Next Activity.
 *
 * @property constant this factor is added to the utility function
 */
interface PersonModeParameters {
    val constant: Double
    val purposes: ChoiceModelPurposes
    fun evaluateAge(person: ModePersonScope): Double = 0.0
    fun evaluateNumberOfCars(person: ModePersonScope): Double = 0.0
    fun evaluateEconomicStatus(person: ModePersonScope): Double = 0.0

    /**
     * Some utility functions shift the utility if the mode of the last trip matches a specific transport mode. This function
     * implements the behaviour and returns a shift value if the previous mode meets a condition specified in the subinterfaces
     * Most implementations simply check whether the previous mode is the same as the mode for which the utility is calculated
     * right now. Technically, any behaviour could be assigned to this function, such as assigning a target value if the
     * previous mode is PT, and a different value if it is carsharing.
     *
     * @param person The target mode scope encapsulating the relevant information for calculating the mode utility
     * @return a double by which the utility should be shifted if whatever condition specified in the function is met.
     */
    fun evaluatePreviousMode(person: ModePersonScope): Double = 0.0
    fun evaluateCommuterTicket(person: ModePersonScope): Double = 0.0
    fun evaluateGender(person: ModePersonScope): Double = 0.0
    fun evaluateDrivingLicence(person: ModePersonScope): Double = 0.0
    fun evaluateNextActivity(person: ModePersonScope): Double = 0.0
    fun evaluate(person: ModePersonScope) = constant + toParameters(person).toDouble()
    fun toParameters(person: ModePersonScope): ModePersonParameterBlock {
        return ModePersonParameterBlock(
            evaluateAge(person),
            evaluateNumberOfCars(person),
            evaluateEconomicStatus(person),
            evaluatePreviousMode(person),
            evaluateCommuterTicket(person),
            evaluateGender(person),
            evaluateDrivingLicence(person),
            evaluateNextActivity(person)
        )
    }
}

/**
 * This interface provides a standard evaluation for parameter
 *
 * @property female this parameter value is added to the parameter sum if the target agent is
 * female.
 */
@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
interface StandardGender : PersonModeParameters {
    val female: Double
    override fun evaluateGender(person: ModePersonScope): Double {
        return if (person.person.sex == Sex.FEMALE) female else 0.0
    }
}

/**
 * Provides a standard evaluation of the possession of a driving licence for the target person
 *
 * @property drivingLicence the parameter applied to the utility function if the target person is in possession of a
 * driving licence.
 */
interface StandardDrivingLicence : PersonModeParameters {
    val drivingLicence: Double
    override fun evaluateDrivingLicence(person: ModePersonScope): Double {
        return if (person.person.hasLicense) drivingLicence else 0.0
    }
}

/**
 * Provides a standard evaluation of the economic status of a person for the mode choice utility. This function requires
 * a [economicStatus] parameter in implementors and calculates the parameter based on
 *
 * [economicStatus] * (1, 2, 3, 4, 5) depending on the economic status. Note that currently the number corresponds to
 * [EconomicStatus.code]. But to be futureproof this code has been unrolled in this function, so that if the encoding
 * changes the utility function remains untouched.
 *
 * @property economicStatus this parameter is multiplied with the economic status of the persons household.
 */
@Suppress("MagicNumber")
interface StandardEconomicStatus : PersonModeParameters {
    val economicStatus: Double
    override fun evaluateEconomicStatus(person: ModePersonScope): Double {
        return when (person.person.household.economicStatus) {
            EconomicStatus.VERY_LOW -> 1 * this.economicStatus
            EconomicStatus.LOW -> 2 * this.economicStatus
            EconomicStatus.MIDDLE -> 3 * this.economicStatus
            EconomicStatus.HIGH -> 4 * this.economicStatus
            EconomicStatus.VERY_HIGH -> 5 * this.economicStatus
        }
    }
}

/**
 * Provides a standard implementation to evaluate the influence of the number of cars onto a utility block in the mode
 * choice.
 *
 * [numberOfCars] * NUMBER_OF_CARS_IN_HOUSEHOLD
 *
 * @property numberOfCars the parameter multiplied with the number of cars in the household when the person is
 */
interface StandardCars : PersonModeParameters {
    val numberOfCars: Double
    override fun evaluateNumberOfCars(person: ModePersonScope): Double {
        return person.person.household.cars.size * numberOfCars
    }
}

/**
 * This interface provides a standard implementation for evaluating the next activity. The original utility functions
 * had a high degree of difference in the influence of the next activity depending on the target block. Most travel
 * times separated by work business and leisure, whereas the constant blocks separated along 6 activity groups [StandardActivities]
 *
 * This interface provides the implementation based on the original separation for travel time parameters and separates
 * into 3 groups of activity types.
 * @property work this parameter is applied to the utility function if the activity type is [LegacyActivityType.WORK]
 * @property leisure this parameter is applied to the utility function if the activity type is [LegacyActivityType.LEISURE],
 * [LegacyActivityType.LEISURE_INDOOR],  [LegacyActivityType.LEISURE_OUTDOOR],  [LegacyActivityType.LEISURE_OTHER],
 * [LegacyActivityType.LEISURE_WALK],  [LegacyActivityType.LEISURE_SIGHTSEEING],  [LegacyActivityType.PRIVATE_VISIT]
 * @property business this parameter is applied to the utility function if the activity type is  [LegacyActivityType.BUSINESS]
 */

interface TravelTimeActivities : PersonModeParameters {
    val work: Double
    val leisure: Double
    val business: Double
    override fun evaluateNextActivity(person: ModePersonScope): Double {
        return when (person.nextActivity.type) {
            purposes.work -> work
            purposes.business -> business
            in purposes.leisureTypes -> leisure
//            LegacyActivityType.LEISURE,
//            LegacyActivityType.LEISURE_INDOOR,
//            LegacyActivityType.LEISURE_OUTDOOR,
//            LegacyActivityType.LEISURE_OTHER,
//            LegacyActivityType.LEISURE_WALK,
//            LegacyActivityType.LEISURE_SIGHTSEEING,
//            LegacyActivityType.PRIVATE_VISIT
            else -> 0.0
        }
    }
}

/**
 * Provides a standard implementation for assigning a parameter for the mode utility function depending on whether
 * a person is in possesion of a commuter ticket.
 *
 * @property commuterTicket this parameter is added to the utility function if the person is in possession of a commuter
 * ticket
 */
interface StandardHasCommuterTicket : PersonModeParameters {
    val commuterTicket: Double
    override fun evaluateCommuterTicket(person: ModePersonScope): Double {
        return commuterTicket * person.person.hasCommuterTicket.D
    }
}

/**
 * Provides a standard implementation for assigning parameters to a given activity type. This implementation is based on
 * the original constant factors of a utility function which separates into 6 different [LegacyActivityType] groupings.
 *
 * @property work this parameter is applied to the utility function when the activity type is [LegacyActivityType.WORK]
 * @property education this parameter is applied to the utility function when the activity type corresponds with any
[LegacyActivityType.EDUCATION], [LegacyActivityType.EDUCATION_PRIMARY], [LegacyActivityType.EDUCATION_SECONDARY],
[LegacyActivityType.EDUCATION_TERTIARY], [LegacyActivityType.EDUCATION_OCCUP]
 * @property business this parameter is applied to the utility function when the activity type is [LegacyActivityType.BUSINESS]
 * @property leisure this parameter is applied to the utility function when the activity type is in [LegacyActivityType.LEISURE]
[LegacyActivityType.LEISURE], [LegacyActivityType.PRIVATE_VISIT], [LegacyActivityType.LEISURE_INDOOR], [LegacyActivityType.LEISURE_OUTDOOR],
[LegacyActivityType.LEISURE_OTHER], [LegacyActivityType.LEISURE_SIGHTSEEING], [LegacyActivityType.LEISURE_WALK]

 * @property service this parameter is applied to the utility function when the activity type is [LegacyActivityType.SERVICE]
 * @property shopping this parameter is applied to the utility function when the activity type is
 * [LegacyActivityType.SHOPPING], [LegacyActivityType.PRIVATE_BUSINESS], [LegacyActivityType.SHOPPING_DAILY],
 * [LegacyActivityType.SHOPPING_OTHER]
 *
 */
interface StandardActivities : PersonModeParameters {

    val work: Double
    val education: Double
    val business: Double
    val leisure: Double
    val service: Double
    val shopping: Double

    override fun evaluateNextActivity(person: ModePersonScope): Double {
        return when (person.nextActivity.type) {
            purposes.work -> work

            purposes.business -> business

            purposes.service -> service

            in purposes.educationTypes -> education

            in purposes.shoppingTypes -> shopping

            in purposes.leisureTypes -> leisure

            else -> 0.0
        }
    }
}

/**
 * This interface overrides the default implementation of [PersonModeParameters.evaluateNextActivity] to avoid accidental
 * usage of the default implementation when a custom implementation is desired.
 *
 * Add this interface to a parameter composition if the next activity type is a relevant factor and no standard implementation
 * suits your needs.
 */
interface CustomNextActivity : PersonModeParameters {
    override fun evaluateNextActivity(person: ModePersonScope): Double
}

/**
 * This interface overrides the default implementation of [PersonModeParameters.evaluateAge] to avoid accidental
 * usage of the default implementation when a custom implementation is desired.
 *
 * Add this interface to a parameter composition if the agent age is a relevant factor and no standard implementation
 * suits your needs.
 */

interface CustomAge : PersonModeParameters {

    override fun evaluateAge(person: ModePersonScope): Double
}

/**
 * This interface overrides the default implementation of [PersonModeParameters.evaluateEconomicStatus] to avoid accidental
 * usage of the default implementation when a custom implementation is desired.
 *
 * Add this interface to a parameter composition if the economic status is a relevant factor and no standard implementation
 * suits your needs.
 */

interface CustomEconomicStatus : PersonModeParameters {
    override fun evaluateEconomicStatus(person: ModePersonScope): Double
}

/**
 * This interface overrides the default implementation of [PersonModeParameters.evaluatePreviousMode] to avoid accidental
 * usage of the default implementation when a custom implementation is desired.
 *
 * Add this interface to a parameter composition if the previous mode is a relevant factor and no standard implementation
 * suits your needs.
 * @property mode Some utility functions require knowledge of the mode of the previous trip, such as assigning car a higher
 * utility if the previous trip has been taken by car. This field assigns the specific mode for which the calculation should
 * look out for.
 */

interface CustomPreviousMode : PersonModeParameters {
    val mode: Mode

    override fun evaluatePreviousMode(person: ModePersonScope): Double
}

/**
 * The standard age evaluation for mode choice separates the age in three distinct age groups. 0-17, 18-29,30-49.
 * The other ages do not receive a parameter to shift the mode choice.
 *
 * @property minors the parameter used for the age group of agents aged 0 to 17
 * @property youngAdults the parameter used for the age group of agents aged 18 to 29
 * @property adults the parameter used for the age group of agents aged 30 to 49
 */
interface StandardAge : PersonModeParameters {

    val minors: Double
    val youngAdults: Double
    val adults: Double

    @Suppress("MagicNumber")
    override fun evaluateAge(person: ModePersonScope): Double {
        return when (person.person.age) {
            in 0..17 -> minors
            in 18..29 -> youngAdults
            in 30..49 -> adults
            else -> 0.0
        }
    }
}

class ModePersonScope(
    val person: Person,
    val nextActivity: StationaryAction,
    val previousMode: Mode
)

data class ModePersonParameterBlock(
    val age: Double,
    val numCars: Double,
    val economicStatus: Double,
    val previousMode: Double,
    val commuterTicket: Double,
    val gender: Double,
    val drivingLicence: Double,
    val nextActivity: Double
) {
    fun toDouble(): Double {
        return age + numCars + economicStatus + previousMode + commuterTicket + gender + drivingLicence + nextActivity
    }
}
