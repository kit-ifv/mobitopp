package domain.synthesis.behavior.cars.choicemodels.parameters

/**
 * New mobiTopp uses one large class for the parameters of a target choice model. This class should contain all
 * parameters that you intend to utilize in your utility functions. You need to add the class name later in the
 * definition of the choice model, so that the program knows which parameters you want to use. In [domain.synthesis.behavior.discreteChoice.carChoiceUtility]
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
 * documentation without having to scroll to this text. You can try using this feature by going to [domain.synthesis.behavior.discreteChoice.carChoiceUtility]
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
     * utility functions. This property is used later in the 'option' call, where the translation is
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
            sigma = asc_1_sig,
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
            sigma = asc_2_sig,
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
            sigma = asc_3_sig,
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
            sigma = 0.0,
        )
    }
}
