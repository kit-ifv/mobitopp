package domain.shared.car

import domain.simulation.data.household.IHousehold
import domain.simulation.data.person.IPerson
import utils.binary.Simplifiable

interface IPrivateCar :
    Car,
    Simplifiable<CarBinaryRecord> {
    val owner: IHousehold
    val mainUser: IPerson?

    override fun simplify(): CarBinaryRecord = CarBinaryRecord(
        id.value,
        owner.id.value,
        seats,
        mainUser?.id?.value ?: Long.MIN_VALUE,
        segment.code,
        engine.type.code,
    )
}
