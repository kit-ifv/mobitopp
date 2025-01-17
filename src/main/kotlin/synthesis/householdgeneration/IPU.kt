package synthesis.householdgeneration

import domain.data.Zone
import synthesis.SurveyHousehold
import synthesis.domain.SynthesisHousehold
import synthesis.pickWithReplacement
import synthesis.selectExact
import utils.collections.invertMap
import kotlin.random.Random

/**
 * The default implementation of the [HouseholdSynthesis] interface, which generates a synthetic population
 * for each zone based on survey household data represented as [ScalableVector]s. The goal of the synthesis is
 * to match the conditions defined by a set of rules using the [Observer] for each zone.
 *
 * Each household in the survey is encoded as a [ScalableVector], a vector where each element represents an
 * attribute of the household. For example, a household might be encoded as `[0, 1, 2, 0]`. The synthesis process
 * involves applying these vectorized households to meet the rules for each zone. The algorithm is zone-specific,
 * meaning that each zone is processed independently of others.
 *
 *
 * **Important:** The [algorithm] mutates the [ScalableVector] instances during the calculation process. And the
 * values for the synthesis are extracted from this algorithm. If you write your own algorithm remember to mutate
 * the fields to pass the proper output. This is a design decision made for performance purposes.
 *
 *
 * To improve computational efficiency, households with identical vector representations are grouped together.
 * If two households have the same vector (e.g., `hh1 = [0, 1, 2]`, `hh2 = [0, 1, 2]`), the vector `[0, 1, 2]`
 * will only be processed once in the calculation. A strategy for reversing this grouping (i.e., mapping a unique vector
 * back to the corresponding households) is provided through the [converter] property.
 *
 * @property converter Defines how to extract the full list of survey households from the vector groupings.
 * @property algorithm A function that defines the strategy for calculating a solution for the unique vectors and observers.
 *                     This function **mutates** the [ScalableVector] instances to adjust them to better fit the rules.
 */
class IPU<T>(
    val converter: GenerateHouseholdsFromVector<T> = SampleAndCollect(),
    val algorithm: (vectors: Collection<ScalableVector>, Collection<Observer>) -> Unit
) :
    HouseholdSynthesis<T> {

    /**
     * Synthesizes households for each zone based on the provided survey data and the conditions (rules) defined
     * for each zone. For each zone, the corresponding rules are applied to the survey households to generate
     * a synthesized population of households in vectorized form.
     *
     * The survey households are converted to their vector representations based on the ruleset for each zone,
     * and the resulting vectors are processed using the defined [algorithm] to match the target conditions.
     *
     * @param surveyHouseholds A collection of survey households to be synthesized.
     * @param conditions A map of zones to their associated rules, which define the conditions the synthesized households
     *        must meet.
     * @return A map of zones to lists of synthesized households, where each household is represented by a
     *         [SynthesisHousehold] object.
     */
    override fun synthesize(
        surveyHouseholds: Collection<SurveyHousehold<out T>>,
        conditions: Map<Zone, List<Rule<in T>>>
    ): Map<Zone, List<SynthesisHousehold<out T>>> {
        return conditions.entries.associate { (zone, rules) ->
            zone to calculate(surveyHouseholds, rules, converter).map { it.toSynthesisHousehold() }

        }
    }

    /**
     * Calculates the synthetic household population for a set of survey households, based on the provided rules
     * and conversion strategy. This method first converts each survey household into its vector representation,
     * then applies the defined [algorithm] to match the rules for each zone.
     *
     * It groups the households with identical vector representations together for efficient processing and then
     * uses the [converter] to map the resulting unique vectors back to the corresponding households.
     *
     * @param surveyHouseholds A collection of survey households to be vectorized and processed.
     * @param rules The list of rules that define the conditions for the synthesized population.
     * @param conversion The strategy used to convert the grouped vectors back into households.
     * @return A list of survey households that meet the conditions specified by the rules.
     */
    private fun calculate(
        surveyHouseholds: Collection<SurveyHousehold<out T>>,
        rules: List<Rule<in T>>,
        conversion: GenerateHouseholdsFromVector<T>
    ): List<SurveyHousehold<out T>> {
        val vectorMapping = surveyHouseholds.associateWith { it.toScalableVector(rules) }
        val inverseMap = vectorMapping.invertMap()
        val uniqueVectors = inverseMap.keys
        val observers = rules.withIndex().map {
            Observer.fromRule(it.value, it.index, uniqueVectors)
        }
        algorithm(uniqueVectors, observers)
        return conversion.run {
            inverseMap.extract()
        }

    }

}

/**
 * There may be different strategies to pick a certain amount of survey households from a scalable vector. This
 * interface encapsulates different methods to convert a vector and an associated list of households.
 */
fun interface GenerateHouseholdsFromVector<T> {
    fun Map<ScalableVector, List<SurveyHousehold<out T>>>.extract(): List<SurveyHousehold<out T>>

    companion object {
        /**
         * Coercion strategy cuts off the number of requested households. if the required number is 12.8, the number
         * of households picked is 12. Does not shuffle and maintains the order of the entries
         */
        fun <T> coerceMaintainingOrder(): GenerateHouseholdsFromVector<T> {
            return GenerateHouseholdsFromVector {
                entries.flatMap {
                    it.value.selectExact(it.key.scalar.toInt())
                }
            }
        }
    }
}

/**
 * This generation method tracks the amount of leftover decimals and adds one additional synthesis household once
 * a spillover occurs.
 */
class SampleAndCollect<T>(val random: Random = Random(1)) : GenerateHouseholdsFromVector<T> {
    private var overflowCounter: Double = 0.0
    override fun Map<ScalableVector, List<SurveyHousehold<out T>>>.extract(): List<SurveyHousehold<out T>> {
        return entries.flatMap {
            // Update the offset decimal to be added to the overflow counter
            val offset = it.key.scalar - it.key.scalar.toInt()
            overflowCounter += offset
            val amount = if (overflowCounter >= 1.0) {
                // If the overflow counter spills, add one extra household
                overflowCounter--
                it.key.scalar.toInt() + 1

            } else {
                it.key.scalar.toInt()
            }
            it.value.pickWithReplacement(amount, random)
        }
    }
}



