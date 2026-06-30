package edu.kit.ifv.domain.synthesis.behavior.cars.amount
import edu.kit.ifv.domain.synthesis.AssignmentStep
import edu.kit.ifv.domain.synthesis.behavior.MinimalistHousehold
import kotlin.random.Random

@Suppress("SpacingAroundColon") // Seems to be a detekt version thing
fun interface NumberOfCarDeterminer<in S, in T> : AssignmentStep<MinimalistHousehold<S, T>, Int> {
    context(random: Random)
    fun determineNumberOfCars(householdBuilder: MinimalistHousehold<S, T>): Int

    context(random: Random)
    override fun assign(input: MinimalistHousehold<S, T>): Int = determineNumberOfCars(input)
}
