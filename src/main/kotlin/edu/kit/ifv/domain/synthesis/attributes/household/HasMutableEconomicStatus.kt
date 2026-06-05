package edu.kit.ifv.domain.synthesis.attributes.household
import edu.kit.ifv.domain.shared.enums.household.EconomicStatus

interface HasMutableEconomicStatus : HasEconomicStatus {
    override var economicStatus: EconomicStatus
}
