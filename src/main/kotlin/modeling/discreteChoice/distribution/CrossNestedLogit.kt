package modeling.discreteChoice.distribution

import modeling.discreteChoice.DistributionFunction
import modeling.models.ChoiceAlternative

class CrossNestedLogit<R : Any, A, P>(
    private val structure: CrossNestStructureData<R, A, P>
) : DistributionFunction<A, P> where A : ChoiceAlternative<R> {

    override fun calculateProbabilities(utilities: Map<A, Double>, parameters: P): Map<A, Double> {
        require(utilities.isNotEmpty()) {
            "Received empty utility map!"
        }

        val root = structure.root
        val leafs = structure.leafs

        return synchronized(root) {
            root.reset()

            val situations = utilities.entries.flatMap { (k, v) ->
                leafs[k.choice]?.map { AssociatedSituation(k, it, v) } ?: emptyList()
            }
            val crossNestedSimilarity = situations.groupBy { it.sit.choice }.values.associateWith {
                it.sumOf { sit ->
                    sit.leaf.extractAlphaParameter(parameters)
                }
            }

            require(crossNestedSimilarity.none { it.value != 1.0 }) {
                println(
                    "Your alpha parameters do not sum to 1 for the alternatives ${
                        crossNestedSimilarity.filter { it.value != 1.0 }
                            .map { "${it.key.first().sit.choice} ${it.value}" }
                    }"
                )
            }

            runQueue(situations, parameters)
            situations.groupBy { it.sit }.mapValues { it.value.sumOf { it.probability } }
        }
    }
}

data class CrossNestStructureData<R : Any, A, P>(
    val root: NestStructure<P>.Node,
    val leafs: Map<R, List<NestStructure<P>.Leaf>>,
) where A : ChoiceAlternative<R>

fun interface CrossNestedStructureDataBuilder<R : Any, A, P> where A : ChoiceAlternative<R> {
    fun buildStructure(): CrossNestStructureData<R, A, P>
}
