package domain.synthesis.behavior.householdgeneration

import domain.shared.location.Zone
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SurveyHousehold
import domain.synthesis.behavior.domain.SynthesisHousehold

/**
 * A functional interface that synthesizes households for different zones based on the provided [SurveyHousehold] data
 * and the associated rules for each zone.
 *
 * The [synthesize] function processes a collection of [SurveyHousehold] objects and applies the rules for each zone
 * to generate synthesized households, returning a mapping of zones to the synthesized households within those zones.
 *
 * @param T The type of data associated with the household (e.g., demographic information).
 */
fun interface HouseholdSynthesis<AREA, T> : GenericPopulationSynthesis<AREA, T> {
    /**
     * Synthesizes households based on the provided survey data and rules for each zone.
     *
     * @param surveyHouseholds A collection of [SurveyHousehold] objects containing the raw household data.
     * @param conditions The rules for each zone that define how to synthesize the households.
     * @return A map of [Zone] to a list of [SynthesisHousehold] objects representing the synthesized households for
     *         each zone.
     */
    fun synthesize(
        surveyHouseholds: Collection<SurveyHousehold<out T>>,
        conditions: Map<AREA, List<Rule<in T, ISurveyHousehold<out T>>>>
    ): Map<AREA, List<SynthesisHousehold<out T>>> {
        return synthesize(conditions.keys.toList())
    }

    override fun synthesize(targetAreas: List<AREA>): Map<AREA, List<SynthesisHousehold<out T>>>
}

/**
 * Create synthesis households by placing a copy of each survey household in each zone. Disregard any conditions that
 * may exist.
 */
class TrivialSynthesis<AREA, T>(
    private val surveyHouseholds: Collection<SurveyHousehold<out T>>
) : HouseholdSynthesis<AREA, T> {
    override fun synthesize(
        targetAreas: List<AREA>
    ): Map<AREA, List<SynthesisHousehold<out T>>> {
        return targetAreas.associateWith { surveyHouseholds.map { it.toSynthesisHousehold() } }
    }
}
