package modeling.discreteChoice

interface OptionBasedSituationBuilder<X : Any, SIT : ChoiceSituation<X>, PARAMS> {
    /**
     * Checking whether a situation is equal to a certain element x is a concretization of the more general
     * concept of when to apply a rule.
     */
    fun addUtilityFunctionByIdentifier(x: X, utilityFunction: UtilityFunction<SIT, PARAMS>)

    /**
     * Add an option to a nest block via specifying the concrete choice [option] as well as a [utilityFunction] to
     * create a utility function from the parameters and choice situations.
     */
    fun option(option: X, utilityFunction: PARAMS.(SIT) -> Double) {
        val internalUtilityFunction = UtilityFunction { alternative: SIT, parameterObject: PARAMS ->
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
     * to a different parameter object [P] in case the original parameter object is too verbose/complex
     */
    fun <P> option(option: X, parameters: PARAMS.() -> P, utilityFunction: P.(SIT) -> Double) {
        val internalUtilityFunction = UtilityFunction { alternative: SIT, parameterObject: PARAMS ->
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
    fun option(option: SIT, utilityFunction: PARAMS.(SIT) -> Double) {
        option(option.choice, utilityFunction)
    }

    /**
     * Theoretically you can also specify options via their Situation instantiations, but that seems weird
     */
    fun <P> option(option: SIT, parameters: PARAMS.() -> P, utilityFunction: P.(SIT) -> Double) {
        option(option.choice, parameters, utilityFunction)
    }
}

interface RuleBasedSituationBuilder<X : Any, SIT : ChoiceSituation<X>, PARAMS> {
    fun addUtilityFunctionByRule(rule: (SIT) -> Boolean, utilityFunction: UtilityFunction<SIT, PARAMS>)

    fun <P> rule(rule: (SIT) -> Boolean, parameters: PARAMS.() -> P, utilityFunction: P.(SIT) -> Double) {
        val internalUtilityFunction = UtilityFunction { alternative: SIT, parameterObject: PARAMS ->
            utilityFunction.invoke(
                parameterObject.parameters(),
                alternative
            )
        }
        addUtilityFunctionByRule(rule, internalUtilityFunction)
    }
    fun rule(rule: (SIT) -> Boolean, utilityFunction: PARAMS.(SIT) -> Double) {
        val internalUtilityFunction = UtilityFunction { alternative: SIT, parameterObject: PARAMS ->
            utilityFunction.invoke(
                parameterObject,
                alternative
            )
        }
        addUtilityFunctionByRule(rule, internalUtilityFunction)
    }
    fun ruleForAll(utilityFunction: PARAMS.(SIT) -> Double) {
        rule({ true }, utilityFunction)
    }

    fun <P> ruleForAll(parameters: PARAMS.() -> P, utilityFunction: P.(SIT) -> Double) {
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
