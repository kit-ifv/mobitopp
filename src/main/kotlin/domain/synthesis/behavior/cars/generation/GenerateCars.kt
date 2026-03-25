package domain.synthesis.behavior.cars.generation

import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.data.Car

fun interface GenerateCars<in S, in T> {
    fun generate(householdBuilder: MinimalistHousehold<S, T>): List<Car>
}
