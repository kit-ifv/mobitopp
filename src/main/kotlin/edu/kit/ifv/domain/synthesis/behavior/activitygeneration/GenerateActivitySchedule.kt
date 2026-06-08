package edu.kit.ifv.domain.synthesis.behavior.activitygeneration
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson

/**
 * Strategy for generating a preliminary activity schedule for a single person.
 *
 * This interface is a convenience specialization of [GenerateHouseholdActivitySchedule]
 * for generators that can treat each household member independently.
 *
 * The household-level implementation applies this person-level strategy to each
 * member of the supplied household in member order. Therefore, the returned
 * schedule at index `i` belongs to `household.members[i]`.
 *
 * Implement this interface when a person's schedule can be generated without
 * considering the schedules or attributes of other household members.
 *
 * If schedule generation needs household-level coordination, such as joint
 * activities, shared resources or dependencies between household members,
 * implement [GenerateHouseholdActivitySchedule] directly instead.
 *
 * @param S the household-level attribute type required by the household-level
 *          interface. It is not used by the default implementation except
 *          through the supplied household.
 * @param T the person-level attribute type available to the generator.
 */
fun interface GenerateActivitySchedule<in S : MinimumHouseholdAttributes, in T : MinimumPersonAttributes> :
    GenerateHouseholdActivitySchedule<S, T> {
    fun generate(person: SurveyPerson<T>): PreliminaryActivitySchedule
    override fun generate(household: ISurveyHousehold<S, T>): List<PreliminaryActivitySchedule> =
        household.members.map {
            generate(it)
        }
}
