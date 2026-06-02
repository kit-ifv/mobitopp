package domain.synthesis.attributes.household

import domain.shared.location.StandardLocation
import domain.synthesis.data.household.EconomicStatus
import domain.synthesis.data.household.HouseholdType
import edu.kit.ifv.units.Currency

data class MaximumHouseholdAttributesImpl(
    override val income: Currency,
    override val type: HouseholdType,
    val householdSize: Int, // Useless attributes, but legacy mobiTopp produced these, so we take them into account.
    val year: Int,
    val areaTypeCode: Int,
    override var amountOfCars: Int,
    override var location: StandardLocation = StandardLocation.Companion.LOCATIONUNKNOWN,
    override var economicStatus: EconomicStatus = EconomicStatus.MIDDLE,
) : MaximumHouseholdAttributes
