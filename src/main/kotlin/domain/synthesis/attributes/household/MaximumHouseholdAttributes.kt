package domain.synthesis.attributes.household

import domain.shared.location.LOCATIONUNKNOWN
import domain.shared.location.StandardLocation
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.HouseholdType
import edu.kit.ifv.units.Currency

data class MaximumHouseholdAttributes(
    override val income: Currency,
    override val type: HouseholdType,
    val householdSize: Int, // Useless attributes, but legacy mobiTopp produced these, so we take them into account.
    val year: Int,
    val areaTypeCode: Int,
    override var amountOfCars: Int,
    override var location: StandardLocation = LOCATIONUNKNOWN,
    override var economicStatus: EconomicStatus = EconomicStatus.MIDDLE,
): MinimumHouseholdAttributes, HasNumberOfCars, HasMutableEconomicStatus