package synthesis.householdgeneration

import domain.data.Zone
import synthesis.SurveyHousehold
import synthesis.domain.SynthesisHousehold

fun interface HouseholdSynthesis<T> {
    fun synthesize(
        surveyHouseholds: Collection<SurveyHousehold<T>>,
        targets: Collection<Zone>,
        conditions: Map<Zone, List<Rule<Any>>>
    ): Map<Zone, List<SynthesisHousehold<T>>>
}

/**
 * Create synthesis households by placing a copy of each survey household in each zone. Disregard any conditions that
 * may exist.
 */
class TrivialSynthesis<T>: HouseholdSynthesis<T> {
    override fun synthesize(
        surveyHouseholds: Collection<SurveyHousehold<T>>,
        targets: Collection<Zone>,
        conditions: Map<Zone, List<Rule<Any>>>
    ): Map<Zone, List<SynthesisHousehold<T>>> {
        return targets.associateWith { surveyHouseholds.map { it.toSynthesisHousehold() } }
    }
}