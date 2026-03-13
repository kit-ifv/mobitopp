package domain.synthesis.behavior.householdgeneration

import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.domain.SynthesisHousehold
import domain.synthesis.behavior.pickWithReplacement
import domain.synthesis.behavior.selectExact
import utils.collections.addProgressBar
import utils.collections.invertMap
import kotlin.random.Random

private const val IPU_GENERATION_LABEL = "IPU generation"

/**
 * The default implementation of the [HouseholdSynthesisDeprecated] interface, which generates a synthetic population
 * for each zone based on survey household data represented as [ScalableVector]s. The goal of the synthesis is
 * to match the conditions defined by a set of rules using the [RuleObserver] for each zone.
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
class IPU<AREA, T>(
    val converter: GenerateHouseholdsFromVector<ISurveyHousehold<out T>> = SampleAndCollect(),
    val algorithm: GenericIPU,
) :
    HouseholdSynthesisDeprecated<AREA, ISurveyHousehold<out T>, SynthesisHousehold<out T>> {

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
        surveyHouseholds: Collection<ISurveyHousehold<out T>>,
        conditions: Map<AREA, List<Rule<ISurveyHousehold<out T>>>>,
    ): Map<AREA, List<SynthesisHousehold<out T>>> {
        return generate(surveyHouseholds, conditions).mapValues { it.value.map { it.toSynthesisHousehold() } }
    }

    override fun synthesize(targetAreas: List<AREA>): Map<AREA, List<SynthesisHousehold<T>>> {
        throw UnsupportedOperationException("The original implementation of IPU cannot handle this signature")
    }

    fun generate(
        surveyHouseholds: Collection<ISurveyHousehold<out T>>,
        conditions: Map<AREA, List<Rule<ISurveyHousehold<out T>>>>,
    ): Map<AREA, List<ISurveyHousehold<out T>>> {
        return conditions.entries
            .addProgressBar(
                label = IPU_GENERATION_LABEL,
                expectedCount = conditions.size.toLong()
            ).associate { (zone, rules) ->
                zone to converter.run {
                    val ipu = calculate(surveyHouseholds, rules)
                    ipu.extractFrom()
                }
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
    fun calculate(
        surveyHouseholds: Collection<ISurveyHousehold<out T>>,
        rules: List<Rule<ISurveyHousehold<out T>>>,

    ): Map<ScalableVector, List<ISurveyHousehold<out T>>> {
        val vectorMapping = surveyHouseholds.associateWith { it.toScalableVector(rules) }
        val inverseMap = vectorMapping.invertMap()
        val uniqueVectors = inverseMap.keys
        val ruleObservers = rules.withIndex().map {
            RuleObserver.fromRule(it.value, it.index, uniqueVectors)
        }
        algorithm.run(uniqueVectors, ruleObservers)
        return inverseMap
    }

    companion object {
        fun <AREA, T> standard() = IPU<AREA, T>(algorithm = GenericIPU.newAlgorithm)

        fun <AREA, T> legacy() = IPU<AREA, T>(algorithm = GenericIPU.legacy)
    }
}

fun interface GenerateHouseholds<X, H> {
    fun Map<X, List<H>>.extractFrom(): List<H>

    fun extract(map: Map<X, List<H>>) = map.extractFrom()
}

open class GenericCollector<X, H>(
    val random: Random = Random(1),
    val amountDeterminer: (Collection<X>) -> Collection<Int>,
) : GenerateHouseholds<X, H> {
    override fun Map<X, List<H>>.extractFrom(): List<H> {
        val amounts = amountDeterminer(keys)
        return amounts.zip(values).flatMap { (amount, households) ->
            households.pickWithReplacement(amount)
        }
    }
}

/**
 * There may be different strategies to pick a certain amount of survey households from a scalable vector. This
 * interface encapsulates different methods to convert a vector and an associated list of households.
 */
fun interface GenerateHouseholdsFromVector<H> : GenerateHouseholds<ScalableVector, H> {
    override fun Map<ScalableVector, List<H>>.extractFrom(): List<H>

    companion object {
        /**
         * Coercion strategy cuts off the number of requested households. if the required number is 12.8, the number
         * of households picked is 12. Does not shuffle and maintains the order of the entries
         */
        fun <T, H : MinimalistHousehold<out T>> coerceMaintainingOrder(): GenerateHouseholdsFromVector<H> {
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
class SampleAndCollect<H>(
    random: Random = Random(1),
    roundingStrategy: RoundingStrategy = standardRoundingStrategy,
) : GenericCollector<ScalableVector, H>(random, { roundingStrategy.convertToInts(it.map { it.scalar }) }),
    GenerateHouseholdsFromVector<H>

fun interface RoundingStrategy {
    fun convertToInts(values: Collection<Double>): List<Int>

    fun <I> integerizeIPUOutput(elements: Collection<IPUOutput<I>>): List<IntegerIPUOutput<I>> {
        val newValues = convertToInts(elements.map { it.amount })
        return elements.zip(newValues).map { (e, num) -> e.discretize(num) }
    }
}

fun Collection<Double>.roundVia(strategy: RoundingStrategy): List<Int> = strategy.convertToInts(this)

val standardRoundingStrategy = RoundingStrategy { values ->
    var overflowCounter = 0.0
    values.map {
        val intVal = it.toInt()
        val offset = it - intVal
        overflowCounter += offset
        val amount = if (overflowCounter >= 1.0) {
            overflowCounter--
            intVal + 1
        } else {
            intVal
        }
        amount
    }
}
