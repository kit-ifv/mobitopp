package domain.synthesis.behavior.cars.generation

import domain.simulation.data.car.Car
import domain.synthesis.behavior.MinimalistHousehold

fun interface GenerateCars<in S, in T> {
    fun generate(householdBuilder: MinimalistHousehold<S, T>): List<Car>
}
