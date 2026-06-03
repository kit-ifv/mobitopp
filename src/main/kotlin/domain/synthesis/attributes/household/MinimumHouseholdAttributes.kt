package domain.synthesis.attributes.household

import domain.shared.enums.household.HouseholdType
import domain.shared.location.StandardLocation
import edu.kit.ifv.units.Currency

interface MinimumHouseholdAttributes :
    HasIncome,
    HasHouseholdType,
    HasMutableLocation {
    override val income: Currency
    override val type: HouseholdType
    override var location: StandardLocation
}
