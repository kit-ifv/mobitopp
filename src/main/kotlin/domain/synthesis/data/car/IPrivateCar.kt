package domain.synthesis.data.car

import domain.synthesis.data.car.CarBinaryRecord
import domain.synthesis.data.household.IHousehold
import domain.synthesis.data.person.IPerson
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
