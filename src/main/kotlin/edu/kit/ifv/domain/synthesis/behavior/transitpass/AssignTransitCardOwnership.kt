package edu.kit.ifv.domain.synthesis.behavior.transitpass
import edu.kit.ifv.domain.synthesis.HouseholdAssignmentStep
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes

fun interface AssignTransitCardOwnership<in S : MinimumHouseholdAttributes, in T : MinimumPersonAttributes> :
    HouseholdAssignmentStep<S, T, Boolean>
