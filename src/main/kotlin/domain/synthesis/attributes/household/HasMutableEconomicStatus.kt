package domain.synthesis.attributes.household

import domain.synthesis.data.household.EconomicStatus

interface HasMutableEconomicStatus : HasEconomicStatus {
    override var economicStatus: EconomicStatus
}
