package edu.kit.ifv.domain.synthesis.behavior.cars.amount
import edu.kit.ifv.domain.synthesis.behavior.MinimalistHousehold
import kotlin.random.Random

class AlwaysAssignFixedNumber(val amount: Int) : NumberOfCarDeterminer<Any?, Any?> {
    context(random: Random)
    override fun determineNumberOfCars(householdBuilder: MinimalistHousehold<*, *>): Int = amount
}
