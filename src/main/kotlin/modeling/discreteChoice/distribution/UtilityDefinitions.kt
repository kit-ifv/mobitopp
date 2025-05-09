package modeling.discreteChoice.distribution

//
// /**
// * An allocated function knows what options are available
// */
// interface OptionDistributionFunction<X : Any, SIT : ChoiceAlternative<X>, PARAMS> :
//    ExtractableDistributionFunction<X, SIT, PARAMS> {
//    val options: Set<X> get() = translation.keys
//    val translation: Map<X, UtilityFunction<SIT, PARAMS>>
//    override fun translation(target: SIT): UtilityFunction<SIT, PARAMS> = translation.getOrElse(target.choice) {
//        throw NoSuchElementException("There is no utility function for $target in this distribution function")
//    }
// }
//
// /**
// * If we have this class we have the ability to predetermine the utility function for a given situation SIT
// */
// interface ExtractableDistributionFunction<X : Any, SIT : ChoiceAlternative<X>, PARAMS> :
//    DistributionFunction<SIT, PARAMS> {
//    val name get() = "Unnamed Distribution Function"
//    fun translation(target: SIT): UtilityFunction<SIT, PARAMS>
//    fun calculateProbabilities(alternatives: Set<SIT>, parameters: PARAMS): Map<SIT, Double> {
//        return calculateProbabilities(
//            alternatives.associateWith { translation(it).calculateUtility(it, parameters) },
//            parameters
//        )
//    }
// }
