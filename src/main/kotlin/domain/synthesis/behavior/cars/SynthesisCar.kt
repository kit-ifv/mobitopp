package domain.synthesis.behavior.cars

import domain.shared.car.Car
import domain.synthesis.SynthesisPerson

class SynthesisCar constructor(private val car: Car, val mainUser: SynthesisPerson<*, *>? = null) : Car by car
