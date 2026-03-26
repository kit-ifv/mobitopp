package domain.synthesis.behavior.cars.amount

import domain.synthesis.behavior.MinimalistHousehold

class AlwaysAssignFixedNumber(val amount: Int) : NumberOfCarDeterminer<Any?, Any?> {

    override fun determineNumberOfCars(householdBuilder: MinimalistHousehold<*, *>): Int {
        return amount
    }
}
