package edu.kit.ifv.domain.simulation.data.person
import edu.kit.ifv.domain.shared.data.household.HouseholdId
import edu.kit.ifv.utils.Identifiable

interface HasHousehold<H : Identifiable<HouseholdId>> {
    val household: H
}
