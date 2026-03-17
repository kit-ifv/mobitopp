package domain.synthesis.attributes.household

import domain.synthesis.data.HouseholdType
import edu.kit.ifv.units.Currency

data class MinimumHouseholdAttributesImpl(
    override val income: Currency,
    override val type: HouseholdType,
): MinimumHouseholdAttributes