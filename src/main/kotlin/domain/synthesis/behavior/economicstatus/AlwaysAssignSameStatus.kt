package domain.synthesis.behavior.economicstatus

import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.data.household.EconomicStatus

/**
 * A trivial implementation to assign the economic status, returning the constructor parameter for each household.
 */
class AlwaysAssignSameStatus(val economicStatus: EconomicStatus) : DetermineEconomicStatus<Any?, Any?> {
    override fun determineStatus(surveyHousehold: MinimalistHousehold<*, *>): EconomicStatus = economicStatus
}
