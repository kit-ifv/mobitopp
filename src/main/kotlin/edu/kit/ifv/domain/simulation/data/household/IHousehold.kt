package edu.kit.ifv.domain.simulation.data.household
import edu.kit.ifv.domain.shared.data.household.HouseholdBinaryRecord
import edu.kit.ifv.domain.shared.data.household.HouseholdId
import edu.kit.ifv.domain.shared.enums.household.EconomicStatus
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.data.car.IPrivateCar
import edu.kit.ifv.domain.simulation.data.person.IPerson
import edu.kit.ifv.domain.simulation.data.sharing.HasStandardLocation
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.utils.Identifiable
import edu.kit.ifv.utils.binary.Simplifiable
import edu.kit.ifv.utils.random.StochasticActor

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
