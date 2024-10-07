package usecases.choicemodels.destinationchoice

import domain.data.Person

/**
 * The destination choice function taken from transmove requires 6 individual blocks for the utility calculation.
 *
 * U_zone = [constant] + [attractiveness] * Attractiveness + [carLogsum] * CarLogsum + [carLogsumFix] + CarLogsumFix +
 * [flexLogsum] * FlexLogsum + [flexFixLogsum] * FlexFixLogsum
 *
 * Note that the examples of influences
 *
 * @property constant Constant influences on the utility function which should be applied without any attached variable,
 * such as [StandardDestinationDistance] or [StandardDestinationParkstress] (though by composition there may be additional factors)
 * @property attractiveness Attributes that should influence the impact of the attractiveness onto the destination utility,
 * in the original utility function the following attributes are influencing the attractiveness score: [DestinationAge],
 * [DestinationEmployment], [DestCommuterTicket], [SurplusCars], [DestinationEconomicStatus], [DestinationUmland], [StandardDestinationDistance]
 *
 * @property carLogsum the parameters shifting the influence of the IV logsum. In the original implementation the logsum
 * is calculated as ln(e(U_CAR) + e(U_PASSENGER)), taking travel time and cost from origin to destination.
 *
 * @property carLogsumFix the parameters shifting the influence of the IV logsum. In the original implementation the logsum
 *  * is calculated as ln(e(U_CAR) + e(U_PASSENGER)), taking travel time and cost from destination to next fixed pole.
 * @property flexLogsum the parameters shifting the influence of the Flexible modes logsum. In the original implementation
 * the logsum is calculated as ln(e(U_BIKE) + e(U_PUBLICTRANSPORT) + e(U_PED), taking travel time and cost from origin to destination.
 *  @property flexLogsum the parameters shifting the influence of the Flexible modes logsum. In the original implementation
 *  the logsum is calculated as ln(e(U_BIKE) + e(U_PUBLICTRANSPORT) + e(U_PED), taking travel time and cost from destination to next fixed pole.
 */
interface DestinationRequirements {
    val constant: CombinedDestinationParameters
    val attractiveness: AttractivenessParameters
    val carLogsum: CombinedDestinationParameters
    val carLogsumFix: CombinedDestinationParameters
    val flexLogsum: CombinedDestinationParameters
    val flexFixLogsum: CombinedDestinationParameters
}

/**
 * Combines both Person destination parameters and zone destination parameters, if both are required to influence
 * a certain outcome.
 */
interface CombinedDestinationParameters : PersonDestinationParameters, ZoneDestinationParameters {
    fun evaluate(person: Person, zoneScope: ZoneScope): Double {
        return toParameters(person).toDouble() + toParameters(zoneScope).toDouble()
    }
}

/**
 * This interface adds a maximum score for attractiveness. Since Attractiveness is used in destination choice only, the
 * "Destination" is omitted from the interface name for conciseness.
 *
 * @property maxAttractiveness This parameter is an upper bound for ATTRACTIVENESS =  min(ATTR, [maxAttractiveness])
 */
interface AttractivenessParameters : CombinedDestinationParameters {
    val maxAttractiveness: Double
}
