package application.steps

import core.modelsteps.Context
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.statemachine.Agent
import domain.shared.behavior.AttractivenessModel
import domain.shared.datastructure.LocatableGraph
import domain.shared.enums.Mode
import domain.shared.location.Impedance
import domain.shared.location.zone.ZoneId
import domain.simulation.agent.DrtProviderMessage
import domain.simulation.agent.PersonMessage
import domain.simulation.events.PersonBehavior
import domain.simulation.data.DrtProviderId
import domain.simulation.data.SharingProviderId
import domain.simulation.data.car.CarId
import domain.simulation.data.household.HouseholdId
import domain.simulation.data.person.PersonId
import utils.CodePlan
import utils.Identifiable

private const val ERROR_OUTPUT_SIZE = 5 // TODO use in context requirements?

private fun reportList(elements: List<*>) = "Top $ERROR_OUTPUT_SIZE elements: ${elements.take(ERROR_OUTPUT_SIZE)}"

interface HasHouseholdRepo<M : H, out H : Identifiable<domain.simulation.data.household.HouseholdId>> : Context {
    val householdRepository: Repository<H, domain.simulation.data.household.HouseholdId>
        get() = mutableHouseholdRepository

    fun getHousehold(householdId: domain.simulation.data.household.HouseholdId) = requireNotNull(householdRepository[householdId]) {
        "Household referenced by id $householdId could not be found in ${householdRepository.name}: " +
            reportList(householdRepository.elements.map { it.id }.toList())
    }

    val mutableHouseholdRepository: MutableRepository<M, domain.simulation.data.household.HouseholdId>

    fun getMutableHousehold(householdId: domain.simulation.data.household.HouseholdId) = requireNotNull(mutableHouseholdRepository[householdId]) {
        "Mutable household referenced by id $householdId could not be found in ${mutableHouseholdRepository.name}: " +
            reportList(mutableHouseholdRepository.elements.map { it.id }.toList())
    }
}

interface HasSharingProviderRepo<M : S, out S : Identifiable<domain.simulation.data.SharingProviderId>> : Context {
    val sharingProviderRepository: Repository<S, domain.simulation.data.SharingProviderId>
        get() = mutableSharingProviderRepository

    fun getSharingProvider(providerId: domain.simulation.data.SharingProviderId) = requireNotNull(sharingProviderRepository[providerId]) {
        "Sharing provider referenced by id $providerId could not be found in ${sharingProviderRepository.name}: " +
            reportList(sharingProviderRepository.elements.map { it.id }.toList())
    }

    val mutableSharingProviderRepository: MutableRepository<M, domain.simulation.data.SharingProviderId>

    fun getMutableSharingProvider(providerId: domain.simulation.data.SharingProviderId) = requireNotNull(
        mutableSharingProviderRepository[providerId],
    ) {
        "Mutable sharing provider referenced by id $providerId " +
            "could not be found in ${mutableSharingProviderRepository.name}: " +
            reportList(mutableSharingProviderRepository.elements.map { it.id }.toList())
    }
}

interface HasDrtProviderRepo<M : D, out D : Identifiable<domain.simulation.data.DrtProviderId>> : Context {
    val drtProviderRepository: Repository<D, domain.simulation.data.DrtProviderId>
        get() = mutableDrtProviderRepository

    fun getDrtProvider(providerId: domain.simulation.data.DrtProviderId) = requireNotNull(drtProviderRepository[providerId]) {
        "Drt provider referenced by id $providerId could not be found in ${drtProviderRepository.name}: " +
            reportList(drtProviderRepository.elements.map { it.id }.toList())
    }

    val mutableDrtProviderRepository: MutableRepository<M, domain.simulation.data.DrtProviderId>

    fun getMutableDrtProvider(providerId: domain.simulation.data.DrtProviderId) = requireNotNull(mutableDrtProviderRepository[providerId]) {
        "Mutable Drt provider referenced by id $providerId " +
            "could not be found in ${mutableDrtProviderRepository.name}: " +
            reportList(mutableDrtProviderRepository.elements.map { it.id }.toList())
    }
}

interface HasPersonRepo<M : P, out P : Identifiable<domain.simulation.data.person.PersonId>> : Context {
    val personRepository: Repository<P, domain.simulation.data.person.PersonId>
        get() = mutablePersonRepository

    fun getPerson(personId: domain.simulation.data.person.PersonId) = requireNotNull(personRepository[personId]) {
        "Person referenced by id $personId could not be found in ${personRepository.name}: " +
            reportList(personRepository.elements.map { it.id }.toList())
    }

    val mutablePersonRepository: MutableRepository<M, domain.simulation.data.person.PersonId>

    fun getMutablePerson(personId: domain.simulation.data.person.PersonId) = requireNotNull(mutablePersonRepository[personId]) {
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

interface HasCarRepo<M : C, out C : Identifiable<domain.simulation.data.car.CarId>> : Context {
    val carRepository: Repository<C, domain.simulation.data.car.CarId>
        get() = mutableCarRepository

    fun getCar(carId: domain.simulation.data.car.CarId) = requireNotNull(carRepository[carId]) {
        "Car referenced by id $carId could not be found in ${carRepository.name}: " +
            reportList(carRepository.elements.map { it.id }.toList())
    }

    val mutableCarRepository: MutableRepository<M, domain.simulation.data.car.CarId>

    fun getMutableCar(carId: domain.simulation.data.car.CarId) = requireNotNull(mutableCarRepository[carId]) {
        "Mutable car referenced by id $carId could not be found in ${mutableCarRepository.name}: " +
            reportList(mutableCarRepository.elements.map { it.id }.toList())
    }
}

interface HasPersonAgentRepo<M : P, out P> : Context where P : Identifiable<domain.simulation.data.person.PersonId>, P : Agent<PersonMessage> {
    val personAgentRepository: Repository<P, domain.simulation.data.person.PersonId>
        get() = mutablePersonAgentRepository

    fun getPersonAgent(personId: domain.simulation.data.person.PersonId) = requireNotNull(personAgentRepository[personId]) {
        "Person agent referenced by id $personId could not be found in ${personAgentRepository.name}: " +
            reportList(personAgentRepository.elements.map { it.id }.toList())
    }

    val mutablePersonAgentRepository: MutableRepository<M, domain.simulation.data.person.PersonId>

    fun getMutablePersonAgent(personId: domain.simulation.data.person.PersonId) = requireNotNull(mutablePersonAgentRepository[personId]) {
        "Mutable person agent referenced by id $personId could not be found in ${mutablePersonAgentRepository.name}: " +
            reportList(mutablePersonAgentRepository.elements.map { it.id }.toList())
    }
}

// TODO SharingProividerAgent does not implement Agent interface
interface HasSharingProviderAgentRepo<M : S, out S> : Context where S : Identifiable<domain.simulation.data.SharingProviderId> {
    val sharingProviderAgentRepository: Repository<S, domain.simulation.data.SharingProviderId>
        get() = mutableSharingProviderAgentRepository

    fun getSharingProviderAgent(providerId: domain.simulation.data.SharingProviderId) = requireNotNull(
        sharingProviderAgentRepository[providerId],
    ) {
        "Sharing provider agent referenced  by id $providerId " +
            "could not be found in ${sharingProviderAgentRepository.name}: " +
            reportList(sharingProviderAgentRepository.elements.map { it.id }.toList())
    }

    val mutableSharingProviderAgentRepository: MutableRepository<M, domain.simulation.data.SharingProviderId>

    fun getMutableSharingProviderAgent(providerId: domain.simulation.data.SharingProviderId) = requireNotNull(
        sharingProviderAgentRepository[providerId],
    ) {
        "Mutable sharing provider agent referenced  by id $providerId " +
            "could not be found in ${sharingProviderAgentRepository.name}: " +
            reportList(sharingProviderAgentRepository.elements.map { it.id }.toList())
    }
}

interface HasDrtProviderAgentRepo<M : D, out D> :
    Context where D : Identifiable<domain.simulation.data.DrtProviderId>, D : Agent<DrtProviderMessage> {
    val drtProviderAgentRepository: Repository<D, domain.simulation.data.DrtProviderId>
        get() = mutableDrtProviderAgentRepository

    fun getDrtProviderAgent(providerId: domain.simulation.data.DrtProviderId) = requireNotNull(drtProviderAgentRepository[providerId]) {
        "Drt provider agent referenced  by id $providerId could not be found in ${drtProviderAgentRepository.name}: " +
            reportList(drtProviderAgentRepository.elements.map { it.id }.toList())
    }

    val mutableDrtProviderAgentRepository: MutableRepository<M, domain.simulation.data.DrtProviderId>

    fun getMutableDrtProviderAgent(providerId: domain.simulation.data.DrtProviderId) = requireNotNull(drtProviderAgentRepository[providerId]) {
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
