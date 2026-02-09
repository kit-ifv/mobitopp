package domain.synthesis.behavior.householdgeneration

import domain.synthesis.Signature
import domain.synthesis.behavior.ISurveyHousehold

data class NewAlgorithmConfig(
    val ipu: GenericIPU = GenericIPU.legacy,
    val refinement: Refinement = Refinement { },
    val signatureDistributor: InitialSignatureDistributor = GreedyAmountDistro(),
    val ipuCalculationCallback: (List<Pair<Rule<*>, Double>>) -> Unit = {},
)

fun <T> List<Rule<ISurveyHousehold<out T>>>.toSignature(household: ISurveyHousehold<out T>): Signature {
    return withIndex().map { (index, rule) ->
        index to rule.evaluate(household)
    }.filter { it.second != 0 }.toMap()
}

fun <H> List<NamedCountRule<H>>.toSignatureNamed(household: H): Signature {
    return withIndex().map { (index, rule) ->
        index to rule.matches(household)
    }.filter { it.second != 0 }.toMap()
}

data class SignatureAmount(
    val signature: Signature,
    val amount: Int,
) {
    fun toMutable(index: Int): MutableSignatureAmount {
        return MutableSignatureAmount(signature, amount, SignatureIndex(index))
    }
}

data class MutableSignatureAmount(
    val signature: Signature,
    var amount: Int,
    val index: SignatureIndex,
)

fun interface InitialSignatureDistributor {
    fun distribute(partitions: List<Partition>, signatureAmounts: Collection<SignatureAmount>)
}

fun interface PartitionMetric {
    fun calculate(expected: IntArray, actual: IntArray, signature: Signature): Double
}

@Suppress("MagicNumber")
val SquaredDiff = PartitionMetric { expected, actual, signature ->
    var origDiff = .0
    var newDiff = .0
    signature.entries.forEach { (k, v) ->
        val exp = expected[k]
        val act = actual[k]
        val denom = if (exp == 0) 1e-9 else exp.toDouble() // avoid div by 0
        val delta = v
        val relErrorOrig = (exp - act) / denom
        val relErrorNew = (exp - (act + delta)) / denom

        origDiff += relErrorOrig * relErrorOrig
        newDiff += relErrorNew * relErrorNew
    }

    origDiff - newDiff
}
