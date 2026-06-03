package domain.synthesis.data.person

import utils.binary.Simplifiable
import domain.synthesis.data.drt.DrtProvider
import domain.synthesis.data.drt.DrtProviderId
import domain.synthesis.data.ISharingProvider
import domain.synthesis.data.SharingProviderId
import domain.synthesis.data.household.IHousehold
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.UnitIntervalValue
import utils.Identifiable
import utils.random.StochasticActor

@Suppress("ComplexInterface")
interface IPerson :
    Identifiable<PersonId>,
    StochasticActor,
    Simplifiable<PersonBinaryRecord>,
    HasHousehold<IHousehold> {
    override val household: IHousehold
    val age: Int
    val employment: Employment
    val sex: Sex
    val graduation: Graduation
    val income: Currency
    val hasBike: Boolean
    val hasCommuterTicket: Boolean
    val hasLicense: Boolean
    val sharingMemberships: List<ISharingProvider>
    val drtMemberships: List<DrtProvider>
    val eMobilityAcceptance: UnitIntervalValue
    val chargingInfluence: ChargingInfluence

    override fun simplify(): PersonBinaryRecord = PersonBinaryRecord(
        id.value,
        household.id.value,
        age,
        employment.code,
        sex.code,
        income.toDouble(CurrencyUnit.EUROS),
        hasBike,
        hasCommuterTicket,
        hasLicense,
        eMobilityAcceptance.toDouble(),
        chargingInfluence.code,
        graduation.code,
        sharingMemberships.map { it.id.value },
        drtMemberships.map { it.id.value },
    )
}

const val ADULT_AGE_GER = 18 // TODO we need a global constant location

val IPerson.sharingMembershipIds: Set<SharingProviderId>
    get() = sharingMemberships.map { it.id }.toSet()

val IPerson.drtMembershipIds: Set<DrtProviderId>
    get() = drtMemberships.map { it.id }.toSet()

val IPerson.isAdult: Boolean
    get() = (age >= ADULT_AGE_GER)
