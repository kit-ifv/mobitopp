package modeling.discreteChoice.structure

import modeling.discreteChoice.UtilityFunction
import modeling.discreteChoice.distribution.NestStructure
import modeling.discreteChoice.distribution.NestStructureData
import modeling.discreteChoice.distribution.NestedStructureDataBuilder
import modeling.discreteChoice.utility.MapBasedUtilityEnumeration
import modeling.discreteChoice.utility.UtilityEnumeration
import modeling.models.ChoiceAlternative

class NestedStructure<R : Any, A, P>(

    lambda: NestedTree<R, A, P>.() -> Unit,
) :
    UtilityEnumerationBuilder<R, A, P>,
    NestedStructureDataBuilder<R, A, P>
    where A : ChoiceAlternative<R> {

    private val leafs: MutableMap<R, NestStructure<P>.Leaf> = mutableMapOf()
    private val utilityFunctions: MutableMap<R, UtilityFunction<A, P>> = mutableMapOf()
    private val root: NestStructure<P>.Nest =
        NestedTree<R, A, P>(leafs, utilityFunctions).nest("root") {
            lambda()
        }

    override fun build(): UtilityEnumeration<R, A, P> = MapBasedUtilityEnumeration<R, A, P>(utilityFunctions)

    override fun buildStructure() = NestStructureData<R, A, P>(root, leafs.toMap())
}

class NestedTree<R : Any, A, P>(
    private val leafs: MutableMap<R, NestStructure<P>.Leaf>,
    private val utilityFunctions: MutableMap<R, UtilityFunction<A, P>>
) : EnumeratedStructureBuilder<R, A, P>,
    NestStructureBuilder<R, P, NestedTree<R, A, P>>
    where A : ChoiceAlternative<R> {

    private val children: MutableList<NestStructure<P>.Node> = mutableListOf()

    override fun new(): NestedTree<R, A, P> = NestedTree<R, A, P>(leafs, utilityFunctions)
    override fun children(): List<NestStructure<P>.Node> = children
    override fun addNest(nest: NestStructure<P>.Nest) {
        children.add(nest)
    }

    override fun addUtilityFunctionByIdentifier(option: R, utilityFunction: UtilityFunction<A, P>) {
        utilityFunctions[option] = utilityFunction
        val element = NestStructure<P>().Leaf()
        children.add(element)
        require(!leafs.containsKey(option)) {
            "A utility function for $option has already been defined in this nest structure. Current elements" +
                " ${leafs.keys} have a utility function associated. "
        }
        leafs[option] = element
    }
}
