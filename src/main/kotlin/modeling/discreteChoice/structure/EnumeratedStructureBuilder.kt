package modeling.discreteChoice.structure

import modeling.discreteChoice.UtilityAssignment
import modeling.discreteChoice.UtilityFunction
import modeling.discreteChoice.distribution.NestStructure
import modeling.discreteChoice.utility.UtilityEnumeration
import modeling.models.ChoiceAlternative

fun interface UtilityAssignmentBuilder<R : Any, A, P> where A : ChoiceAlternative<R> {

    // TODO add name here?

    fun build(): UtilityAssignment<R, A, P>
}

fun interface UtilityEnumerationBuilder<R : Any, A, P> : UtilityAssignmentBuilder<R, A, P>
    where A : ChoiceAlternative<R> {

    override fun build(): UtilityEnumeration<R, A, P>
}

interface EnumeratedStructureBuilder<R : Any, A, P> where A : ChoiceAlternative<R> {
    /**
     * Checking whether a situation is equal to a certain element x is a concretization of the more general
     * concept of when to apply a rule.
     */
    fun addUtilityFunctionByIdentifier(option: R, utilityFunction: UtilityFunction<A, P>)

    /**
     * Add an option to a nest block via specifying the concrete choice [option] as well as a [utilityFunction] to
     * create a utility function from the parameters and choice situations.
     */
    fun option(option: R, utilityFunction: P.(A) -> Double) {
        val internalUtilityFunction = UtilityFunction { alternative: A, parameterObject: P ->
            utilityFunction.invoke(
                parameterObject,
                alternative
            )
        }
        addUtilityFunctionByIdentifier(option, internalUtilityFunction)
    }

    /**
     * Add an option to a nest block via specifying the concrete choice [option] as well as a [utilityFunction] to
     * create a utility function from the parameters and choice situations. Additionally allows a conversion
     * to a different parameter object [T] in case the original parameter object is too verbose/complex
     */
    fun <T> option(option: R, parameters: P.() -> T, utilityFunction: T.(A) -> Double) {
        val internalUtilityFunction = UtilityFunction { alternative: A, parameterObject: P ->
            utilityFunction.invoke(
                parameterObject.parameters(),
                alternative
            )
        }
        addUtilityFunctionByIdentifier(option, internalUtilityFunction)
    }

    /**
     * Theoretically you can also specify options via their Situation instantiations, but that seems weird
     */
    fun option(option: A, utilityFunction: P.(A) -> Double) {
        option(option.choice, utilityFunction)
    }

    /**
     * Theoretically you can also specify options via their Situation instantiations, but that seems weird
     */
    fun <T> option(option: A, parameters: P.() -> T, utilityFunction: T.(A) -> Double) {
        option(option.choice, parameters, utilityFunction)
    }
}

interface RuleBasedStructureBuilder<R : Any, A, P> : EnumeratedStructureBuilder<R, A, P>
    where A : ChoiceAlternative<R> {
    fun addUtilityFunctionByRule(rule: (A) -> Boolean, utilityFunction: UtilityFunction<A, P>)

    override fun addUtilityFunctionByIdentifier(option: R, utilityFunction: UtilityFunction<A, P>) {
        addUtilityFunctionByRule(rule = { it.choice == option }, utilityFunction)
    }

    fun <T> rule(rule: (A) -> Boolean, parameters: P.() -> T, utilityFunction: T.(A) -> Double) {
        val internalUtilityFunction = UtilityFunction { alternative: A, parameterObject: P ->
            utilityFunction.invoke(
                parameterObject.parameters(),
                alternative
            )
        }
        addUtilityFunctionByRule(rule, internalUtilityFunction)
    }

    fun rule(rule: (A) -> Boolean, utilityFunction: P.(A) -> Double) {
        val internalUtilityFunction = UtilityFunction { alternative: A, parameterObject: P ->
            utilityFunction.invoke(
                parameterObject,
                alternative
            )
        }
        addUtilityFunctionByRule(rule, internalUtilityFunction)
    }

    fun ruleForAll(utilityFunction: P.(A) -> Double) {
        rule({ true }, utilityFunction)
    }

    fun <T> ruleForAll(parameters: P.() -> T, utilityFunction: T.(A) -> Double) {
        rule({ true }, parameters, utilityFunction)
    }
}

/**
 * These functions should reside in the package where utility functions are built, so that they are available
 * whereever someone creates a utility function, without needing to import.
 */
inline val Boolean.D get() = if (this) 1.0 else 0.0
operator fun Boolean.times(double: Double): Double {
    return this.D * double
}

interface NestStructureBuilder<R, P, B> where B : NestStructureBuilder<R, P, B> {

    fun new(): B
    fun children(): List<NestStructure<P>.Node>
    fun addNest(nest: NestStructure<P>.Nest)

    fun nest(name: String, lambda: Double = 1.0, content: B.() -> Unit) =
        nest(name, { lambda }, content)

    fun nest(
        name: String,
        lambdaParameter: P.() -> Double,
        content: B.() -> Unit
    ): NestStructure<P>.Nest {
        val newBuilder = new()
        newBuilder.content()

        val childNodes = newBuilder.children()
        require(childNodes.isNotEmpty()) {
            "Cannot create an empty nest. You must add at least one option in a nest block using " +
                "the option(...) { } syntax. This includes the implicit root nest block. "
        }
        val nest = NestStructure<P>().Nest(childNodes, name, lambdaParameter)
        childNodes.forEach { it.parent = nest }
        addNest(nest)
        return nest
    }
}
