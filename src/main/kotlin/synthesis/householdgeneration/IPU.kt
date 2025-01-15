package synthesis.householdgeneration

import domain.data.Zone
import synthesis.SurveyHousehold
import synthesis.domain.SynthesisHousehold
import synthesis.pickWithReplacement
import utils.collections.equivalenceClasses
import utils.collections.sortByValues

typealias HouseholdEquivalence<T> = Map<SurveyHousehold<T>, Set<SurveyHousehold<T>>>

class IPU<T : Any>(val algorithm: (vectors: Collection<ScalableVector>, Collection<Observer>) -> Collection<ScalableVector>) :
    HouseholdSynthesis<T> {
    private var overflowCounter: Double = 0.0
    val convertNumbersToHousehold: (HouseholdEquivalence<T>, SurveyHousehold<T>, Double) -> List<SurveyHousehold<T>> =
        { e, h, d ->
            val set = e.getOrElse(h) { throw NoSuchElementException("Somehow this happened") }
            overflowCounter += d - d.toInt()
            if (overflowCounter >= 1.0) {
                overflowCounter--
                set.pickWithReplacement(d.toInt() + 1)
            } else {
                set.pickWithReplacement(d.toInt())
            }
        }

    override fun synthesize(
        surveyHouseholds: Collection<SurveyHousehold<T>>,
        targets: Collection<Zone>,
        conditions: Map<Zone, List<Rule<Any>>>
    ): Map<Zone, List<SynthesisHousehold<T>>> {
        val uniques = surveyHouseholds.toSet()
        // TODO equivalnece classes should be determined based on the rules
        val eqD = uniques.equivalenceClasses { hh1, hh2 -> hh1.representative == hh2.representative }
            .sortByValues { a, b -> b.size.compareTo(a.size) }

        val results = targets.associateWith { synZone ->
            val rulesForZone = conditions.getOrDefault(synZone, emptyList())
            require(rulesForZone.isNotEmpty()) {
                "Cannot run IPU for target zone ${synZone.id}, no rules found. Rules are present for ${conditions.keys.map { it.id }}"
            }
            val internal = synZone.calculate(eqD, rulesForZone)
            val output = internal.flatMap {
                convertNumbersToHousehold(eqD, it.first, it.second)
            }
            val check = rulesForZone.associateWith { it.filter(output) }
            val otherCheck = rulesForZone.associateWith { it.verify(output) }
            output.map { it.toSynthesisHousehold() }
        }
        return results
    }

    private fun Zone.calculate(
        surveyHouseholds: HouseholdEquivalence<T>,
        rules: List<Rule<Any>>
    ): Collection<Pair<SurveyHousehold<T>, Double>> {
        val vectorMapping = surveyHouseholds.keys.associateWith { rules.vectorized(it) }
        val vectors = vectorMapping.values
        // TODO maybe assign rule -> Observer so that higher order logic may interact with these.
        val observers = rules.withIndex().map { rule ->
            Observer(
                rule.value.description,
                rule.index,
                vectors.filter { it.appliesTo(rule) },
                rule.value.target
            )
        }

        algorithm(vectors, observers)
        return vectorMapping.entries.map { it.key to it.value.scalar }
    }

    private fun ScalableVector.appliesTo(indexRule: IndexedValue<Rule<Any>>): Boolean {
        return this.content[indexRule.index] != 0
    }
}
