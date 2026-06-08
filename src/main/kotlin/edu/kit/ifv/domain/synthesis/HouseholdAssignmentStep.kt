package edu.kit.ifv.domain.synthesis
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson
import kotlin.random.Random

@Suppress("SpacingAroundColon")
fun interface HouseholdAssignmentStep<in S, in T : MinimumPersonAttributes, out O> :
    AssignmentStep<ISurveyHousehold<S, T>, List<O>> where S : MinimumHouseholdAttributes {
    context(household: ISurveyHousehold<S, T>, random: Random)
    fun assignForPerson(person: SurveyPerson<T>): O

    context(random: Random)
    override fun assign(input: ISurveyHousehold<S, T>): List<O> = context(input) {
        input.members.map { member -> assignForPerson(member) }
    }
}
