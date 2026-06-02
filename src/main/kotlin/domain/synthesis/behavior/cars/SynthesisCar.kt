package domain.synthesis.behavior.cars

import domain.synthesis.SynthesisPerson
import domain.synthesis.data.car.Car

class SynthesisCar constructor(private val car: Car, val mainUser: SynthesisPerson<*, *>? = null) : Car by car
