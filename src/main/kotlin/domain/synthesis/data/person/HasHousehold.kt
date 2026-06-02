package domain.synthesis.data.person

import domain.synthesis.data.household.HouseholdId
import utils.Identifiable

interface HasHousehold<H : Identifiable<HouseholdId>> {
    val household: H
}