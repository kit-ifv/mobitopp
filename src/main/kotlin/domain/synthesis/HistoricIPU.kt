package domain.synthesis

import domain.synthesis.behavior.householdgeneration.GenericIPU
import domain.synthesis.behavior.householdgeneration.HierarchicalPopulationSynthesis
import domain.synthesis.behavior.householdgeneration.HierarchicalRuleProvider
import domain.synthesis.behavior.householdgeneration.RuleObserver
import domain.synthesis.behavior.householdgeneration.SampleAndCollect
import domain.synthesis.behavior.householdgeneration.ScalableVector
import domain.synthesis.behavior.householdgeneration.toScalableVector
import utils.collections.invertMap

class HistoricIPU<AREA, H>(
    override val ruleProvider: HierarchicalRuleProvider<AREA, H>,
    val seedHouseholds: Collection<H>,
    val ipu: GenericIPU = GenericIPU.Companion.legacy,
) : HierarchicalPopulationSynthesis<AREA, H> {
    val extractor = SampleAndCollect<H>()
    override fun synthesize(
        highestArea: AREA,
        targetAreas: Collection<AREA>,
    ): Map<AREA, List<H>> {
        val vectors = calculate(highestArea, targetAreas)
        // Now the vectors should be scaled via side effect
        return vectors.entries.associate { (k, v) ->
            k to extractor.extract(v)
        }
    }

    fun calculate(
        highestArea: AREA,
        targetAreas: Collection<AREA>,
    ): Map<AREA, Map<ScalableVector, List<H>>> {
        val parentRuleset = ruleProvider.getRules(highestArea)
        val temp = targetAreas.associateWith {
            val childRules = ruleProvider.getRules(it)
            val rules = parentRuleset + childRules
            val vectors = seedHouseholds.associateWith { rules.toScalableVector(it) }
            val inverseMap = vectors.invertMap()
            val uniqueVectors = inverseMap.keys
            val ruleObservers = rules.withIndex().drop(parentRuleset.size).map {
                RuleObserver.Companion.fromRule(it.value, it.index, uniqueVectors)
            }
            inverseMap to ruleObservers
        }
        val (vectors, observers) = temp.values.unzip()
        val allHouseholdsEncoded = vectors.map { it.keys }.flatten()
        val additionalObservers = parentRuleset.withIndex().map {
            RuleObserver.Companion.fromRule(it.value, it.index, allHouseholdsEncoded)
        }

        ipu.run(allHouseholdsEncoded, additionalObservers + observers.flatten())
        return temp.mapValues { it.value.first }
    }
}
