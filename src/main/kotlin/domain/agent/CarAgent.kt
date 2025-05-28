package domain.agent

import Mutable
import domain.data.Car
import domain.data.CarId
import domain.data.IPrivateCar
import domain.location.Location

abstract class CarAgent : Car {
    // TODO Debate with Jelle whether CAR should hold information and state or be separated.
    abstract var location: Location
    var driver: PersonAgent? = null
    var passengers: MutableSet<PersonAgent> = mutableSetOf() // TODO person agent
    var keyHolder: PersonAgent? = null

    fun addDriver(person: PersonAgent) {
        driver = person
    }

    fun removeDriver() {
        driver = null
    }

    fun addPassenger(person: PersonAgent) {
        passengers.add(person)
    }

    fun removePassenger(person: PersonAgent) {
        passengers.remove(person)
    }
}

@Mutable
abstract class PrivateCarAgent(
    final override val id: CarId,
    override val owner: HouseholdAgent,
) : CarAgent(), IPrivateCar {

    abstract override val mainUser: PersonAgent?

    var state: CarState = CarState.PARKED
    override var location: Location = owner.location

    enum class CarState {
        PARKED, IN_USE
    }
}
