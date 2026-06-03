package domain.synthesis.attributes.household

import domain.shared.enums.household.HouseholdType
import domain.shared.location.StandardLocation
import edu.kit.ifv.units.Currency

data class MinimumHouseholdAttributesImpl(
    override val income: Currency,
    override val type: HouseholdType,
    override var location: StandardLocation = StandardLocation.LOCATIONUNKNOWN,
) : MinimumHouseholdAttributes
