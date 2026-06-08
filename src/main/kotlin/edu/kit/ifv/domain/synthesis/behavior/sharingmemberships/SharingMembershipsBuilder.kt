package edu.kit.ifv.domain.synthesis.behavior.sharingmemberships
import edu.kit.ifv.domain.synthesis.AssignmentStep
import edu.kit.ifv.domain.synthesis.SynthesisPerson
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes

class SharingMembershipsBuilder<S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> {

    private val steps: MutableMap<String, AssignmentStep<SynthesisPerson<S, T>, Boolean>> = mutableMapOf()

    fun provider(name: String, lambda: () -> AssignmentStep<SynthesisPerson<S, T>, Boolean>) {
        steps[name] = lambda()
    }

    fun build(): Map<String, AssignmentStep<SynthesisPerson<S, T>, Boolean>> = steps
}
