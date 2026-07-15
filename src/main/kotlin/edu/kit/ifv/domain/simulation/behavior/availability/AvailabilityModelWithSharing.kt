package edu.kit.ifv.domain.simulation.behavior.availability
//
// import edu.kit.ifv.domain.shared.behavior.ChoiceModelModes
// import edu.kit.ifv.domain.shared.enums.Mode
// import edu.kit.ifv.domain.shared.location.Impedance
// import edu.kit.ifv.domain.shared.location.StandardLocation
// import edu.kit.ifv.domain.simulation.agent.DrtOffer
// import edu.kit.ifv.domain.simulation.agent.DrtProviderAgent
// import edu.kit.ifv.domain.simulation.agent.DrtRequest
// import edu.kit.ifv.domain.simulation.agent.PersonAgent
// import edu.kit.ifv.domain.simulation.agent.SharingStationAgent
// import edu.kit.ifv.domain.simulation.agent.getBestCarOrNull
// import edu.kit.ifv.domain.simulation.agent.lastTransportMode
// import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceCharacteristics
// import edu.kit.ifv.domain.simulation.data.drt.DrtProviderId
// import edu.kit.ifv.domain.simulation.data.person.IPerson
// import edu.kit.ifv.domain.simulation.data.person.drtMembershipIds
// import edu.kit.ifv.domain.simulation.data.person.sharingMembershipIds
// import edu.kit.ifv.domain.simulation.data.sharing.SharingProviderId
// import edu.kit.ifv.utils.units.AbsoluteTime
//
// @Suppress("TooManyFunctions")
// class AvailabilityModelWithSharing(
//    val modes: ChoiceModelModes,
//    private val sharingProvidersByMode: Map<Mode, Set<SharingProviderId>>,
//    private val drtProvidersByMode: Map<Mode, Set<DrtProviderId>>,
// ) : ModeAvailabilityModel,
//    BikeSharingConnectionSelector,
//    DrtAvailabilitySelector {
//
//    context(person: IPerson)
//    override fun staticAvailability(mode: Mode): Boolean = when (mode) {
//        modes.car -> hasCarStatic(person)
//        modes.bike -> hasBikeStatic(person)
//        modes.carSharingStation -> hasCssbStatic(person)
//        modes.carSharingFree -> hasCsffStatic(person)
//        modes.ridePooling -> hasPoolingStatic(person)
//        modes.bikeSharing -> hasBikeSharingStatic(person)
//        else -> mode in modes.options
//    }
//
//    context(person: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
//    override fun providerAvailability(mode: Mode): ProviderAvailability = takeIf {
//        staticAvailability(mode) // TODO use cached static availability of agent
//    }?.let {
//        when (mode) {
//            modes.car -> isCarCurrentlyAvailable()
//
//            modes.bikeSharing -> isBikeSharingCurrentlyAvailable()
//
//            modes.ridePooling -> isPoolingCurrentlyAvailable()
//
//            else -> when {
//                mode.requiresVehicleTakeAlong -> mode.isFixedModeCurrentlyAvailable(person)
//                else -> mode.isFlexModeCurrentlyAvailableI(person)
//            }
//        }
//    } ?: mode.notAvailable
//
//    context(characteristics: ModeChoiceCharacteristics, impedance: Impedance)
//    override fun resourceAvailability(mode: Mode): ModeResource? = NoResourceMode(mode)
// //        context(characteristics.person, characteristics.time, characteristics.destination) {
// //            mode in characteristics.currentChoices // cached choice set before mode choice / lock
// //        } && when (mode) {
// //            modes.car -> isPrivateCarAvailableForChoice(characteristics)
// //            modes.bikeSharing -> isBikesharingAvailableForChoice(characteristics)
// //            modes.ridePooling -> isPoolingAvailableForChoice(characteristics)
// //            else -> true
// //        }
//
//    // Static availability
//    private fun hasCarStatic(person: IPerson) = person.hasLicense && person.household.cars.isNotEmpty()
//
//    private fun hasBikeStatic(person: IPerson) = person.hasBike
//
//    private fun hasCssbStatic(person: IPerson) = person.hasLicense &&
//        sharingProvidersByMode[modes.carSharingStation]?.any { it in person.sharingMembershipIds } ?: false
//
//    private fun hasCsffStatic(person: IPerson) = person.hasLicense &&
//        sharingProvidersByMode[modes.carSharingFree]?.any { it in person.sharingMembershipIds } ?: false
//
//    private fun hasPoolingStatic(person: IPerson) = drtProvidersByMode[modes.ridePooling]?.any {
//        it in person.drtMembershipIds
//    } ?: false
//
//    private fun hasBikeSharingStatic(person: IPerson) = sharingProvidersByMode[modes.bikeSharing]?.any {
//        it in person.sharingMembershipIds
//    } ?: false
//
//    // provider availability
//
//    context(person: PersonAgent)
//    private fun isCarCurrentlyAvailable(): ProviderAvailability = takeIf {
//        person.household.cars.isNotEmpty() &&
//            (isHome(person) || (person.lastTransportMode() == modes.car))
//    }?.let {
//        modes.car.available(setOf(person.household))
//    } ?: modes.car.notAvailable
//
//    context(person: PersonAgent, destination: StandardLocation)
//    private fun isBikeSharingCurrentlyAvailable(): ProviderAvailability = takeIf {
//        isHome(person) || prevModeIsFlexible(person)
//    }?.let {
//        person.sharingMemberships.filter {
//            it.id in (sharingProvidersByMode[modes.bikeSharing] ?: emptySet())
//        }.filter {
//            val starts = it.stations.filter { s -> s.zonesByFoot.any { z -> person.location in z } }.toSet()
//            val ends = it.stations.filter { s -> s.zonesByFoot.any { z -> destination in z } }.toSet()
//
//            starts != ends && starts.isNotEmpty() && ends.isNotEmpty() // TODO
//        }.flatMap {
//            it.stations
//        }.filter {
//            it.zonesByFoot.any { zone -> person.location in zone }
//        }.takeIf {
//            it.isNotEmpty()
//        }
//    }?.let {
//        modes.bikeSharing.available(it)
//    } ?: modes.bikeSharing.notAvailable
//
//    context(person: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
//    private fun isPoolingCurrentlyAvailable(): ProviderAvailability = getDrtProvidersCurrentlyOperating().takeIf {
//        it.isNotEmpty()
//    }?.let {
//        modes.ridePooling.available(it)
//    } ?: modes.ridePooling.notAvailable
//
//    context(person: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
//    override fun getDrtProvidersCurrentlyOperating(): List<DrtProviderAgent> = takeIf {
//        isHome(person) || prevModeIsFlexible(person)
//    }?.let {
//        person.drtMemberships.filter {
//            it.id in (drtProvidersByMode[modes.ridePooling] ?: emptySet())
//        }.filter {
//            it.operatesAt(time, person.location, destination)
//        }
//    } ?: emptyList()
//
//    private fun Mode.isFlexModeCurrentlyAvailableI(person: PersonAgent) = takeIf {
//        isHome(person) || prevModeIsFlexible(person)
//    }?.let { available() } ?: notAvailable
//
//    private fun Mode.isFixedModeCurrentlyAvailable(person: PersonAgent) = takeIf {
//        isHome(person) || (person.lastTransportMode() == this)
//    }?.let { available() } ?: notAvailable
//
//    private fun prevModeIsFlexible(person: PersonAgent): Boolean = (
//        person.lastTransportMode()?.requiresVehicleTakeAlong?.not()
//            ?: true
//        )
//
//    private fun isHome(person: PersonAgent): Boolean = person.location == person.household.location
//
//    // Choice availability
//    private fun isPrivateCarAvailableForChoice(characteristics: ModeChoiceCharacteristics) =
//        characteristics.person.getBestCarOrNull() != null
//
//    context(impedance: Impedance)
//    private fun isBikesharingAvailableForChoice(characteristics: ModeChoiceCharacteristics) = findConnection(
//        characteristics.person,
//        characteristics.destination,
//    ) != null
//
//    context(impedance: Impedance)
//    override fun findConnection(
//        person: PersonAgent,
//        destination: StandardLocation,
//    ): Pair<SharingStationAgent, SharingStationAgent>? = context(
//        person,
//        destination,
//    ) {
//        findStartEndStation()
//    }
//
//    context(person: PersonAgent, destination: StandardLocation, impedance: Impedance)
//    private fun findStartEndStation(): Pair<SharingStationAgent, SharingStationAgent>? {
//        val origin = person.location
//
//        val memberships = person.sharingMemberships.filter {
//            it.id in (sharingProvidersByMode[modes.bikeSharing] ?: emptySet())
//        }
//
//        val connections = memberships.mapNotNull { provider ->
//
//            provider.stations.filter {
//                it.isReachableFrom(origin)
//            }.filter {
//                it.hasAvailableVehicles
//            }.sortedBy {
//                impedance.distance(origin, it.location, modes.pedestrian)
//            }.firstNotNullOfOrNull { start ->
//
//                provider.stations.filter {
//                    it.isReachableFrom(destination)
//                }.filter {
//                    it != start
//                }.minByOrNull {
//                    impedance.distance(it.location, destination, modes.pedestrian)
//                }?.let {
//                    start to it
//                }
//            }
//        }
//
//        return connections.minByOrNull { (start, end) ->
//            impedance.distance(origin, start.location, modes.pedestrian) +
//                impedance.distance(start.location, end.location, modes.bikeSharing) +
//                impedance.distance(end.location, destination, modes.pedestrian)
//        }
//    }
//
//    private fun isPoolingAvailableForChoice(characteristics: ModeChoiceCharacteristics) = characteristics.run {
//        context(person, time, destination) {
//            findDrtOffers().isNotEmpty()
//        }
//    }
//
//    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
//    override fun findDrtOffers(): List<DrtOffer> {
//        val memberProviders = agent.drtMemberships.filter {
//            it.id in (drtProvidersByMode[modes.ridePooling] ?: emptySet())
//        }
//
//        return memberProviders.mapNotNull {
//            it.requestRide(
//                DrtRequest(it, agent, time, time, agent.location, destination),
//            )
//        }
//    }
// }
