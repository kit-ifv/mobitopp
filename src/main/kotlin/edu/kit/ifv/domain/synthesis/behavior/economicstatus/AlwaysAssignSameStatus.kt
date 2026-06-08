package edu.kit.ifv.domain.synthesis.behavior.economicstatus
import edu.kit.ifv.domain.shared.enums.household.EconomicStatus
import edu.kit.ifv.domain.synthesis.behavior.MinimalistHousehold

/**
 * A trivial implementation to assign the economic status, returning the constructor parameter for each household.
 */
class AlwaysAssignSameStatus(val economicStatus: EconomicStatus) : DetermineEconomicStatus<Any?, Any?> {
    override fun determineStatus(surveyHousehold: MinimalistHousehold<*, *>): EconomicStatus = economicStatus
}
