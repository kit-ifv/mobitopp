package domain.synthesis.rules.measurements

import domain.synthesis.behavior.MinimalistHousehold
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition

/**
 * A definition of how to measure the household size. In the most instances you should use preexisting [edu.kit.ifv.populationsynthesis.rules.measurement.MeasurementDefinition]
 * implementations to avoid unexpected bug hunts. This implementation for example takes a target size and an operation
 * to check whether the size fulfills the predicate given in the constructor.
 *
 * This is deliberately written so that you cannot plug in your own function but use one of the prefabricated Equality
 * operations.
 *
 * The benefit of these prefabs is that they can be tested (and should only be shipped if tested). If you must write
 * your own definition, please for the love of all that is good and holy build a test instance, because painfully
 * debugging the output only to realize that the input definition is wrong can take hours (as proven by precedence)
 */
class HouseholdSizeDefinition(val targetSize: Int, val equalityOp: EqualityOp) :
    BooleanMeasurementDefinition<MinimalistHousehold<*, *>>() {
    override fun generateDescription(): String = "Household $targetSize $equalityOp"

    override fun evaluation(element: MinimalistHousehold<*, *>): Boolean =
        equalityOp.test(element.members.size, targetSize)

    enum class EqualityOp(val symbol: String) {
        EQUALS("==") {
            override fun <T : Comparable<T>> test(a: T, b: T): Boolean = a == b
        },
        NOT_EQUALS("!=") {
            override fun <T : Comparable<T>> test(a: T, b: T): Boolean = a != b
        },
        LESS_THAN("<") {
            override fun <T : Comparable<T>> test(a: T, b: T): Boolean = a < b
        },
        LESS_OR_EQUAL("<=") {
            override fun <T : Comparable<T>> test(a: T, b: T): Boolean = a <= b
        },
        GREATER_THAN(">") {
            override fun <T : Comparable<T>> test(a: T, b: T): Boolean = a > b
        },
        GREATER_OR_EQUAL(">=") {
            override fun <T : Comparable<T>> test(a: T, b: T): Boolean = a >= b
        }, ;

        abstract fun <T : Comparable<T>> test(a: T, b: T): Boolean
    }
}
