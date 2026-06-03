package domain.synthesis

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.ISurveyHousehold

/**
 * This functional interface is the insertion point to spawn the Households used in the core framework.
 * As per the Attribute design, Each Household has its own attribute class S and each person has its attribute class
 * T. You can implement anything here as long as the result is then the households with which you want to work.
 *
 * We provide a Csv extractor, but you can let your imagination run wild.
 */
fun interface GenerateSurveyHouseholds<S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> {
    fun generateSurveyHouseholds(): Collection<ISurveyHousehold<S, T>>
}
