package domain.synthesis.behavior.sharingmemberships

import AssignmentStep
import domain.synthesis.SynthesisPerson
import domain.synthesis.attributes.person.MinimumPersonAttributes

class SharingMembershipsBuilder<T : MinimumPersonAttributes> {

    private val steps: MutableMap<String, AssignmentStep<SynthesisPerson<*, T>, Boolean>> = mutableMapOf()

    fun provider(name: String, lambda: () -> AssignmentStep<SynthesisPerson<*, T>, Boolean>) {
        steps[name] = lambda()
    }

    fun build(): Map<String, AssignmentStep<SynthesisPerson<*, T>, Boolean>> = steps
}
