package utils

import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

fun interface Metric {
    fun evaluateNumber(expected: Collection<Number>, actual: Collection<Number>) = evaluate(
        expected.map { it.toDouble() },
        actual.map { it.toDouble() }
    )

    fun evaluateNumber(pair: Pair<Collection<Number>, Collection<Number>>) = evaluateNumber(pair.first, pair.second)
    fun evaluate(expected: Collection<Double>, actual: Collection<Double>): Double

    fun evaluate(input: Collection<Pair<Number, Number>>) = evaluateNumber(input.unzip())

    companion object {
        val meanAbsoluteError = Metric { expected, actual ->
            require(expected.size == actual.size) {
                "cannot compare different sizes"
            }

            val sum = expected.zip(actual).sumOf { (exp, act) ->
                abs(exp - act)
            }
            sum / expected.size
        }

        val meanSquaredError = Metric { expected, actual ->
            val sum = expected.zip(actual).sumOf { (exp, act) ->
                val diff = exp - act
                diff * diff
            }
            sum / expected.size
        }

        val rootMeanSquaredError = Metric { expected, actual ->

            sqrt(meanSquaredError.evaluate(expected, actual))
        }

        val meanAbsolutePercentError = Metric { expected, actual ->
            val sum = expected.zip(actual).sumOf { (exp, act) ->
                if (exp == 0.0) {
                    if (act == 0.0) 0.0 else 1.0
                } else {
                    abs(exp - act) / abs(exp)
                }
            }

            sum / expected.size * 100.0
        }
        val standardizedRootMeanSquaredResidual = Metric { expected, actual ->
            val avg = actual.average()
            val variance = actual.sumOf { (it - avg) * (it - avg) / actual.size }
            val deviation = sqrt(variance)
            val rsme = rootMeanSquaredError.evaluate(expected, actual)
            if (deviation == 0.0) Double.NaN else rsme / deviation
        }

        val theilsInequality = Metric { expected, actual ->
            val size = expected.size

            val diffSquaredMean = expected.zip(actual)
                .sumOf { (o, c) -> (o - c) * (o - c) } / size

            val originalSquaredMean = expected.sumOf { it * it } / size
            val comparisonSquaredMean = actual.sumOf { it * it } / size

            sqrt(diffSquaredMean) / (sqrt(originalSquaredMean) + sqrt(comparisonSquaredMean))
        }

        val symmetricAbsoluterPercentError = Metric { expected, actual ->
            val sum = expected.zip(actual).sumOf { (exp, act) ->
                abs(exp - act) / (abs(exp + act) / 2)
            }

            sum / expected.size * 100.0
        }
        // This value is needed for avoiding log(0) calculations. At least for
        private const val LOG_LOWER_BOUND: Double = 0.5
        val meanAbsoluteLogError = Metric { expected, actual ->

            require(expected.all { it >= 0.0 } && actual.all { it >= 0.0 })  {
                "Log error cannot handle negative values, the cheat to avoid log(0) by adding epsilon log(0 + e) could" +
                        "now result in a log(-e + e)"
            }
            val sum = expected.zip(actual).sumOf { (exp, act) ->
                abs(ln(exp.coerceAtLeast(LOG_LOWER_BOUND)) - ln(act.coerceAtLeast(LOG_LOWER_BOUND)))

            }
            sum / expected.size
        }

        val rootMeanSquareLogError = Metric { expected, actual ->

            require(expected.all { it >= 0.0 } && actual.all { it >= 0.0 })  {
                "Log error cannot handle negative values, the cheat to avoid log(0) by adding epsilon log(0 + e) could" +
                        "now result in a log(-e + e)"
            }
            val sum = expected.zip(actual).sumOf { (exp, act) ->
                (ln(exp.coerceAtLeast(LOG_LOWER_BOUND)) - ln(act.coerceAtLeast(LOG_LOWER_BOUND))).pow(2)

            }
            sqrt(sum / expected.size)

        }

        val absolutePercentageError = Metric { expected, actual ->
            val sum = expected.zip(actual).sumOf { (exp, act) ->
                if (exp == 0.0) {
                    if (act == 0.0) 0.0 else 1.0
                } else {
                    abs(exp - act) / abs(exp)
                }
            }

            sum

        }
    }
}
