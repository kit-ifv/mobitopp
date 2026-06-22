package edu.kit.ifv.domain.synthesis.behavior.cars
import edu.kit.ifv.domain.shared.car.Car
import edu.kit.ifv.domain.synthesis.SynthesisPerson

class SynthesisCar constructor(private val car: Car, val mainUser: SynthesisPerson<*, *>? = null) : Car by car
