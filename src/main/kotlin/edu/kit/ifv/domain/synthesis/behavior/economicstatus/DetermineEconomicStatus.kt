package edu.kit.ifv.domain.synthesis.behavior.economicstatus
import edu.kit.ifv.domain.shared.enums.household.EconomicStatus
import edu.kit.ifv.domain.synthesis.behavior.MinimalistHousehold

/**
 * Assign an economic status to a household
 */
fun interface DetermineEconomicStatus<in S, in T> {
    fun determineStatus(surveyHousehold: MinimalistHousehold<S, T>): EconomicStatus
}
