package usecases.choicemodels.modechoice

import domain.data.EconomicStatus
import units.Currency
import units.CurrencyUnit
import units.euros
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

/**
 * Evaluates a [CombinedScope] for mode utility calculation. A mode choice decision will always require a travel time
 * and optional travel cost.
 *
 */
interface Evaluable {
    fun evaluate(utilityScope: CombinedScope, travelTime: Duration, travelCost: Currency? = null): Double

    fun convertTravelTime(travelTime: Duration): Double =
        travelTime.coerceAtMost(1000.minutes).toDouble(DurationUnit.MINUTES)

    fun convertTravelCost(travelCost: Currency): Double =
        travelCost.coerceAtMost(1000.euros).toDouble(CurrencyUnit.EUROS)
}

/**
 * When the associated mode has no cost this interface provides an evaluation function that drops the cost parameter.
 *
 */
interface NoCost : Evaluable {
    val alpha: Alpha
    val travelTimeBeta: TravelTimeBeta

    override fun evaluate(utilityScope: CombinedScope, travelTime: Duration, travelCost: Currency?): Double {
        return alpha.evaluate(utilityScope) +
            travelTimeBeta.evaluate(travelTime, utilityScope, ::convertTravelTime)
    }
}

/**
 * If the associated mode has an assigned cost this interface provides the necessary blocks to evaluate the utility function
 *
 * @constructor Create empty With cost
 */
interface WithCost : Evaluable {
    val alpha: Alpha
    val travelTimeBeta: TravelTimeBeta
    val travelCostBeta: TravelCostBeta

    override fun evaluate(utilityScope: CombinedScope, travelTime: Duration, travelCost: Currency?): Double {
        requireNotNull(travelCost) {
            "travel cost is required when calling evaluate on WithCost interface"
        }
        return alpha.evaluate(utilityScope) +
            travelTimeBeta.evaluate(travelTime, utilityScope, ::convertTravelTime) +
            travelCostBeta.evaluate(travelCost, utilityScope, ::convertTravelCost)
    }
}

/**
 * Historically utility functions are separated in a constant factor block alpha and several variable beta blocks.
 * This type alias helps visualize this arrangement
 */
typealias Alpha = CombinedModeParameters

/**
 * The Travel Time Beta block takes a normal set of parameters, but multiplies these parameters with a variable input,
 * namely travel time. The class provides an implementation for evaluation that takes a duration as additional external
 * parameter.
 *
 */
abstract class TravelTimeBeta : CombinedModeParameters {
    inline fun evaluate(duration: Duration, utilityScope: CombinedScope, lambda: (Duration) -> Double): Double {
        return lambda(duration) * evaluate(utilityScope)
    }
}

/**
 * The Travel Cost Beta block takes a normal set of parameters and multiplies the sum with a [Currency] cost.
 */
abstract class TravelCostBeta : CombinedModeParameters {
    inline fun evaluate(cost: Currency, utilityScope: CombinedScope, lambda: (Currency) -> Double): Double {
        return lambda(cost) * evaluate(utilityScope)
    }

    inline fun evaluatePlus(
        cost: Currency,
        utilityScope: CombinedScope,
        additions: Double,
        lambda: (Currency) -> Double
    ): Double {
        return lambda(cost) * (evaluate(utilityScope) + additions)
    }
}

/**
 * This class is criminal. Someone made the utility functions use the parameter for very rich households and multiplied
 * it with the number of cars. I heavily assume that this decision was malformed. This class only exists to mock the
 * behaviour originally observed in transmove, but I cannot support such misrepresentation and will be very happy once
 * it is deleted.
 *
 */
// TODO FOR SOME REASON SOMEONE MULTIPLIED THE NUMBER OF CARS WITH THE RICHNESS PARAMETER FOR CS; ESCOOTER... WHY?
@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
@Deprecated(
    "Are you absolutely sure you want to multiply the parameter for" +
        " economic status with the number of cars rather than the economic status?"
)
open class NumCarCostBeta(
    override val purposes: ChoiceModelPurposes
) : TravelCostBeta(), CustomNextActivity, StandardCars {
    /* This is originally "b_oekstat5_on_cost" though in order to multiply it with the number of cars we need to
    implement the StandardCars interface, which requires a "numberOfCars" parameter, which is reasonable as the interface
    expects you to multiply a parameter with the number of cars. In order to cheat the behavior from the original
    mode choie utility function we implement StandardCars but set the numberOfCars parameter to the value of b_oekstat5
     */
    override val numberOfCars: Double = 0.029633074216897
    override val constant: Double = -0.173579310377872
    private val businessCost = 0.0840900269251931 - 0.02
    override fun evaluateNextActivity(person: ModePersonScope): Double {
        return if (person.nextActivity.type == purposes.business) businessCost else 0.0
    }
}

/**
 * Economic status cost beta
 *
 * @constructor Create empty Economic status cost beta
 */
// Takes care of the most cost implementations based on b_cost b_cost_oekv5 and b_dienst_on_cost
@Suppress("MagicNumber") // It's ok detekt, parameters may be magic numbers
open class EconomicStatusCostBeta(
    override val purposes: ChoiceModelPurposes
) : TravelCostBeta(), CustomNextActivity, CustomEconomicStatus {
    override val constant: Double = -0.173579310377872
    val veryRich = 0.029633074216897
    val businessCost = 0.0840900269251931 - 0.02
    override fun evaluateNextActivity(person: ModePersonScope): Double {
        return if (person.nextActivity.type == purposes.business) businessCost else 0.0
    }

    override fun evaluateEconomicStatus(person: ModePersonScope): Double {
        return if (person.person.household.economicStatus == EconomicStatus.VERY_HIGH) veryRich else 0.0
    }
}
