package application.steps

import core.modelsteps.Context
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

interface HasHouseholdRepo<H: Identifiable<HouseholdId>>: Context {
    var householdRepository: Repository<H, HouseholdId>

    fun getHousehold(householdId: HouseholdId) =
        requireNotNull(householdRepository[householdId]) {
            "Referenced household id $householdId could not be found in ${householdRepository.name}:" +
                    " ${householdRepository.elements.map { it.id }.toList()}"
        }
}

interface HasSharingProviderRepo<S: Identifiable<SharingProviderId>>: Context {
    var sharingProviderRepository: Repository<S, SharingProviderId>

    fun getSharingProvider(providerId: SharingProviderId) =
        requireNotNull(sharingProviderRepository[providerId]) {
            "Referenced sharing provider id $providerId could not be found in ${sharingProviderRepository.name}:" +
                    " ${sharingProviderRepository.elements.map { it.id }.toList()}"
        }
}

interface HasDrtProviderRepo<D: Identifiable<DrtProviderId>>: Context {
    var drtProviderRepository: Repository<D, DrtProviderId>

    fun getDrtProvider(providerId: DrtProviderId) =
        requireNotNull(drtProviderRepository[providerId]) {
            "Referenced drt provider id $providerId could not be found in ${drtProviderRepository.name}:" +
                    " ${drtProviderRepository.elements.map { it.id }.toList()}"
        }
}

interface HasPersonRepo<P: Identifiable<PersonId>>: Context {
    var personRepository: Repository<P, PersonId>

    fun getPerson(personId: PersonId) =
        requireNotNull(personRepository[personId]) {
            "Referenced person id $personId could not be found in ${personRepository.name}:" +
                    " ${personRepository.elements.map { it.id }.toList()}"
        }
}

interface HasZoneRepo<Z: Identifiable<ZoneId>>: Context {
    var zoneRepository: Repository<Z, ZoneId>

    fun getZone(zoneId: ZoneId) =
        requireNotNull(zoneRepository[zoneId]) {
            "Referenced zone id $zoneId could not be found in ${zoneRepository.name}:" +
                    " ${zoneRepository.elements.map { it.id }.toList()}"
        }
}

interface HasCarRepo<C: Identifiable<CarId>>: Context {
    var carRepo: Repository<C, CarId>

    fun getCar(carId: CarId) =
        requireNotNull(carRepo[carId]) {
            "Referenced car id $carId could not be found in ${carRepo.name}:" +
                    " ${carRepo.elements.map { it.id }.toList()}"
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