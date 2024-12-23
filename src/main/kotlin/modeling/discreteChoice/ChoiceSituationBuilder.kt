package modeling.discreteChoice



interface ChoiceSituationBuilder<X: Any, SIT: ChoiceSituation<X>, PARAMS> {
    fun addUtilityFunction(x: X, utilityFunction: UtilityFunction<SIT, PARAMS>)
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
        addUtilityFunction(option, internalUtilityFunction)
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
        addUtilityFunction(option, internalUtilityFunction)
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
/**
 * These functions should reside in the package where utility functions are built, so that they are available
 * whereever someone creates a utility function, without needing to import.
 */
inline val Boolean.D get() = if (this) 1.0 else 0.0
operator fun Boolean.times(double: Double): Double {
    return this.D * double
}