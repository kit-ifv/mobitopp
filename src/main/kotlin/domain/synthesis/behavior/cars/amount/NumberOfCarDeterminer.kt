package domain.synthesis.behavior.cars.amount

import AssignmentStep
import domain.synthesis.behavior.MinimalistHousehold
import kotlin.random.Random

@Suppress("SpacingAroundColon") // Seems to be a detekt version thing
fun interface NumberOfCarDeterminer<in S, in T> : AssignmentStep<MinimalistHousehold<S, T>, Int> {

    fun determineNumberOfCars(householdBuilder: MinimalistHousehold<S, T>): Int

    context(random: Random)
    override fun assign(input: MinimalistHousehold<S, T>): Int = determineNumberOfCars(input)
}
