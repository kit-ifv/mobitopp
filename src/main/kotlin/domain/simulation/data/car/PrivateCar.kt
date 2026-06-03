package domain.simulation.data.car

import Mutable
import domain.shared.car.CarId
import domain.shared.car.IPrivateCar
import domain.simulation.data.household.MutableHousehold
import domain.simulation.data.person.Person

/**
 * A vehicle that is assigned to a specific household or user
 */
@Mutable
abstract class PrivateCar(final override val id: CarId, override val owner: MutableHousehold) : IPrivateCar {

    abstract override val mainUser: Person?

    init {
        registerCarOwner()
    }

    private fun registerCarOwner() {
        owner.cars.add(this)
    }
}
