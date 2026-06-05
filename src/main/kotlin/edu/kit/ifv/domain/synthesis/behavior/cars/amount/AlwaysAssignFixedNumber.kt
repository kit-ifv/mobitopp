package edu.kit.ifv.domain.synthesis.behavior.cars.amount
import edu.kit.ifv.domain.synthesis.behavior.MinimalistHousehold

class AlwaysAssignFixedNumber(val amount: Int) : NumberOfCarDeterminer<Any?, Any?> {

    override fun determineNumberOfCars(householdBuilder: MinimalistHousehold<*, *>): Int = amount
}
