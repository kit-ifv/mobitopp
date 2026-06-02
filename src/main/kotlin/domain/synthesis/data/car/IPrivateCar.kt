package domain.synthesis.data.car

import domain.jackson.CarBinaryRecord
import domain.jackson.Simplifiable
import domain.synthesis.data.household.IHousehold
import domain.synthesis.data.person.IPerson

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