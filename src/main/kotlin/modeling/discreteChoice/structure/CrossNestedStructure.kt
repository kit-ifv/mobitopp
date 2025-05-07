package modeling.discreteChoice.structure

import modeling.discreteChoice.UtilityFunction
import modeling.discreteChoice.distribution.CrossNestStructureData
import modeling.discreteChoice.distribution.CrossNestedStructureDataBuilder
import modeling.discreteChoice.distribution.NestStructure
import modeling.discreteChoice.utility.MapBasedUtilityEnumeration
import modeling.discreteChoice.utility.UtilityEnumeration
import modeling.models.ChoiceAlternative

class CrossNestedStructure<R : Any, A, P>(
    content: CrossNestedDAG<R, P>.() -> Unit,
) : CrossNestedStructureDataBuilder<R, A, P> where A : ChoiceAlternative<R> {

    private val leafsByOption: MutableMap<R, MutableList<NestStructure<P>.Leaf>> = mutableMapOf()
    private val root: NestStructure<P>.Nest =
        CrossNestedDAG<R, P>(leafsByOption).nest("root") {
            content()
        }

    override fun buildStructure() = CrossNestStructureData<R, A, P>(root, leafsByOption)

    fun withUtility(
        lambda: EnumeratedStructureBuilder<R, A, P>.() -> Unit,
    ) = CrossNestedWithUtilities<R, A, P>(this, lambda)
}

class CrossNestedDAG<R, P>(
    val leafsByOption: MutableMap<R, MutableList<NestStructure<P>.Leaf>>
) : NestStructureBuilder<R, P, CrossNestedDAG<R, P>> {

    private val children: MutableList<NestStructure<P>.Node> = mutableListOf()

    override fun new(): CrossNestedDAG<R, P> = CrossNestedDAG<R, P>(leafsByOption = leafsByOption)
    override fun children(): List<NestStructure<P>.Node> = children
    override fun addNest(nest: NestStructure<P>.Nest) {
        children.add(nest)
    }

    fun option(option: R, name: String = option.toString(), alpha: Double = 1.0) =
        option(option, name) { alpha }

    fun option(option: R, name: String = option.toString(), alpha: P.() -> Double): NestStructure<P>.Leaf {
        val element = NestStructure<P>().Leaf(extractAlphaParameter = alpha, name = name)
        children.add(element)
        val globalEntries = leafsByOption.getOrPut(option) {
            mutableListOf()
        }
        globalEntries.add(element)
        return element
    }
}

class CrossNestedWithUtilities<R : Any, A, P>(
    private val structure: CrossNestedStructure<R, A, P>,
    lambda: EnumeratedStructureBuilder<R, A, P>.() -> Unit,
) : EnumeratedStructureBuilder<R, A, P>,
    UtilityEnumerationBuilder<R, A, P>,
    CrossNestedStructureDataBuilder<R, A, P> by structure
    where A : ChoiceAlternative<R> {

    private var utilityFunctions: MutableMap<R, UtilityFunction<A, P>> = mutableMapOf()

    init {
        lambda()
    }

    override fun addUtilityFunctionByIdentifier(
        option: R,
        utilityFunction: UtilityFunction<A, P>
    ) {
        require(!utilityFunctions.containsKey(option)) {
            "Duplicate: A utility function for $option has already been defined in this structure. " +
                "Current elements ${utilityFunctions.keys} already have a utility function associated. "
        }
        utilityFunctions[option] = utilityFunction
    }

    override fun build(): UtilityEnumeration<R, A, P> = MapBasedUtilityEnumeration(utilityFunctions)
}
