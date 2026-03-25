package domain.synthesis.behavior.cars.amount

import domain.shared.enums.areatype.SizebasedRegiostarClassification
import domain.synthesis.attributes.household.MaximumHouseholdAttributes
import domain.synthesis.attributes.person.MaximumPersonAttributes
import domain.synthesis.behavior.MinimalistHousehold
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.EnumeratedDiscreteModelBuilder
import kotlin.random.Random

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
 * @param A The type of choice situation that represents a specific car ownership scenario.
 * @param P The type of parameters used for different region classifications.
 * @param model The known discrete choice model that is used to make the car ownership assignment decision.
 * @param converter A function that converts a given index and a `SynthesisHousehold` of survey information into a choice situation (SIT).
 * @param cityParameters Parameters for the city region.
 * @param smallTownParameters Parameters for the small town region.
 * @param urbanAreaParameters Parameters for the urban area region.
 * @param ruralAreaParameters Parameters for the rural area region.
 */
class AssignBySizebasedClassification<A, P>(
    val model: EnumeratedDiscreteModelBuilder<Int, A, P>,
    val converter: (MinimalistHousehold<MaximumHouseholdAttributes, MaximumPersonAttributes>) -> A,
    private val cityParameters: P,
    private val smallTownParameters: P,
    private val urbanAreaParameters: P,
    private val ruralAreaParameters: P,
) : NumberOfCarDeterminer<MaximumHouseholdAttributes, MaximumPersonAttributes>  {

    private val models = SizebasedRegiostarClassification.entries.associateWith {
        model.build(it.toParameters())
    }

    override fun determineNumberOfCars(householdBuilder: MinimalistHousehold<MaximumHouseholdAttributes, MaximumPersonAttributes>): Int {
        val region = householdBuilder.attributes.location.sizebasedRegiostarClassification
        // TODO check where the randomness for this dcm should come from
        return context(converter(householdBuilder), Random(householdBuilder.hashCode())) {
            models[region]!!.select()
        }
    }

    private fun SizebasedRegiostarClassification.toParameters(): P {
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
         * @param A The type of choice situation.
         * @param P The type of parameters used for the region classification. PARAMS needs to be Any, so that it can
         * be lateinit instead of nullable
         */
        class AssignViaRegionTypeBuilder<A, P : Any>(
            val model: EnumeratedDiscreteModelBuilder<Int, A, P>
        ) {

            lateinit var converter: (MinimalistHousehold<MaximumHouseholdAttributes, MaximumPersonAttributes>) -> A
            lateinit var cityParameters: P
            lateinit var smallTownParameters: P
            lateinit var urbanAreaParameters: P
            lateinit var ruralAreaParameters: P

            /**
             * Builds and returns an instance of `AssignBySizebasedClassification` with the provided parameters.
             *
             * @return The fully constructed `AssignBySizebasedClassification` instance.
             */
            fun  build(): AssignBySizebasedClassification<A, P> {
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
        fun <SIT, PARAMS : Any> createUsingModel(
            model: EnumeratedDiscreteModelBuilder<Int, SIT, PARAMS>,
            lambda: AssignViaRegionTypeBuilder<SIT, PARAMS>.() -> Unit
        ): AssignBySizebasedClassification<SIT, PARAMS>  {
            val builder = AssignViaRegionTypeBuilder(model)
            builder.apply(lambda)
            return builder.build()
        }
    }
}