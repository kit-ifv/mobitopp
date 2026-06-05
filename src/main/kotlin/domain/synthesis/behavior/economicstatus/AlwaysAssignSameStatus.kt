package domain.synthesis.behavior.economicstatus

import domain.shared.enums.household.EconomicStatus
import domain.synthesis.behavior.MinimalistHousehold

/**
 * A trivial implementation to assign the economic status, returning the constructor parameter for each household.
 */
class AlwaysAssignSameStatus(val economicStatus: EconomicStatus) : DetermineEconomicStatus<Any?, Any?> {
    override fun determineStatus(surveyHousehold: MinimalistHousehold<*, *>): EconomicStatus = economicStatus
}
