package edu.kit.ifv.domain.synthesis.rules
import edu.kit.ifv.populationsynthesis.rules.measurement.MeasurementDefinition

/**
 * Combines a [MeasurementDefinition] with the target value that the resulting rule should satisfy.
 *
 * A [MeasurementDefinition] describes how a measurement is obtained from an element of type [T].
 * The [target] defines the total value that the generated rule is expected to match. Together,
 * these two values contain all information required to construct a rule via [toRule].
 *
 * This class exists to represent that pair explicitly instead of passing around an unstructured
 * `Pair<MeasurementDefinition<T>, Number>`.
 *
 * @param T the type of element from which the measurement is computed.
 * @property definition the measurement definition used to create the rule.
 * @property target the target value passed to [MeasurementDefinition.makeRule].
 */
data class RuleDefinition<X, T : MeasurementDefinition<X>>(val definition: T, val target: Number) {
    fun toRule() = definition.makeRule(target)
}
