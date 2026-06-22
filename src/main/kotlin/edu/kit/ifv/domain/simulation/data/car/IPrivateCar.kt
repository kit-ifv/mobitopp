package edu.kit.ifv.domain.simulation.data.car
import edu.kit.ifv.domain.shared.car.Car
import edu.kit.ifv.domain.shared.car.CarBinaryRecord
import edu.kit.ifv.domain.simulation.data.household.IHousehold
import edu.kit.ifv.domain.simulation.data.person.IPerson
import edu.kit.ifv.utils.binary.Simplifiable

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
