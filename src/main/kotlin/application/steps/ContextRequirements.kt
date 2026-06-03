package application.steps

import core.modelsteps.Context
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.statemachine.Agent
import domain.shared.behavior.AttractivenessModel
import domain.shared.car.CarId
import domain.shared.datastructure.LocatableGraph
import domain.shared.enums.Mode
import domain.shared.location.Impedance
import domain.shared.location.zone.ZoneId
import domain.simulation.agent.DrtProviderMessage
import domain.simulation.agent.PersonMessage
import domain.simulation.data.DrtProviderId
import domain.simulation.data.SharingProviderId
import domain.simulation.data.household.HouseholdId
import domain.simulation.data.person.PersonId
import domain.simulation.events.PersonBehavior
import domain.synthesis.data.drt.DrtProviderId
import domain.synthesis.data.SharingProviderId
import domain.synthesis.data.car.CarId
import domain.synthesis.data.household.HouseholdId
import domain.synthesis.data.person.PersonId
import utils.CodePlan
import utils.Identifiable

private const val ERROR_OUTPUT_SIZE = 5 // TODO use in context requirements?

private fun reportList(elements: List<*>) = "Top $ERROR_OUTPUT_SIZE elements: ${elements.take(ERROR_OUTPUT_SIZE)}"

interface HasHouseholdRepo<M : H, out H : Identifiable<HouseholdId>> : Context {
    val householdRepository: Repository<H, HouseholdId>
        get() = mutableHouseholdRepository

    fun getHousehold(householdId: HouseholdId) = requireNotNull(
        householdRepository[householdId],
    ) {
        "Household referenced by id $householdId could not be found in ${householdRepository.name}: " +
            reportList(householdRepository.elements.map { it.id }.toList())
    }

    val mutableHouseholdRepository: MutableRepository<M, HouseholdId>

    fun getMutableHousehold(householdId: HouseholdId) = requireNotNull(
        mutableHouseholdRepository[householdId],
    ) {
        "Mutable household referenced by id $householdId could not be found in ${mutableHouseholdRepository.name}: " +
            reportList(mutableHouseholdRepository.elements.map { it.id }.toList())
    }
}

interface HasSharingProviderRepo<M : S, out S : Identifiable<SharingProviderId>> : Context {
    val sharingProviderRepository: Repository<S, SharingProviderId>
        get() = mutableSharingProviderRepository

    fun getSharingProvider(providerId: SharingProviderId) = requireNotNull(
        sharingProviderRepository[providerId],
    ) {
        "Sharing provider referenced by id $providerId could not be found in ${sharingProviderRepository.name}: " +
            reportList(sharingProviderRepository.elements.map { it.id }.toList())
    }

    val mutableSharingProviderRepository: MutableRepository<M, SharingProviderId>

    fun getMutableSharingProvider(providerId: SharingProviderId) = requireNotNull(
        mutableSharingProviderRepository[providerId],
    ) {
        "Mutable sharing provider referenced by id $providerId " +
            "could not be found in ${mutableSharingProviderRepository.name}: " +
            reportList(mutableSharingProviderRepository.elements.map { it.id }.toList())
    }
}

interface HasDrtProviderRepo<M : D, out D : Identifiable<DrtProviderId>> : Context {
    val drtProviderRepository: Repository<D, DrtProviderId>
        get() = mutableDrtProviderRepository

    fun getDrtProvider(providerId: DrtProviderId) = requireNotNull(
        drtProviderRepository[providerId],
    ) {
        "Drt provider referenced by id $providerId could not be found in ${drtProviderRepository.name}: " +
            reportList(drtProviderRepository.elements.map { it.id }.toList())
    }

    val mutableDrtProviderRepository: MutableRepository<M, DrtProviderId>

    fun getMutableDrtProvider(providerId: DrtProviderId) = requireNotNull(
        mutableDrtProviderRepository[providerId],
    ) {
        "Mutable Drt provider referenced by id $providerId " +
            "could not be found in ${mutableDrtProviderRepository.name}: " +
            reportList(mutableDrtProviderRepository.elements.map { it.id }.toList())
    }
}

interface HasPersonRepo<M : P, out P : Identifiable<PersonId>> : Context {
    val personRepository: Repository<P, PersonId>
        get() = mutablePersonRepository

    fun getPerson(personId: PersonId) = requireNotNull(personRepository[personId]) {
        "Person referenced by id $personId could not be found in ${personRepository.name}: " +
            reportList(personRepository.elements.map { it.id }.toList())
    }

    val mutablePersonRepository: MutableRepository<M, PersonId>

    fun getMutablePerson(personId: PersonId) = requireNotNull(
        mutablePersonRepository[personId],
    ) {
        "Mutable person referenced by id $personId could not be found in ${mutablePersonRepository.name}: " +
            reportList(mutablePersonRepository.elements.map { it.id }.toList())
    }
}

interface HasZoneRepo<M : Z, out Z : Identifiable<ZoneId>> : Context {
    val zoneRepository: Repository<Z, ZoneId>
        get() = mutableZoneRepository

    fun getZone(zoneId: ZoneId) = requireNotNull(zoneRepository[zoneId]) {
        "Zone referenced id $zoneId could not be found in ${zoneRepository.name}: " +
            reportList(zoneRepository.elements.map { it.id }.toList())
    }

    val mutableZoneRepository: MutableRepository<M, ZoneId>

    fun getMutableZone(zoneId: ZoneId) = requireNotNull(mutableZoneRepository[zoneId]) {
        "Mutable zone referenced id $zoneId could not be found in ${mutableZoneRepository.name}: " +
            reportList(mutableZoneRepository.elements.map { it.id }.toList())
    }
}

interface HasCarRepo<M : C, out C : Identifiable<CarId>> : Context {
    val carRepository: Repository<C, CarId>
        get() = mutableCarRepository

    fun getCar(carId: CarId) = requireNotNull(carRepository[carId]) {
        "Car referenced by id $carId could not be found in ${carRepository.name}: " +
            reportList(carRepository.elements.map { it.id }.toList())
    }

    val mutableCarRepository: MutableRepository<M, CarId>

    fun getMutableCar(carId: CarId) = requireNotNull(mutableCarRepository[carId]) {
        "Mutable car referenced by id $carId could not be found in ${mutableCarRepository.name}: " +
            reportList(mutableCarRepository.elements.map { it.id }.toList())
    }
}

interface HasPersonAgentRepo<M : P, out P> : Context where P : Identifiable<PersonId>, P : Agent<PersonMessage> {
    val personAgentRepository: Repository<P, PersonId>
        get() = mutablePersonAgentRepository

    fun getPersonAgent(personId: PersonId) = requireNotNull(
        personAgentRepository[personId],
    ) {
        "Person agent referenced by id $personId could not be found in ${personAgentRepository.name}: " +
            reportList(personAgentRepository.elements.map { it.id }.toList())
    }

    val mutablePersonAgentRepository: MutableRepository<M, PersonId>

    fun getMutablePersonAgent(personId: PersonId) = requireNotNull(
        mutablePersonAgentRepository[personId],
    ) {
        "Mutable person agent referenced by id $personId could not be found in ${mutablePersonAgentRepository.name}: " +
            reportList(mutablePersonAgentRepository.elements.map { it.id }.toList())
    }
}

// TODO SharingProividerAgent does not implement Agent interface
interface HasSharingProviderAgentRepo<M : S, out S> : Context where S : Identifiable<SharingProviderId> {
    val sharingProviderAgentRepository: Repository<S, SharingProviderId>
        get() = mutableSharingProviderAgentRepository

    fun getSharingProviderAgent(providerId: SharingProviderId) = requireNotNull(
        sharingProviderAgentRepository[providerId],
    ) {
        "Sharing provider agent referenced  by id $providerId " +
            "could not be found in ${sharingProviderAgentRepository.name}: " +
            reportList(sharingProviderAgentRepository.elements.map { it.id }.toList())
    }

    val mutableSharingProviderAgentRepository: MutableRepository<M, SharingProviderId>

    fun getMutableSharingProviderAgent(providerId: SharingProviderId) = requireNotNull(
        sharingProviderAgentRepository[providerId],
    ) {
        "Mutable sharing provider agent referenced  by id $providerId " +
            "could not be found in ${sharingProviderAgentRepository.name}: " +
            reportList(sharingProviderAgentRepository.elements.map { it.id }.toList())
    }
}

interface HasDrtProviderAgentRepo<M : D, out D> :
    Context where D : Identifiable<DrtProviderId>, D : Agent<DrtProviderMessage> {
    val drtProviderAgentRepository: Repository<D, DrtProviderId>
        get() = mutableDrtProviderAgentRepository

    fun getDrtProviderAgent(providerId: DrtProviderId) = requireNotNull(
        drtProviderAgentRepository[providerId],
    ) {
        "Drt provider agent referenced  by id $providerId could not be found in ${drtProviderAgentRepository.name}: " +
            reportList(drtProviderAgentRepository.elements.map { it.id }.toList())
    }

    val mutableDrtProviderAgentRepository: MutableRepository<M, DrtProviderId>

    fun getMutableDrtProviderAgent(providerId: DrtProviderId) = requireNotNull(
        drtProviderAgentRepository[providerId],
    ) {
        "Mutable drt provider agent referenced  by id $providerId " +
            "could not be found in ${drtProviderAgentRepository.name}: " +
            reportList(drtProviderAgentRepository.elements.map { it.id }.toList())
    }
}

interface HasImpedance : Context {
    val impedance: Impedance
}

interface HasMutableImpedance :
    Context,
    HasImpedance {
    override var impedance: Impedance
}

interface HasModes : Context { // TODO modes are very common, maybe move to base context?
    val modes: CodePlan<Mode>
}

interface HasMutableRoadNetwork : Context {
    var roadNetwork: LocatableGraph
}

interface HasAttractivenessModel : Context {
    val attractiveness: AttractivenessModel
}

interface HasMutableAttractivenessModel : HasAttractivenessModel {
    override var attractiveness: AttractivenessModel
}

interface HasPersonBehavior : Context {
    val personBehavior: PersonBehavior
}

interface HasMutablePersonBehavior :
    Context,
    HasPersonBehavior {
    override var personBehavior: PersonBehavior
}
