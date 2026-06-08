package edu.kit.ifv.domain.synthesis.behavior.cars.generation
import edu.kit.ifv.domain.shared.car.Car
import edu.kit.ifv.domain.synthesis.behavior.MinimalistHousehold
import kotlin.random.Random

fun interface GenerateCars<in S, in T> {
    fun generate(householdBuilder: MinimalistHousehold<S, T>): List<Car>
}
