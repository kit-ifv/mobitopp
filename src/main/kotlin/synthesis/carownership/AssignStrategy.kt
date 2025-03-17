package synthesis.carownership

import domain.enums.SizebasedRegiostarClassification
import modeling.discreteChoice.ChoiceSituation
import modeling.discreteChoice.KnownDiscreteChoiceModel
import synthesis.SurveyInfo
import synthesis.discreteChoice.CarOwnershipAttributes
import synthesis.discreteChoice.carChoiceModel
import synthesis.discreteChoice.carOwnershipCityParameters
import synthesis.discreteChoice.carOwnershipRuralArea
import synthesis.discreteChoice.carOwnershipSmallCity
import synthesis.discreteChoice.carOwnershipUrbanAreaParameters
import synthesis.domain.SynthesisHousehold
import synthesis.toCarOwnershipAttributes

fun interface CarOwnershipAssignStrategy<T> {

    fun determineNumberOfCars(householdBuilder: SynthesisHousehold<out T>): Int
}

class AlwaysAssignFixedNumber(val amount: Int) : CarOwnershipAssignStrategy<Any> {
    override fun determineNumberOfCars(householdBuilder: SynthesisHousehold<out Any>): Int {
        return amount
    }
}

/**
 * This class implements a car ownership assignment strategy based on a size-based classification of regions.
 * It assumes four distinct sets of parameters based on the location of the survey household,
 * which are derived from the `Regiostar 17` mapping. These regions are classified into:
 * - City
 * - Small Town
 * - Urban Area
 * - Rural Area
 *
 * The parameters for each region are passed as concrete instances and are used during the
 * decision-making process for car ownership assignments.
 *
 * This strategy uses a discrete choice model to determine the number of cars for a given household.
 * The choice model is applied to the household data, which is converted into a situation (via a converter function),
 * and then the appropriate set of parameters for the household’s location is selected.
 *
 * The `converter` function transforms household data into a specific choice situation, using an integer index
 * and the household data, which is necessary to make the decision.
 *
 * @param SIT The type of choice situation that represents a specific car ownership scenario.
 * @param PARAMS The type of parameters used for different region classifications.
 * @param model The known discrete choice model that is used to make the car ownership assignment decision.
 * @param converter A function that converts a given index and a `SynthesisHousehold` of survey information into a choice situation (SIT).
 * @param cityParameters Parameters for the city region.
 * @param smallTownParameters Parameters for the small town region.
 * @param urbanAreaParameters Parameters for the urban area region.
 * @param ruralAreaParameters Parameters for the rural area region.
 */
class AssignBySizebasedClassification<SIT : ChoiceSituation<Int>, PARAMS>(
    val model: KnownDiscreteChoiceModel<Int, SIT, PARAMS>,
    val converter: (Int, SynthesisHousehold<out SurveyInfo>) -> SIT,
    private val cityParameters: PARAMS,
    private val smallTownParameters: PARAMS,
    private val urbanAreaParameters: PARAMS,
    private val ruralAreaParameters: PARAMS,
) : CarOwnershipAssignStrategy<SurveyInfo> {

    override fun determineNumberOfCars(householdBuilder: SynthesisHousehold<out SurveyInfo>): Int {
        val parameterSet =
            householdBuilder.location.zone?.regionType?.toRegiostar17()?.toSizebasedClassification()?.toParameters()
                ?: cityParameters

        return model.select({converter(it, householdBuilder) }, parameterSet)
    }

    private fun SizebasedRegiostarClassification.toParameters(): PARAMS {
        return when (this) {
            SizebasedRegiostarClassification.CITY -> cityParameters
            SizebasedRegiostarClassification.SMALL_TOWN -> smallTownParameters
            SizebasedRegiostarClassification.URBAN_AREA -> urbanAreaParameters
            SizebasedRegiostarClassification.RURAL_AREA -> ruralAreaParameters
        }
    }

    companion object {
        /**
         * A builder class used to construct an instance of `AssignBySizebasedClassification` with the necessary parameters.
         * This builder helps with the setup of the discrete choice model and the regional parameters.
         *
         * @param SIT The type of choice situation.
         * @param PARAMS The type of parameters used for the region classification. PARAMS needs to be Any, so that it can
         * be lateinit instead of nullable
         */
        class AssignViaRegionTypeBuilder<SIT : ChoiceSituation<Int>, PARAMS : Any>(
            var model: KnownDiscreteChoiceModel<Int, SIT, PARAMS>
        ) {

            lateinit var converter: (Int, SynthesisHousehold<out SurveyInfo>) -> SIT
            lateinit var cityParameters: PARAMS
            lateinit var smallTownParameters: PARAMS
            lateinit var urbanAreaParameters: PARAMS
            lateinit var ruralAreaParameters: PARAMS

            /**
             * Builds and returns an instance of `AssignBySizebasedClassification` with the provided parameters.
             *
             * @return The fully constructed `AssignBySizebasedClassification` instance.
             */
            fun build(): AssignBySizebasedClassification<SIT, PARAMS> {
                return AssignBySizebasedClassification(
                    model,
                    converter,
                    cityParameters,
                    smallTownParameters,
                    urbanAreaParameters,
                    ruralAreaParameters
                )
            }
        }

        /**
         * Creates and returns an instance of `AssignBySizebasedClassification` using a builder.
         * The builder accepts a lambda function to configure the required parameters.
         *
         * @param model The discrete choice model to be used.
         * @param lambda A lambda function to configure the builder.
         * @return The constructed `AssignBySizebasedClassification` instance.
         */
        fun <SIT : ChoiceSituation<Int>, PARAMS : Any> createUsingModel(
            model: KnownDiscreteChoiceModel<Int, SIT, PARAMS>,
            lambda: AssignViaRegionTypeBuilder<SIT, PARAMS>.() -> Unit
        ): AssignBySizebasedClassification<SIT, PARAMS> {
            val builder = AssignViaRegionTypeBuilder(model)
            builder.apply(lambda)
            return builder.build()
        }
    }
}

/**
 * A standard assignment strategy that uses predefined choice models and parameter sets for different region types.
 * This strategy leverages the `AssignBySizebasedClassification` with default parameters for various region types.
 */
val standardAssignmentByRegionSize = AssignBySizebasedClassification.createUsingModel(carChoiceModel) {
    converter = { i, household -> CarOwnershipAttributes(i, household.toCarOwnershipAttributes()) }
    cityParameters = carOwnershipCityParameters
    smallTownParameters = carOwnershipSmallCity
    urbanAreaParameters = carOwnershipUrbanAreaParameters
    ruralAreaParameters = carOwnershipRuralArea
}
