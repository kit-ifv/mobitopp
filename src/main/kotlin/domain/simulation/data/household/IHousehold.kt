package domain.simulation.data.household

import domain.shared.car.IPrivateCar
import domain.shared.enums.household.EconomicStatus
import domain.shared.location.StandardLocation
import domain.simulation.data.person.IPerson
import domain.simulation.data.sharing.HasStandardLocation
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.CurrencyUnit
import utils.Identifiable
import utils.binary.Simplifiable
import utils.random.StochasticActor

interface IHousehold :
    Identifiable<HouseholdId>,
    StochasticActor,
    Simplifiable<HouseholdBinaryRecord>,
    HasStandardLocation {
    val householdNumber: Long
    val surveyYear: Int
    override val location: StandardLocation
    val domCode: Int
    val type: Int
    val incomePerMonth: Currency
    val economicStatus: EconomicStatus
    val members: Set<IPerson>
    val cars: Set<IPrivateCar>

    override fun simplify(): HouseholdBinaryRecord = HouseholdBinaryRecord(
        id.value,
        householdNumber,
        surveyYear,
        domCode,
        type,
        incomePerMonth.toDouble(CurrencyUnit.EUROS),
        economicStatus.code,
        location.toRecord(),

    )
}
