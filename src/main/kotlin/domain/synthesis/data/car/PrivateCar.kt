package domain.synthesis.data.car

import Mutable
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.person.Person

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