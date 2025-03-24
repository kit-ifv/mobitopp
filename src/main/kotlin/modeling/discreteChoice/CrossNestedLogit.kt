package modeling.discreteChoice

/**
 * Leafs maps to a non empty list of leaves, as the structure in a crossnested logit may allow for an option to occur
 * multiple times
 */
class CrossNestedLogit<X : Any, SIT : ChoiceSituation<X>, PARAMS>(
    private val leafs: Map<X, List<NestStructure<PARAMS>.Leaf>>,
    private val root: NestStructure<PARAMS>.Nest,
) : OptionDistributionFunction<X, SIT, PARAMS> {

    override lateinit var translation: Map<X, UtilityFunction<SIT, PARAMS>>
    override fun calculateProbabilities(evaluators: Map<SIT, Double>, parameters: PARAMS): Map<SIT, Double> {
        return synchronized(this) {
            root.reset()

            val situations = evaluators.entries.flatMap { (k, v) ->
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

    fun setUtilityFunctions(lambda: UtilityMapBuilder<X, SIT, PARAMS>.() -> Unit) {
        translation = UtilityMapBuilder<X, SIT, PARAMS>().apply(lambda).build()
    }

    fun updateUtilityFunctions(lambda: UtilityMapBuilder<X, SIT, PARAMS>.() -> Unit) {
        translation = UtilityMapBuilder(translation.toMutableMap()).apply(lambda).build()
    }

    companion object {

        class CrossNestedLogitBuilder<X : Any, SIT : ChoiceSituation<X>, PARAMS> :
            OptionBasedSituationBuilder<X, SIT, PARAMS> {
            private lateinit var nestStructure: MutableList<NestStructure<PARAMS>.Node>
            private val entriesFor: MutableMap<X, MutableList<NestStructure<PARAMS>.Leaf>> = mutableMapOf()
            lateinit var root: NestStructure<PARAMS>.Nest

            @Suppress("EmptyFunctionBlock")
            override fun addUtilityFunctionByIdentifier(x: X, utilityFunction: UtilityFunction<SIT, PARAMS>) {
            }

            fun build(): CrossNestedLogit<X, SIT, PARAMS> {
                println(nestStructure)
                return CrossNestedLogit(entriesFor, root)
            }

            fun structure(lambda: NestStructureBuilder<X, PARAMS>.() -> Unit): MutableList<NestStructure<PARAMS>.Node> {
                val builder = NestStructureBuilder<X, PARAMS>(entriesFor)
                root = builder.nest(name = "root", lambda = { 1.0 }) {
                    lambda()
                }
                nestStructure = builder.build()
                return builder.build()
            }
        }

        fun <X : Any, SIT : ChoiceSituation<X>, PARAMS> build(
            lambda: CrossNestedLogitBuilder<X, SIT, PARAMS>.() -> Unit
        ): CrossNestedLogit<X, SIT, PARAMS> {
            val builder = CrossNestedLogitBuilder<X, SIT, PARAMS>()
            builder.apply(lambda)
            return builder.build()
        }

        class NestStructureBuilder<X, PARAMS>(val entriesFor: MutableMap<X, MutableList<NestStructure<PARAMS>.Leaf>>) {
            fun nest(
                name: String = "Unnamed Nest",
                lambda: PARAMS.() -> Double = { 1.0 },
                buildInstruction: NestStructureBuilder<X, PARAMS>.() -> Unit
            ): NestStructure<PARAMS>.Nest {
                val newBuilder = NestStructureBuilder<X, PARAMS>(entriesFor = entriesFor)
                newBuilder.apply(buildInstruction)
                val childNodes = newBuilder.build()
                val nest = NestStructure<PARAMS>().Nest(childNodes, name, lambda)
                childNodes.forEach { it.parent = nest }
                childs.add(nest)
                return nest
            }

            fun option(x: X, name: String = x.toString(), alpha: PARAMS.() -> Double = { 1.0 }) {
                val element = NestStructure<PARAMS>().Leaf(extractAlphaParameter = alpha, name = name)
                childs.add(element)
                val globalEntries = entriesFor.getOrPut(x) {
                    mutableListOf()
                }
                globalEntries.add(element)
            }

            private val childs: MutableList<NestStructure<PARAMS>.Node> = mutableListOf()
            fun build(): MutableList<NestStructure<PARAMS>.Node> {
                return childs
            }
        }
    }
}

class UtilityMapBuilder<X, SIT, PARAMS>(val map: MutableMap<X, UtilityFunction<SIT, PARAMS>> = mutableMapOf()) {

    fun option(x: X, utilityFunction: PARAMS.(SIT) -> Double) {
        map[x] = UtilityFunction { alternative: SIT, parameterObject: PARAMS ->
            utilityFunction.invoke(
                parameterObject,
                alternative
            )
        }
    }
    fun <P> option(option: X, parameters: PARAMS.() -> P, utilityFunction: P.(SIT) -> Double) {
        val internalUtilityFunction = UtilityFunction { alternative: SIT, parameterObject: PARAMS ->
            utilityFunction.invoke(
                parameterObject.parameters(),
                alternative
            )
        }
        map[option] = internalUtilityFunction
    }

    fun build(): Map<X, UtilityFunction<SIT, PARAMS>> {
        return map
    }
}
