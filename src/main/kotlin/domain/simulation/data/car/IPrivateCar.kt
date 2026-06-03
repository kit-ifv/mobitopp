package domain.simulation.data.car

import domain.jackson.CarBinaryRecord
import domain.jackson.Simplifiable
import domain.simulation.data.household.IHousehold
import domain.simulation.data.person.IPerson

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
