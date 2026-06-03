package domain.synthesis.behavior.cars.choicemodels

import domain.shared.enums.household.EconomicStatus
import domain.synthesis.behavior.cars.choicemodels.parameters.CarOwnershipParameters
import domain.synthesis.behavior.cars.choicemodels.parameters.CarParameters
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.NestedStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.nestedLogit

/**
 * Here we design the discrete choice model we want to be using for car ownership. We are interested in using our
 * attributes defined in [CarOwnershipAttributes] and the parameters defined in [domain.synthesis.behavior.discreteChoice.CarOwnershipParameters]. We create
 * a discrete choice model and pass a nested logit as distribution function to determine the probabilities.
 */

/*This Annotation tells the code analysis tool to ignore numbers that occur
inexplicably in the code. The tool cannot differentiate between application code and choice model configuration and
starts complaining, if you never write application code or run the tool for code quality you can simply accept the
existence of this annotation + comment as something you don't care about*/
@Suppress("MagicNumber")
val carAmountChoiceModel = NestedStructure<Int, CarOwnershipAttributes, CarOwnershipParameters> {
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
        option(2, { twoCar }) { _, characteristics ->
            mu +
                sigma * characteristics.randomNumber +
                when (characteristics.size) {
                    1 -> oneMember
                    2 -> twoMembers
                    in 4..Int.MAX_VALUE -> fourOrMoreMembers
                    else -> 0.0
                } +
                when (characteristics.economicStatus) {
                    EconomicStatus.LOW, EconomicStatus.VERY_LOW -> lowIncome
                    EconomicStatus.HIGH, EconomicStatus.VERY_HIGH -> highIncome
                    EconomicStatus.MIDDLE -> 0.0
                } +
                (characteristics.amountOfChildren >= 1) * childrenFactor +

                (characteristics.amountOfYouth >= 1) * youthFactor +
                when (characteristics.amountOfWorkers) {
                    1 -> oneWorker
                    2 -> twoWorkers
                    else -> 0.0
                } +
                when (characteristics.amountOfLicences) {
                    1 -> oneLicence
                    2 -> twoLicences
                    3 -> threeLicences
                    in 4..Int.MAX_VALUE -> fourOrMoreLicences
                    else -> 0.0
                } +
                (characteristics.isWg) * isFlat +
                (characteristics.isOnlyRetired) * isRetired +
                (characteristics.isOnlyUnemployed) * isUnemployed
        }

        /*
        If you do not specify a parameters = {...} translation you get the entire parameter object, which you can then
        use in your utility function. Here you can theoretically use b_hh_size_1_on_1, even though that parameter
        is not intended to be used in the utility function for 3 cars. (You can also very easily mistype and mess up
        the utility function, so be moderately careful)
         */
        option(3) { _, characteristics ->
            asc_3_mu +
                asc_3_sig * characteristics.randomNumber +
                (characteristics.size == 1) * b_hh_size_1_on_3 +
                (characteristics.size == 2) * b_hh_size_2_on_3 +
                (characteristics.size >= 4) * b_hh_size_4_on_3 +

                (characteristics.economicStatus.poor) * b_income_low_on_3 +
                (characteristics.economicStatus.rich) * b_income_high_on_3 +
                (characteristics.amountOfChildren >= 1) * b_person_age_0_9_on_3 +

                (characteristics.amountOfYouth >= 1) * b_person_age_10_17_on_3 +
                (characteristics.amountOfWorkers == 1) * b_person_working_1_on_3 +
                (characteristics.amountOfWorkers == 2) * b_person_working_2_on_3 +

                (characteristics.amountOfLicences == 1) * b_license_1_on_3 +
                (characteristics.amountOfLicences == 2) * b_license_2_on_3 +
                (characteristics.amountOfLicences == 3) * b_license_3_on_3 +
                (characteristics.amountOfLicences >= 4) * b_license_4_on_3 +

                (characteristics.isWg) * b_shared_flat_on_3 +
                (characteristics.isOnlyRetired) * b_hh_retired_on_3 +
                (characteristics.isOnlyUnemployed) * b_hh_unemployed_on_3
        }

        /*
        the standardFunction is not written in stone. You can modify it and perform amendments,
        so if for example the utility function needs to do some calculation specifically only for 4 cars, you could add
        it here and reuse the code for the utility function that you had already written. (For this example we added a
         + it.isWg * 0.0 line.)

         Sadly the invocation of standardFunction(this, it) is a bit cryptic, but necessary if you intend to use the
         amendment approach.
         */
        option(4, parameters = { fourCar }) { option, characteristics ->
            standardFunction(this, option, characteristics) +
                characteristics.isWg * 0.0
        }
    }
}.nestedLogit("ExampleNestedNumberOfCarsModel")

/*
 * You can also define other helpers that you would like to utilize in your utility functions. Here we give two examples
 * for writing a helper attribute for economic status. Do not be afraid of the keywords:
 * [private] means: "visible in this File only" (Because we do not want to spam the codebase with our additions)
 * [inline] means: "Be a bit quicker in the calculation" (The technical details of inline are really cool and worth a
 *  read)
 *
 * [val] means: "we want a property instead of a method because writing it.poor reads better than it.poor()."
 * EconomicStatus.xxx is an extension property. if you get an economic status somewhere in the code (For example in the
 * utility functions below) you can simply write economicStatus.poor and this helper is run.
 *
 * [get() =] this snippet is necessary because we do not have a backing field. Also an interesting read, but blindly
 * accepting the syntax as magic is completely fine too.
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
private fun EconomicStatus.poor(): Boolean = this == EconomicStatus.LOW || this == EconomicStatus.VERY_LOW

/**
 * @property rich Determines whether an economic status should be considered poor in the utility function. This is the
 * case if the [EconomicStatus] is either [EconomicStatus.VERY_HIGH] or [EconomicStatus.HIGH]
 */
private inline val EconomicStatus.rich get(): Boolean = this == EconomicStatus.HIGH || this == EconomicStatus.VERY_HIGH

/**
 * Here we can see one benefit of being able to translate the large [CarOwnershipParameters] to something different,
 * like a [CarParameters] object.
 * We can define a standard utility function which takes in a [CarParameters] object and
 * the translation process automatically fills the utility function with the corresponding parameters. In this example
 * we will receive [CarOwnershipParameters.oneCar] for the utility function of one car [CarOwnershipParameters.twoCar]
 * for two cars and so on. Since the utility function is exactly identical for all calculations we can save repeatedly
 * needing to write the same utility function.
 * @property standardFunction This is the standard utility function for car ownership as found in the Rastatt model of
 * mobitopp. Used later in the discrete choice model definition.
 */
@Suppress("MagicNumber") // These magic numbers are ok, the content of the function should still be understandable
private val standardFunction: CarParameters.(Int, CarOwnershipAttributes) -> Double = { _, characteristics ->
    mu +
        sigma * characteristics.randomNumber +
        (characteristics.size == 1) * oneMember +
        (characteristics.size == 2) * twoMembers +
        (characteristics.size >= 4) * fourOrMoreMembers +

        (characteristics.economicStatus.poor) * lowIncome +
        (characteristics.economicStatus.rich) * highIncome +
        (characteristics.amountOfChildren >= 1) * childrenFactor +
        (characteristics.amountOfYouth >= 1) * youthFactor +
        (characteristics.amountOfWorkers == 1) * oneWorker +
        (characteristics.amountOfWorkers == 2) * twoWorkers +

        (characteristics.amountOfLicences == 1) * oneLicence +
        (characteristics.amountOfLicences == 2) * twoLicences +
        (characteristics.amountOfLicences == 3) * threeLicences +
        (characteristics.amountOfLicences >= 4) * fourOrMoreLicences +

        (characteristics.isWg) * isFlat +
        (characteristics.isOnlyRetired) * isRetired +
        (characteristics.isOnlyUnemployed) * isUnemployed
}
