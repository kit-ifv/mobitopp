package domain.synthesis.behavior.fixedDestinations

import domain.shared.location.StandardLocation
import domain.synthesis.SynthesisPerson
import domain.synthesis.attributes.person.MinimumPersonAttributes

data class AssignedLocation<T : MinimumPersonAttributes>(
    val targetPerson: SynthesisPerson<*, T>,
    val assignedLocation: StandardLocation,
)
