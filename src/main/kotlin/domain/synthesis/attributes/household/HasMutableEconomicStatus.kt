package domain.synthesis.attributes.household

import domain.shared.enums.household.EconomicStatus

interface HasMutableEconomicStatus : HasEconomicStatus {
    override var economicStatus: EconomicStatus
}
