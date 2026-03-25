package domain.synthesis.behavior.economicstatus

import domain.synthesis.attributes.person.HasAge
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.data.EconomicStatus
import edu.kit.ifv.units.ClosedCurrencyRange

/**
 * Assign an economic status to a household
 */
fun interface DetermineEconomicStatus<in S, in T> {
    fun determineStatus(surveyHousehold: MinimalistHousehold<S, T>): EconomicStatus
}



