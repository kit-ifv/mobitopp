package edu.kit.ifv.domain.synthesis

import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold
import edu.kit.ifv.domain.synthesis.behavior.MinimalistPerson
import kotlin.random.Random

interface SynthesisRandomProvider<in S : MinimumHouseholdAttributes, in T : MinimumPersonAttributes> {
    fun provideFor(household: ISurveyHousehold<S, T>): Random
    fun provideFor(person: MinimalistPerson<T>): Random
}

/**
 * Provides the random seeds for persons and households and tracks them in a map.
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