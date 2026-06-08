package edu.kit.ifv.domain.synthesis

import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold
import edu.kit.ifv.domain.synthesis.behavior.MinimalistPerson
import kotlin.random.Random
/**
 * Supplies simulation-specific random generators for households and persons.
 *
 * Implementations may either create deterministic generators from stable identifiers
 * or keep mutable generator instances for each household and person.
 */
interface SynthesisRandomProvider<in S : MinimumHouseholdAttributes, in T : MinimumPersonAttributes> {
    fun provideFor(household: ISurveyHousehold<S, T>): Random
    fun provideFor(person: MinimalistPerson<T>): Random
}

/**
 * [SynthesisRandomProvider] that creates deterministic random generators from a
 * global [seed] and stores them in maps.
 *
 * A generator is created lazily the first time a household or person is requested.
 * Subsequent calls for the same household or person return the previously created
 * generator, preserving its mutable random state across synthesis steps.
 *
 * The [householdRandomSpawner] and [personRandomSpawner] functions define how new
 * generators are initialized. They receive the global seed and the requested entity.
 *
 * This provider keeps random state external to the household and person classes.
 * As a consequence, generated values are reproducible for a fixed seed and stable
 * entity identity, but also depend on the order in which each entity's generator is
 * consumed.
 */
class SeededProvider<in S : MinimumHouseholdAttributes, in T : MinimumPersonAttributes>(
    private val seed: Long,
    private val householdRandomSpawner: (Long, ISurveyHousehold<S, T>) -> Random = { seed, household ->
        Random(seed + household.surveyHouseholdId)
    },
    private val personRandomSpawner: (Long, MinimalistPerson<T>) -> Random,
    ) :
    SynthesisRandomProvider<S, T> {
    private val householdMap: MutableMap<ISurveyHousehold<S, T>, Random> = mutableMapOf()
    private val personMap: MutableMap<MinimalistPerson<T>, Random> = mutableMapOf()
    override fun provideFor(household: ISurveyHousehold<S, T>): Random {
        return householdMap.getOrPut(household) {
            householdRandomSpawner(seed, household)
        }
    }

    override fun provideFor(person: MinimalistPerson<T>): Random {
        return personMap.getOrPut(person) { personRandomSpawner(seed, person) }
    }
}