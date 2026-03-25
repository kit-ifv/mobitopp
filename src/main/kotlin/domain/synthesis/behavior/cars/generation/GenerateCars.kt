package domain.synthesis.behavior.cars.generation

import domain.synthesis.data.Car

fun interface GenerateCars<in S, in T> {
    fun generate(householdBuilder: domain.synthesis.behavior.MinimalistHousehold<S, T>): List<Car>
}


