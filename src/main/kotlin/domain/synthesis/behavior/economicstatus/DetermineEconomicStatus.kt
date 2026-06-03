package domain.synthesis.behavior.economicstatus

import domain.shared.enums.household.EconomicStatus
import domain.synthesis.behavior.MinimalistHousehold

/**
 * Assign an economic status to a household
 */
fun interface DetermineEconomicStatus<in S, in T> {
    fun determineStatus(surveyHousehold: MinimalistHousehold<S, T>): EconomicStatus
}
