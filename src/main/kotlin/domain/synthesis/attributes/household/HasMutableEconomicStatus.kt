package domain.synthesis.attributes.household

import domain.synthesis.data.EconomicStatus

interface HasMutableEconomicStatus: HasEconomicStatus {
    override var economicStatus: EconomicStatus
}