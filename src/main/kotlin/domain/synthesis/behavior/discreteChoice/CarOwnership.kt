package domain.synthesis.behavior.discreteChoice

import domain.synthesis.behavior.SurveyInfo
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.employment
import domain.synthesis.behavior.hasLicence
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Employment
import modeling.discreteChoice.structure.NestedStructure
import modeling.discreteChoice.structure.times
import modeling.discreteChoice.utility.nestedLogit
import modeling.models.ChoiceAlternative
import modeling.models.ChoiceSituation
import kotlin.random.Random

/**
 * This class calculates the necessary attributes for the calculation of car ownership using a [SynthesisHousehold]
 * from the population synthesis as input, as well as an [EmploymentSorter] as dependency injection for translating
 * employment types.
 */
@Suppress("MagicNumber") // These magic numbers are ok
class CarOwnershipFactors(
    val household: SynthesisHousehold<out SurveyInfo>,
    employmentSorter: EmploymentSorter = DefaultEmploymentSorter,
) : ChoiceSituation<CarOwnershipAttributes, Int> {
    val size = household.members.size
    val economicStatus: EconomicStatus = household.economicStatus
    val numDrivingLicence: Int = household.members.count { it.hasLicence }
    val numberOfWorkers = household.members.count { employmentSorter.isWorking(it.employment) }
    val isWg = household.members.all { employmentSorter.isUniversityStudent(it.employment) } && size >= 3
    val isOnlyRetired = household.members.all { employmentSorter.isRetired(it.employment) }
    val isOnlyUnemployed = household.members.all { employmentSorter.isUnemployed(it.employment) }
    val amountOfChildren = household.members.count { it.age < 10 }
    val amountOfYouth = household.members.count { it.age in 10..17 }

    override val random: Random = Random.Default
    override fun with(choice: Int) = CarOwnershipAttributes(choice, this)
}

/**
 * For the purpose of demonstrating the translation capabilities we introduce this data class to bring structure in
 * the otherwise clustered [CarOwnershipParameters]. The idea is that the individual parameters of the large parameter
 * set can be extracted and grouped based on their corresponding option. The benefit is that we can design a shared
 * utility function [standardFunction], which is fed with the correct parameter object [CarOwnershipParameters.oneCar] for
 * the option of one car, [CarOwnershipParameters.twoCar] for two cars, etc. Note that this translation approach allows
 * us to rename potentially misleading or unclear parameters to a more fitting name, as well as adding descriptions for
 * documentation purposes.
 *
 * @param oneMember this parameter is the factor by which the utility of a specific option should be shifted if the household has exactly one member.
 * @param twoMembers this parameter is the factor by which the utility of a specific option should be shifted if the household has exactly two members.
 * @param fourOrMoreMembers this parameter is the factor by which the utility  of a specific option should be shifted if the household has at least 4 members.
 * @param lowIncome this parameter is the factor by which the utility of a specific option should be shifted if the household income is considered "low".
 * @param highIncome this parameter is the factor by which the utility of a specific option should be shifted if the household income is considered "high".
 * @param childrenFactor this parameter is the factor by which the utility of a specific option should be shifted if the household has children below the age of 10.
 * @param youthFactor  this parameter is the factor by which the utility of a specific option should be shifted if the household has youths with an age between 10 and 17
 * @param oneLicence  this parameter is the factor by which the utility of a specific option should be shifted if the household has exactly one member with a driving licence
 * @param twoLicences  this parameter is the factor by which the utility of a specific option should be shifted if the household has exactly two members with a driving licence
 * @param threeLicences  this parameter is the factor by which the utility of a specific option should be shifted if the household has exactly three members with a driving licence
 * @param fourOrMoreLicences  this parameter is the factor by which the utility of a specific option should be shifted if the household has 4 or more members with a driving licence
 * @param isFlat  this parameter is the factor by which the utility of a specific option should be shifted if the household is a flat (Only students and at least 3 members)
 * @param isRetired  this parameter is the factor by which the utility of a specific option should be shifted if all members of the household are retired
 * @param isUnemployed  this parameter is the factor by which the utility of a specific option should be shifted if all members of the household are unemployed
 * @param mu the expected value of the normal distribution used for assigning this option to a household.
 * @param sigma the deviation of the normal distirubtion used for assigning this option to a household.
 */
data class CarParameters(
    val oneMember: Double,
    val twoMembers: Double,
    val fourOrMoreMembers: Double,
    val lowIncome: Double,
    val highIncome: Double,
    val childrenFactor: Double,
    val youthFactor: Double,
    val oneWorker: Double,
    val twoWorkers: Double,
    val oneLicence: Double,
    val twoLicences: Double,
    val threeLicences: Double,
    val fourOrMoreLicences: Double,
    val isFlat: Double,
    val isRetired: Double,
    val isUnemployed: Double,
    val mu: Double,
    val sigma: Double,
)

/**
 * New mobiTopp uses one large class for the parameters of a target choice model. This class should contain all
 * parameters that you intend to utilize in your utility functions. You need to add the class name later in the
 * definition of the choice model, so that the program knows which parameters you want to use. In [carChoiceUtility]
 * you can see an example where DiscreteChoiceModel<..., CarOwnerShipParameters> shows where to specify said class
 * name for the program.
 *
 * You will see that sometimes the keyword "data" optionally precedes the definition of the class. There are benefits
 * and disadvantages of specifying the "data" keyword.
 *
 * Advantage: Easy copying and changing the values programmatically; Automatic human readable output.
 * Disadvantage: Unused parameters are not highlighted.
 *
 * In this specific scenario the "data" keyword is deliberately omitted. Note how the parameters "b_education_*" are
 * displayed with a different color than the other parameters in your IDE. These parameters got removed from the
 * utility functions, but the parameters were not deleted. With the help of syntax highlighting we can now easily see
 * when a parameter is not used. You can use that to either realize that you missed a parameter in your utility function
 * or that you no longer require a given parameter and can delete it.
 *
 * You will also most likely see that the IDE flags some parameters with the hint: "This parameter could be private".
 * This happens when the parameter is only used within the confines of the class and nowhere else. In this example
 * the utility function for "household has 3 cars" is the only utility function that uses the raw values found in the
 * parameter class, whereas all other utility functions use the translation feature. This translation happens within
 * the confines of this class only and therefore the parameters are flagged as "Could be private". You may ignore the
 * "Could be private" hint.
 *
 * Another benefit of using a code based parameter definition rather than a text based definition is that we can
 * use code based documentation or "KDoc" to help write documentation for our parameters. As example, you can hover
 * your mouse over the class name "CarOwnershipParameters". You should see this text block as a popup window. This
 * text will also occur at any other position where CarOwnershipParameters is used. That way you can always read the
 * documentation without having to scroll to this text. You can try using this feature by going to [carChoiceUtility]
 * at the end of the file and hover over the class name in DiscreteChoiceModel<..., CarOwnerShipParameters>.
 *
 * Also, we can use the "@property" annotation to add text to a specific parameter. The text will be shown via a popup
 * window similar to the class description when hovering over a specific parameter. For demonstration purposes let's
 * add a description to a parameter.
 *
 * @property b_hh_size_1_on_3 This parameter influences the utility function of having 3 cars in a household when the
 * household contains exactly one member. (Authors note: I expect this parameter to be negative, as I feel that very
 * little households with one person have 3 cars)
 *
 * When you hover over [b_hh_size_1_on_3] in the utility function for 3 cars you will see the description text.
 */
/*Since we omit the "data class" keyword, the constructor is too complex. Also, kotlin does not use snake_case,
rather camelCase, which is why the parameter naming is flagged as incorrect.
*/
@Suppress("PropertyName", "LongParameterList", "ConstructorParameterNaming")
class CarOwnershipParameters(
    val asc_0_mu: Double,
    val asc_0_sig: Double,
    val asc_1_mu: Double,
    val asc_1_sig: Double,
    val asc_2_mu: Double,
    val asc_2_sig: Double,
    val asc_3_mu: Double,
    val asc_3_sig: Double,
    val asc_4: Double,
    val b_hh_size_1_on_1: Double,
    val b_hh_size_1_on_2: Double,
    val b_hh_size_1_on_3: Double,
    val b_hh_size_1_on_4: Double,
    val b_hh_size_2_on_1: Double,
    val b_hh_size_2_on_2: Double,
    val b_hh_size_2_on_3: Double,
    val b_hh_size_2_on_4: Double,
    val b_hh_size_4_on_1: Double,
    val b_hh_size_4_on_2: Double,
    val b_hh_size_4_on_3: Double,
    val b_hh_size_4_on_4: Double,
    val b_income_low_on_1: Double,
    val b_income_low_on_2: Double,
    val b_income_low_on_3: Double,
    val b_income_low_on_4: Double,
    val b_income_high_on_1: Double,
    val b_income_high_on_2: Double,
    val b_income_high_on_3: Double,
    val b_income_high_on_4: Double,
    val b_education_1_on_1: Double,
    val b_education_1_on_2: Double,
    val b_education_1_on_3: Double,
    val b_education_1_on_4: Double,
    val b_education_2_on_1: Double,
    val b_education_2_on_2: Double,
    val b_education_2_on_3: Double,
    val b_education_2_on_4: Double,
    val b_person_age_0_9_on_1: Double,
    val b_person_age_0_9_on_2: Double,
    val b_person_age_0_9_on_3: Double,
    val b_person_age_0_9_on_4: Double,
    val b_person_age_10_17_on_1: Double,
    val b_person_age_10_17_on_2: Double,
    val b_person_age_10_17_on_3: Double,
    val b_person_age_10_17_on_4: Double,
    val b_person_working_1_on_1: Double,
    val b_person_working_1_on_2: Double,
    val b_person_working_1_on_3: Double,
    val b_person_working_1_on_4: Double,
    val b_person_working_2_on_1: Double,
    val b_person_working_2_on_2: Double,
    val b_person_working_2_on_3: Double,
    val b_person_working_2_on_4: Double,
    val b_license_1_on_1: Double,
    val b_license_1_on_2: Double,
    val b_license_1_on_3: Double,
    val b_license_1_on_4: Double,
    val b_license_2_on_1: Double,
    val b_license_2_on_2: Double,
    val b_license_2_on_3: Double,
    val b_license_2_on_4: Double,
    val b_license_3_on_1: Double,
    val b_license_3_on_2: Double,
    val b_license_3_on_3: Double,
    val b_license_3_on_4: Double,
    val b_license_4_on_1: Double,
    val b_license_4_on_2: Double,
    val b_license_4_on_3: Double,
    val b_license_4_on_4: Double,
    val b_shared_flat_on_1: Double,
    val b_shared_flat_on_2: Double,
    val b_shared_flat_on_3: Double,
    val b_shared_flat_on_4: Double,
    val b_hh_retired_on_1: Double,
    val b_hh_retired_on_2: Double,
    val b_hh_retired_on_3: Double,
    val b_hh_retired_on_4: Double,
    val b_hh_unemployed_on_1: Double,
    val b_hh_unemployed_on_2: Double,
    val b_hh_unemployed_on_3: Double,
    val b_hh_unemployed_on_4: Double,
    val lambda_car: Double,
    val lambda_two_more_car: Double,
    val lambda_root: Double,
) {
    /**
     * @property oneCar this property uses the conversion functionality of new mobiTopp to generate a different parameter
     * object for the utility function. In this instance an object of type [CarParameters] is created and used in the
     * utility functions. This property is used later in the [NestedLogitBuilder.option] call, where the translation is
     * set to [oneCar]. The benefit is that only the fields of [CarParameters] are visible. Meaning that the auto completion
     * only suggests valid parameters defined in the Car Parameters.
     *
     * The object is created by translating the corresponding parameters from the main parameter object.
     * The lazy {...} syntax ensures that the object is only created if you use it.
     *
     */
    val oneCar by lazy {
        CarParameters(
            oneMember = b_hh_size_1_on_1,
            twoMembers = b_hh_size_2_on_1,
            fourOrMoreMembers = b_hh_size_4_on_1,
            lowIncome = b_income_low_on_1,
            highIncome = b_income_high_on_1,
            childrenFactor = b_person_age_0_9_on_1,
            youthFactor = b_person_age_10_17_on_1,
            oneWorker = b_person_working_1_on_1,
            twoWorkers = b_person_working_2_on_1,
            oneLicence = b_license_1_on_1,
            twoLicences = b_license_2_on_1,
            threeLicences = b_license_3_on_1,
            fourOrMoreLicences = b_license_4_on_1,
            isFlat = b_shared_flat_on_1,
            isRetired = b_hh_retired_on_1,
            isUnemployed = b_hh_unemployed_on_1,
            mu = asc_1_mu,
            sigma = asc_1_sig
        )
    }

    /**
     * The parameter set for assigning 2 cars to an input household. Behaves similar to [oneCar]
     * @see [oneCar].
     */
    val twoCar by lazy {
        CarParameters(
            oneMember = b_hh_size_1_on_2,
            twoMembers = b_hh_size_2_on_2,
            fourOrMoreMembers = b_hh_size_4_on_2,
            lowIncome = b_income_low_on_2,
            highIncome = b_income_high_on_2,
            childrenFactor = b_person_age_0_9_on_2,
            youthFactor = b_person_age_10_17_on_2,
            oneWorker = b_person_working_1_on_2,
            twoWorkers = b_person_working_2_on_2,
            oneLicence = b_license_1_on_2,
            twoLicences = b_license_2_on_2,
            threeLicences = b_license_3_on_2,
            fourOrMoreLicences = b_license_4_on_2,
            isFlat = b_shared_flat_on_2,
            isRetired = b_hh_retired_on_2,
            isUnemployed = b_hh_unemployed_on_2,
            mu = asc_2_mu,
            sigma = asc_2_sig
        )
    }

    /**
     * The parameter set for assigning 3 cars to an input household. Behaves similar to [oneCar]
     * @see [oneCar].
     */
    val threeCar by lazy {
        CarParameters(
            oneMember = b_hh_size_1_on_3,
            twoMembers = b_hh_size_2_on_3,
            fourOrMoreMembers = b_hh_size_4_on_3,
            lowIncome = b_income_low_on_3,
            highIncome = b_income_high_on_3,
            childrenFactor = b_person_age_0_9_on_3,
            youthFactor = b_person_age_10_17_on_3,
            oneWorker = b_person_working_1_on_3,
            twoWorkers = b_person_working_2_on_3,
            oneLicence = b_license_1_on_3,
            twoLicences = b_license_2_on_3,
            threeLicences = b_license_3_on_3,
            fourOrMoreLicences = b_license_4_on_3,
            isFlat = b_shared_flat_on_3,
            isRetired = b_hh_retired_on_3,
            isUnemployed = b_hh_unemployed_on_3,
            mu = asc_3_mu,
            sigma = asc_3_sig
        )
    }

    /**
     * The parameter set for assigning 4 cars to an input household. Behaves similar to [oneCar]
     * @see [oneCar].
     */
    val fourCar by lazy {
        CarParameters(
            oneMember = b_hh_size_1_on_4,
            twoMembers = b_hh_size_2_on_4,
            fourOrMoreMembers = b_hh_size_4_on_4,
            lowIncome = b_income_low_on_4,
            highIncome = b_income_high_on_4,
            childrenFactor = b_person_age_0_9_on_4,
            youthFactor = b_person_age_10_17_on_4,
            oneWorker = b_person_working_1_on_4,
            twoWorkers = b_person_working_2_on_4,
            oneLicence = b_license_1_on_4,
            twoLicences = b_license_2_on_4,
            threeLicences = b_license_3_on_4,
            fourOrMoreLicences = b_license_4_on_4,
            isFlat = b_shared_flat_on_4,
            isRetired = b_hh_retired_on_4,
            isUnemployed = b_hh_unemployed_on_4,
            mu = asc_4,
            sigma = 0.0
        )
    }
}

/**
 * The class [CarOwnershipAttributes] is an example to show the attributes that are required to form a meaningful decision
 * in the [carChoiceUtility] discrete choice model. In old mobitopp the attributes would have been a helper function written
 * in all caps like "HOUSEHOLD_SIZE". Here the attributes are encapsulated in the specific discrete choice model.
 *
 * Note that the approach to create the attributes class can be implemented in multiple different ways. In this concrete
 * example we delegate all necessary factors into an own object infos of type [CarOwnershipFactors] and extract the
 * attributes that we desire by writing (val: attribute = infos.attribute). There exist multiple different ways to
 * build the Attributes object and this approach is not the definite answer to always create attributes for an utility
 * function.
 *
 * The benefit of creating individual properties instead of methods for the utility function is readability.
 * it.size == 1 reads more fluent
 * than writing it.size() == 1 (writing as a method) and much more fluent than writing it.household.members.count() == 1
 * (writing as code).
 *
 *
 * @property size This helper attribute returns the amount of people in the household as an integer.
 * @property economicStatus This helper attribute returns the assigned economic status [EconomicStatus] of the household.
 * @property amountOfChildren This helper attribute returns the number of agents in the household that are of age [0, 10)
 * @property amountOfYouth This helper attribute returns the number of agents in the household that are of age [10, 17]
 * @property amountOfWorkers This helper attribute returns the number of agents in the household that are considered
 * "Working" by the [EmploymentSorter]. The default logic is that agents with [Employment.FULLTIME] and [Employment.PARTTIME] are
 * considered "working". You can override this behaviour by passing a different [EmploymentSorter] to the [CarOwnershipFactors]
 * creation.
 * @property amountOfLicences This helper attribute returns the number of agents in the household that have a driving licence
 * @property isWg determines whether the selected household is a flat, as in at least 3 or more students and only students,
 * based on the [EmploymentSorter] logic to determine what employment type qualifies as student (Default is [Employment.STUDENT_TERTIARY])
 * @property isOnlyRetired This helper attribute returns whether all agents in the household are retired, based on the [EmploymentSorter]
 * to determine retirement.
 *  @property isOnlyUnemployed This helper attribute returns whether all agents in the household are unemployed, based on the [EmploymentSorter]
 *  to determine unemployment.
 */
class CarOwnershipAttributes(
    override val choice: Int,
    infos: CarOwnershipFactors
) : ChoiceAlternative<Int>() {
    val randomNumber = 0.0
    val size = infos.size
    val economicStatus = infos.economicStatus
    val amountOfChildren = infos.amountOfChildren
    val amountOfYouth = infos.amountOfYouth
    val amountOfWorkers = infos.numberOfWorkers
    val amountOfLicences = infos.numDrivingLicence
    val isWg = infos.isWg
    val isOnlyRetired = infos.isOnlyRetired
    val isOnlyUnemployed = infos.isOnlyUnemployed
}

/**
 * This interface provides an interaction point to define different behaviours to determine whether a person is considered
 * working, student, retired or unemployed. If you ever need a different behaviour to determine what should be considered
 * either of these attributes for the utility function you can simply pass a different Employment sorter to the creation
 * of the [CarOwnershipFactors] and your logic will be used instead. The idea behind this design decision is to avoid
 * hard coding decisions into the utility function, and rather provide the end user (You) with the option to flexibly
 * change the behaviour without needing to go through the entire code base.
 */
interface EmploymentSorter {
    /**
     * Determines whether an employment should be considered working.
     * @param employment the [Employment] to evaluate.
     */
    fun isWorking(employment: Employment): Boolean

    /**
     * Determines whether an employment should be considered being a student.
     * @param employment the [Employment] to evaluate.
     */
    fun isUniversityStudent(employment: Employment): Boolean

    /**
     * Determines whether an employment should be considered retired.
     * @param employment the [Employment] to evaluate.
     */
    fun isRetired(employment: Employment): Boolean

    /**
     * Determines whether an employment should be considered unemployed.
     * @param employment the [Employment] to evaluate.
     */
    fun isUnemployed(employment: Employment): Boolean
}

/**
 * The standard instantiation for an [EmploymentSorter]. Note that there is a keyword "object" instead of "class".
 * This makes the [DefaultEmploymentSorter] a singleton. The benefit is that you can reference it writing DefaultEmploymentSorter
 * instead of DefaultEmploymentSorter() (no brackets). In this particular scenario an "object" instantiation is adequate as there
 * is no requirement for different object state (actually the DefaultEmploymentSorter has no interactable state at all)
 *
 * The logic behaves as follows:
 * [Employment.PARTTIME] and [Employment.FULLTIME] are considered "Working" Employments.
 * [Employment.STUDENT_TERTIARY] is considered a university student.
 * [Employment.RETIRED] is considered retired
 * [Employment.UNEMPLOYED] is considered unemployed.
 * All other employments fall in neither of these categories.
 */
object DefaultEmploymentSorter : EmploymentSorter {
    /**
     * For performance reasons it is smart to keep a fixed reference to the set of occupations that are considered
     * working employments.
     */
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

/*
 * You can also define other helpers that you would like to utilize in your utility functions. Here we give two examples
 * for writing a helper attribute for economic status. Do not be afraid of the keywords:
 * [private] means: "visible in this File only" (Because we do not want to spam the codebase with our additions)
 * [inline] means: "Be a bit quicker in the calculation"
 *
 * [val] means: "we want a property instead of a method because writing it.poor reads better than it.poor()."
 * EconomicStatus.xxx is an extension property. if you get an economic status somewhere in the code (For example in the
 * utility functions below) you can simply write economicStatus.poor and this helper is run.
 *
 * [get() =] this snippet is necessary because we do not have a backing field. Not necessary to understand what that means
 */
/**
 * @property poor Determines whether an economic status should be considered poor in the utility function. This is the
 * case if the [EconomicStatus] is either [EconomicStatus.VERY_LOW] or [EconomicStatus.LOW]
 */
private inline val EconomicStatus.poor get(): Boolean = this == EconomicStatus.LOW || this == EconomicStatus.VERY_LOW

/*
Absolutely identical to the definition above. Only that in this case you need to write brackets in the utility function
instead.
 */
private fun EconomicStatus.poor(): Boolean {
    return this == EconomicStatus.LOW || this == EconomicStatus.VERY_LOW
}

/**
 * @property rich Determines whether an economic status should be considered poor in the utility function. This is the
 * case if the [EconomicStatus] is either [EconomicStatus.VERY_HIGH] or [EconomicStatus.HIGH]
 */
private inline val EconomicStatus.rich get(): Boolean = this == EconomicStatus.HIGH || this == EconomicStatus.VERY_HIGH

/**
 * Here we can see one benefit of being able to translate the large [CarOwnershipParameters] to something different,
 * like a [CarParameters] object. We can define a standard utility function which takes in a [CarParameters] object and
 * the translation process automatically fills the utility function with the corresponding parameters. In this example
 * we will receive [CarOwnershipParameters.oneCar] for the utility function of one car [CarOwnershipParameters.twoCar] for
 * two cars and so on. Since the utility function is exactly identical for all calculations we can save repeatedly needing
 * to write the same utility function.
 * @property standardFunction This is the standard utility function for car ownership as found in the Rastatt model of
 * mobitopp. Used later in the discrete choice model definition.
 */
@Suppress("MagicNumber") // These magic numbers are ok, the content of the function should still be understandable
private val standardFunction: CarParameters.(CarOwnershipAttributes) -> Double = {
    mu +
        sigma * it.randomNumber +
        (it.size == 1) * oneMember +
        (it.size == 2) * twoMembers +
        (it.size >= 4) * fourOrMoreMembers +

        (it.economicStatus.poor) * lowIncome +
        (it.economicStatus.rich) * highIncome +
        (it.amountOfChildren >= 1) * childrenFactor +
        (it.amountOfYouth >= 1) * youthFactor +
        (it.amountOfWorkers == 1) * oneWorker +
        (it.amountOfWorkers == 2) * twoWorkers +

        (it.amountOfLicences == 1) * oneLicence +
        (it.amountOfLicences == 2) * twoLicences +
        (it.amountOfLicences == 3) * threeLicences +
        (it.amountOfLicences >= 4) * fourOrMoreLicences +

        (it.isWg) * isFlat +
        (it.isOnlyRetired) * isRetired +
        (it.isOnlyUnemployed) * isUnemployed
}

/**
 * Here we design the discrete choice model we want to be using for car ownership. We are interested in using our
 * attributes defined in [CarOwnershipAttributes] and the parameters defined in [CarOwnershipParameters]. We create
 * a discrete choice model and pass a nested logit as distribution function to determine the probabilities.
 */

/*This Annotation tells the code analysis tool to ignore numbers that occur
inexplicably in the code. The tool cannot differentiate between application code and choice model configuration and
starts complaining, if you never write application code or run the tool for code quality you can simply accept the
existence of this annotation + comment as something you don't care about*/
@Suppress("MagicNumber")
val carChoiceUtility = NestedStructure<Int, CarOwnershipAttributes, CarOwnershipParameters> {
    /*
    Calling NestedStructure tells the program that you want to build a nested logit for your discrete choice model.
    Note that "root" automatically assigns a nest with lambda = 1.0. So the parameter lambda_root is no longer required.
     */

    /*
    To add an option within a nest you can simply write option("Number") to define the utility function for said number.
    Since CarOwnershipAttributes is a choice situation over a whole number (You can see that in the class definition
    CarOwnershipParameters: ChoiceSituation<Int> - that tells the program that you want to build a choice model
    for whole numbers - Integers). Afterward, you can build the utility function. For no car it is currently just 0.0
     */
    option(0) {
        0.0
    }

    /*
    If you want to build a nest you can write nest()  {...}.
    You need to specify the lambda parameter within curly brackets i.e. nest({lambda_car}).
    The curly brackets are sadly a computational necessity and cannot be omitted.
     */
    nest("exactly 1 car", { lambda_car }) {

        /*
         Note how you can write the utility function either by writing option()  { UtilityFunction }
         or by passing a reference via option(utilityFunction = standardFunction). the parameters = {} translation
         automatically determines the appropriate parameters.
         */
        option(1, parameters = { oneCar }, utilityFunction = standardFunction)
    }

    nest("two or more cars", { lambda_two_more_car }) {
        /*
        You can also use the full language syntax of kotlin in your utility function. Here we have an example
        using the when(...) {} block, which is a miniscule amount faster than the handwritten utility function.
        (Though it is not as readable as the standardFunction)
        Also, you don't need to explicitly write parameters = {...}, the curly brackets are automatically assumed
        to be the parameter translation.
         */
        option(2, { twoCar }) {
            mu +
                sigma * it.randomNumber +
                when (it.size) {
                    1 -> oneMember
                    2 -> twoMembers
                    in 4..Int.MAX_VALUE -> fourOrMoreMembers
                    else -> 0.0
                } +
                when (it.economicStatus) {
                    EconomicStatus.LOW, EconomicStatus.VERY_LOW -> lowIncome
                    EconomicStatus.HIGH, EconomicStatus.VERY_HIGH -> highIncome
                    EconomicStatus.MIDDLE -> 0.0
                } +
                (it.amountOfChildren >= 1) * childrenFactor +

                (it.amountOfYouth >= 1) * youthFactor +
                when (it.amountOfWorkers) {
                    1 -> oneWorker
                    2 -> twoWorkers
                    else -> 0.0
                } +
                when (it.amountOfLicences) {
                    1 -> oneLicence
                    2 -> twoLicences
                    3 -> threeLicences
                    in 4..Int.MAX_VALUE -> fourOrMoreLicences
                    else -> 0.0
                } +
                (it.isWg) * isFlat +
                (it.isOnlyRetired) * isRetired +
                (it.isOnlyUnemployed) * isUnemployed
        }

        /*
        If you do not specify a parameters = {...} translation you get the entire parameter object, which you can then
        use in your utility function. Here you can theoretically use b_hh_size_1_on_1, even though that parameter
        is not intended to be used in the utility function for 3 cars. (You can also very easily mistype and mess up
        the utility function, so be moderately careful)
         */
        option(3) {
            asc_3_mu +
                asc_3_sig * it.randomNumber +
                (it.size == 1) * b_hh_size_1_on_3 +
                (it.size == 2) * b_hh_size_2_on_3 +
                (it.size >= 4) * b_hh_size_4_on_3 +

                (it.economicStatus.poor) * b_income_low_on_3 +
                (it.economicStatus.rich) * b_income_high_on_3 +
                (it.amountOfChildren >= 1) * b_person_age_0_9_on_3 +

                (it.amountOfYouth >= 1) * b_person_age_10_17_on_3 +
                (it.amountOfWorkers == 1) * b_person_working_1_on_3 +
                (it.amountOfWorkers == 2) * b_person_working_2_on_3 +

                (it.amountOfLicences == 1) * b_license_1_on_3 +
                (it.amountOfLicences == 2) * b_license_2_on_3 +
                (it.amountOfLicences == 3) * b_license_3_on_3 +
                (it.amountOfLicences >= 4) * b_license_4_on_3 +

                (it.isWg) * b_shared_flat_on_3 +
                (it.isOnlyRetired) * b_hh_retired_on_3 +
                (it.isOnlyUnemployed) * b_hh_unemployed_on_3
        }

        /*
        the standardFunction is not written in stone. You can modify it and perform amendments,
        so if for example the utility function needs to do some calculation specifically only for 4 cars, you could add
        it here and reuse the code for the utility function that you had already written. (For this example we added a
         + it.isWg * 0.0 line.)

         Sadly the invocation of standardFunction(this, it) is a bit cryptic, but necessary if you intend to use the
         amendment approach.
         */
        option(4, parameters = { fourCar }) {
            standardFunction(this, it) +
                it.isWg * 0.0
        }
    }
}.nestedLogit("ExampleNestedNumberOfCarsModel")

// fun <T> KnownDiscreteChoiceModel<Int, CarOwnershipAttributes, T>.select(
//    household: SynthesisHousehold<out SurveyInfo>
// ): Int {
//    return select { CarOwnershipAttributes(it, household.toCarOwnershipAttributes()) }
// }
