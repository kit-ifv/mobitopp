package edu.kit.ifv.domain.synthesis.attributes.household
import edu.kit.ifv.domain.shared.enums.household.HouseholdType
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.units.Currency

data class MinimumHouseholdAttributesImpl(
    override val income: Currency,
    override val type: HouseholdType,
    override var location: StandardLocation = StandardLocation.LOCATIONUNKNOWN,
) : MinimumHouseholdAttributes
