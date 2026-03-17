package domain.synthesis.attributes.household

import domain.shared.location.Location
import domain.synthesis.data.HouseholdType
import edu.kit.ifv.units.Currency

interface MinimumHouseholdAttributes: HasIncome, HasHouseholdType, HasLocation {
    override val income: Currency
    override val type: HouseholdType
    override val location: Location
}