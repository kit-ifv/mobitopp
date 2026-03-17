package domain.synthesis.attributes.household

import domain.shared.location.LOCATIONUNKNOWN
import domain.shared.location.Location
import domain.synthesis.data.HouseholdType
import edu.kit.ifv.units.Currency

data class MaximumHouseholdAttributes(
    override val income: Currency,
    override val type: HouseholdType,
    val householdSize: Int, // Useless attributes, but legacy mobiTopp produced these, so we take them into account.
    val year: Int,
    val areaTypeCode: Int,
    val numCars: Int,
    override var location: Location = LOCATIONUNKNOWN
): MinimumHouseholdAttributes