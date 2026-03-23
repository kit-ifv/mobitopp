package domain.synthesis.behavior.sharingmemberships

import AssignmentStep
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.domain.SynthesisPerson

class SharingMembershipsBuilder<T : MinimumPersonAttributes> {

    private val steps: MutableMap<String, AssignmentStep<SynthesisPerson<*, T>, Boolean>> = mutableMapOf()

    fun provider(name: String, lambda: () -> AssignmentStep<SynthesisPerson<*,  T>, Boolean>) {
        steps[name] = lambda()
    }

    fun build(): Map<String, AssignmentStep<SynthesisPerson<*,  T>, Boolean>> = steps
}
