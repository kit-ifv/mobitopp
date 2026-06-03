package domain.synthesis.behavior.activitygeneration

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.ISurveyHousehold

/**
 * Strategy for generating preliminary activity schedules for all members of a household.
 *
 * The returned schedules must correspond to [household]'s member order: the
 * schedule at index `i` belongs to the household member at index `i`.
 *
 * This index-based association is intentional. Returning schedules together
 * with their persons would make the person type appear in an output position
 * and would therefore prevent [T] from being contravariant.
 *
 * Implement this interface when schedule generation depends on the household as
 * a whole rather than on isolated persons, for example when modelling joint
 * activities, coordination between members or other intra-household
 * dependencies.
 *
 * @param S the household-level attribute type available to the generator.
 * @param T the person-level attribute type available to the generator.
 */

fun interface GenerateHouseholdActivitySchedule<in S : MinimumHouseholdAttributes, in T : MinimumPersonAttributes> {
    fun generate(household: ISurveyHousehold<S, T>): List<PreliminaryActivitySchedule>
}
