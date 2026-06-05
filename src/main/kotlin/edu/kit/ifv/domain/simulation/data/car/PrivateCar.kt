package edu.kit.ifv.domain.simulation.data.car
import edu.kit.ifv.Mutable
import edu.kit.ifv.domain.shared.car.CarId
import edu.kit.ifv.domain.simulation.data.household.MutableHousehold
import edu.kit.ifv.domain.simulation.data.person.Person

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
