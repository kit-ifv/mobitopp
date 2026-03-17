package domain.synthesis.behavior.householdgeneration

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.behavior.pickWithReplacement
import domain.synthesis.behavior.selectExact
import kotlin.random.Random

private const val IPU_GENERATION_LABEL = "IPU generation"

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
        fun <T, H : MinimalistHousehold<MinimumHouseholdAttributes, T>> coerceMaintainingOrder(): GenerateHouseholdsFromVector<H> {
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
