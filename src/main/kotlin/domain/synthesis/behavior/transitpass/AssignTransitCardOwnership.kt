package domain.synthesis.behavior.transitpass

import domain.synthesis.HouseholdAssignmentStep
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes

fun interface AssignTransitCardOwnership<in S : MinimumHouseholdAttributes, in T : MinimumPersonAttributes> :
    HouseholdAssignmentStep<S, T, Boolean>