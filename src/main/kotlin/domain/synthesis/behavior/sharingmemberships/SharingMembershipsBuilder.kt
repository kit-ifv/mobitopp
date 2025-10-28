package domain.synthesis.behavior.sharingmemberships

import AssignmentStep
import domain.synthesis.behavior.domain.SynthesisPerson

class SharingMembershipsBuilder<T> {

    private val steps: MutableMap<String, AssignmentStep<SynthesisPerson<out T>, Boolean>> = mutableMapOf()

    fun provider(name: String, lambda: () -> AssignmentStep<SynthesisPerson<out T>, Boolean>) {
        steps[name] = lambda()
    }

    fun build(): Map<String, AssignmentStep<SynthesisPerson<out T>, Boolean>> = steps

}