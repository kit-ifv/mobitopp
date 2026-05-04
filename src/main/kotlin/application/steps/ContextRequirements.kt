package application.steps

import core.modelsteps.Context
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import domain.shared.datastructure.LocatableGraph
import domain.shared.enums.Mode
import domain.shared.location.Impedance
import domain.shared.location.ZoneId
import domain.synthesis.data.CarId
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.PersonId
import domain.synthesis.data.SharingProviderId
import utils.CodePlan
import utils.Identifiable

interface HasHouseholdRepo<M: H, H: Identifiable<HouseholdId>>: Context {
    val householdRepository: Repository<H, HouseholdId>
        get() = mutableHouseholdRepository

    fun getHousehold(householdId: HouseholdId) =
        requireNotNull(householdRepository[householdId]) {
            "Referenced household id $householdId could not be found in ${householdRepository.name}:" +
                    " ${householdRepository.elements.map { it.id }.toList()}"
        }

    val mutableHouseholdRepository: MutableRepository<M, HouseholdId>

    fun getMutableHousehold(householdId: HouseholdId) =
        requireNotNull(mutableHouseholdRepository[householdId]) {
            "Referenced household id $householdId could not be found in ${mutableHouseholdRepository.name}:" +
                    " ${mutableHouseholdRepository.elements.map { it.id }.toList()}"
        }
}

interface  HasSharingProviderRepo<M: S, S: Identifiable<SharingProviderId>>: Context {
    val sharingProviderRepository: Repository<S, SharingProviderId>
        get() = mutableSharingProviderRepository

    fun getSharingProvider(providerId: SharingProviderId) =
        requireNotNull(sharingProviderRepository[providerId]) {
            "Referenced sharing provider id $providerId could not be found in ${sharingProviderRepository.name}:" +
                    " ${sharingProviderRepository.elements.map { it.id }.toList()}"
        }

    val mutableSharingProviderRepository: MutableRepository<M, SharingProviderId>

    fun getMutableSharingProvider(providerId: SharingProviderId) =
        requireNotNull(mutableSharingProviderRepository[providerId]) {
            "Referenced sharing provider id $providerId could not be found in ${mutableSharingProviderRepository.name}:" +
                    " ${mutableSharingProviderRepository.elements.map { it.id }.toList()}"
        }
}


interface HasDrtProviderRepo<M: D, D: Identifiable<DrtProviderId>>: Context {
    val drtProviderRepository: Repository<D, DrtProviderId>
        get() = mutableDrtProviderRepository

    fun getDrtProvider(providerId: DrtProviderId) =
        requireNotNull(drtProviderRepository[providerId]) {
            "Referenced drt provider id $providerId could not be found in ${drtProviderRepository.name}:" +
                    " ${drtProviderRepository.elements.map { it.id }.toList()}"
        }

    val mutableDrtProviderRepository: MutableRepository<M, DrtProviderId>

    fun getMutableDrtProvider(providerId: DrtProviderId) =
        requireNotNull(mutableDrtProviderRepository[providerId]) {
            "Referenced drt provider id $providerId could not be found in ${mutableDrtProviderRepository.name}:" +
                    " ${mutableDrtProviderRepository.elements.map { it.id }.toList()}"
        }
}




interface HasPersonRepo<M: P, P: Identifiable<PersonId>>: Context {
    val personRepository: Repository<P, PersonId>
        get() = mutablePersonRepository

    fun getPerson(personId: PersonId) =
        requireNotNull(personRepository[personId]) {
            "Referenced person id $personId could not be found in ${personRepository.name}:" +
                    " ${personRepository.elements.map { it.id }.toList()}"
        }

    val mutablePersonRepository: MutableRepository<M, PersonId>

    fun getMutablePerson(personId: PersonId) =
        requireNotNull(mutablePersonRepository[personId]) {
            "Referenced person id $personId could not be found in ${mutablePersonRepository.name}:" +
                    " ${mutablePersonRepository.elements.map { it.id }.toList()}"
        }
}



interface HasZoneRepo<M: Z, Z: Identifiable<ZoneId>>: Context {
    val zoneRepository: Repository<Z, ZoneId>
        get() = mutableZoneRepository

    fun getZone(zoneId: ZoneId) =
        requireNotNull(zoneRepository[zoneId]) {
            "Referenced zone id $zoneId could not be found in ${zoneRepository.name}:" +
                    " ${zoneRepository.elements.map { it.id }.toList()}"
        }

    val mutableZoneRepository: MutableRepository<M, ZoneId>

    fun getMutableZone(zoneId: ZoneId) =
        requireNotNull(mutableZoneRepository[zoneId]) {
            "Referenced zone id $zoneId could not be found in ${mutableZoneRepository.name}:" +
                    " ${mutableZoneRepository.elements.map { it.id }.toList()}"
        }
}

interface HasCarRepo<M: C, C: Identifiable<CarId>>: Context {
    val carRepo: Repository<C, CarId>
        get() = mutableCarRepository

    fun getCar(carId: CarId) =
        requireNotNull(carRepo[carId]) {
            "Referenced car id $carId could not be found in ${carRepo.name}:" +
                    " ${carRepo.elements.map { it.id }.toList()}"
        }

    val mutableCarRepository: MutableRepository<M, CarId>

    fun getMutableCar(carId: CarId) =
        requireNotNull(mutableCarRepository[carId]) {
            "Referenced car id $carId could not be found in ${mutableCarRepository.name}:" +
                    " ${mutableCarRepository.elements.map { it.id }.toList()}"
        }
}



//TODO find/create package for common context requirements
interface HasMutableImpedance: Context {
    var impedance: Impedance
}

interface HasModes: Context { //TODO modes are very common, maybe move to base context?
    val modes: CodePlan<Mode>
}

interface HasMutableRoadNetwork : Context {
    var roadNetwork: LocatableGraph
}