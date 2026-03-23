package domain.synthesis.attributes.household

import domain.shared.location.LOCATIONUNKNOWN
import domain.shared.location.StandardLocation
import domain.synthesis.data.HouseholdType
import edu.kit.ifv.units.Currency

data class MinimumHouseholdAttributesImpl(
    override val income: Currency,
    override val type: HouseholdType,
    override var location: StandardLocation = LOCATIONUNKNOWN,
) : MinimumHouseholdAttributes