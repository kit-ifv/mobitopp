package edu.kit.ifv.domain.simulation.data.person
import edu.kit.ifv.binary.Simplifiable
import edu.kit.ifv.domain.shared.data.person.PersonBinaryRecord
import edu.kit.ifv.domain.shared.data.person.PersonId
import edu.kit.ifv.domain.shared.enums.person.ChargingInfluence
import edu.kit.ifv.domain.shared.enums.person.Employment
import edu.kit.ifv.domain.shared.enums.person.Graduation
import edu.kit.ifv.domain.shared.enums.person.Sex
import edu.kit.ifv.domain.simulation.data.drt.DrtProvider
import edu.kit.ifv.domain.simulation.data.drt.DrtProviderId
import edu.kit.ifv.domain.simulation.data.household.IHousehold
import edu.kit.ifv.domain.simulation.data.sharing.ISharingProvider
import edu.kit.ifv.domain.simulation.data.sharing.SharingProviderId
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.utils.Identifiable
import edu.kit.ifv.utils.random.StochasticActor

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
