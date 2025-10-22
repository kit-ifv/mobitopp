package utils

import kotlin.math.abs
import kotlin.math.sqrt

fun interface Metric {
    fun evaluateNumber(expected: Collection<Number>, actual: Collection<Number>) = evaluate(expected.map { it.toDouble() }, actual.map { it.toDouble() })
    fun evaluateNumber(pair: Pair<Collection<Number>, Collection<Number>>) = evaluateNumber(pair.first, pair.second)
    fun evaluate(expected: Collection<Double>, actual: Collection<Double>): Double


    fun evaluate(input: Collection<Pair<Number, Number>>) = evaluateNumber(input.unzip())
    companion object {
        val meanAbsoluteError = Metric {expected, actual ->
            require(expected.size == actual.size) {
                "cannot compare different sizes"
            }

            val sum = expected.zip(actual).sumOf {(exp, act) ->
                abs(exp - act)
            }
            sum / expected.size
        }

        val meanSquaredError = Metric {expected, actual ->
            val sum = expected.zip(actual).sumOf {(exp, act) ->
                val diff = exp - act
                diff * diff
            }
            sum / expected.size
        }

        val rootMeanSquaredError = Metric {expected, actual ->

            sqrt(meanSquaredError.evaluate(expected, actual))
        }

        val meanAbsolutePercentError = Metric {expected, actual ->
            val sum = expected.zip(actual).sumOf {(exp, act) ->
                if(exp == 0.0) {
                    if(act == 0.0) 0.0 else 1.0
                } else abs(exp - act) / abs(exp)


            }

            sum / expected.size * 100.0
        }
        val standardizedRootMeanSquaredResidual = Metric {expected, actual ->
            val avg = actual.average()
            val variance = actual.sumOf { (it - avg) * (it - avg) / actual.size}
            val deviation = sqrt(variance)
            val rsme = rootMeanSquaredError.evaluate(expected, actual)
            if(deviation == 0.0) Double.NaN else rsme / deviation

        }
    }
}



fun standardizedRootMeanSquaredResidual(expected: Collection<Number>, actual: Collection<Number>): Double {
    return sqrt(-1.0)
}

fun theilsIneq(original: List<Number>, comparisons: List<Number>) = theilsInequality(original.map { it.toDouble() }, comparisons.map{it.toDouble()})
fun theilsInequality(original: List<Double>, comparison: List<Double>): Double {
    require(original.size == comparison.size) {
        "Original and comparison must be of the same length"
    }

    val size = original.size

    val diffSquaredMean = original.zip(comparison)
        .sumOf { (o, c) -> (o - c) * (o - c) } / size

    val originalSquaredMean = original.sumOf { it * it } / size
    val comparisonSquaredMean = comparison.sumOf { it * it } / size

    return sqrt(diffSquaredMean) / (sqrt(originalSquaredMean) + sqrt(comparisonSquaredMean))
}







fun symmetricAbsolutePercentError(expected: Collection<Double>, actual: Collection<Double>): Double {
    require(expected.size == actual.size) {
        "Cannot compare different sizes"
    }
    val sum = expected.zip(actual).sumOf {(exp, act) ->
        abs(exp - act) / (abs(exp + act) / 2)
    }

    return sum / expected.size * 100.0
}

