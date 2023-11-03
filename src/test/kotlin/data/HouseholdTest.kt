package data

import utils.units.Currency
import utils.units.CurrencyUnits
import utils.units.toCurrency
import kotlin.test.Test
import kotlin.test.assertEquals

class HouseholdTest {
    @Test
    fun derivedPropertiesAreCalculated() {
        val hhImplementation = object: Household {
            override val residents: Collection<Person>
                get() = listOf(DefaultPerson(), DefaultPerson(), DefaultPerson(age=42))
            override val cars: Collection<Car>
                get() = listOf(GHOSTCAR)
            override val location: Location
                get() = NOWHERE
            override val incomePerMonth: Currency
                get() = 0.toCurrency(CurrencyUnits.EUROS)
            override val economicStatus: EconomicStatus
                get() = EconomicStatus.VERY_LOW

        }
        assertEquals(1, hhImplementation.numberOfCars)
        assertEquals(2, hhImplementation.numberOfMinors)
        assertEquals(3, hhImplementation.numberOfResidents)
    }
}
